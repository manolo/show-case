package com.example.application.views.aichat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.voiceengine.VoiceEngine;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.annotation.security.PermitAll;

@Route("aichat")
@Menu(title = "Chat AI")
@PermitAll
public class AiChatView extends VerticalLayout {

    private final TextField textField = new TextField();
    private final Button button = new Button("Ask");
    private final TextArea textArea = new TextArea();

    private final ValueSignal<String> question = new ValueSignal<>("");

    public AiChatView(ChatClient.Builder chatClientBuilder, @Value("${spring.ai.openai.apikey}") String apikey) {
        ChatClient chatClient = chatClientBuilder.build();
        VoiceEngine voiceEngine = new VoiceEngine().setButtons(VoiceEngine.Buttons.MICROPHONE, VoiceEngine.Buttons.PLAY,
                VoiceEngine.Buttons.CANCEL, VoiceEngine.Buttons.LANG, VoiceEngine.Buttons.VOICE);

        textField.bindValue(question, question::set);
        button.bindEnabled(Signal.computed(() -> apikey != null && !question.get().isBlank()));

        HorizontalLayout questionRow = new HorizontalLayout(textField, button, voiceEngine);
        UI ui = UI.getCurrent();
        button.addClickListener(e -> {
            textArea.clear();
            chatClient.prompt().user(question.peek()).stream().content().subscribe(token -> {
                ui.access(() -> {
                    textArea.setValue(textArea.getValue() + token);
                });
            }, null, () -> {
                ui.access(() -> {
                    voiceEngine.play(textArea.getValue());
                });
            });
        });

        button.addClickShortcut(Key.ENTER);

        voiceEngine.addEndListener(e -> {
            question.set(voiceEngine.getRecorded());
            button.click();
        });
        this.setSizeFull();
        textArea.setSizeFull();
        questionRow.setWidthFull();
        textField.setWidthFull();

        if (apikey == null) {
            textArea.setValue("$OPENAI_API_KEY environent variable is not propertly set.");
        }

        add(questionRow, textArea);

    }
}
