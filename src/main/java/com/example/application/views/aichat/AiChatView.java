package com.example.application.views.aichat;

import java.util.Locale;

import org.springframework.ai.chat.client.ChatClient;
import org.vaadin.voiceengine.VoiceEngine;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;

@Route(value = "aichat", layout = MainLayout.class)
@Menu(title = "Chat AI")
public class AiChatView extends VerticalLayout {

    private TextField textField = new TextField();
    private Button button = new Button("Ask");
    private TextArea textArea = new TextArea();

    public AiChatView(ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        VoiceEngine voiceEngine = new VoiceEngine().setButtons(VoiceEngine.Buttons.MICROPHONE, VoiceEngine.Buttons.PLAY,
                VoiceEngine.Buttons.CANCEL, VoiceEngine.Buttons.LANG, VoiceEngine.Buttons.VOICE);

        HorizontalLayout question = new HorizontalLayout(textField, button, voiceEngine);
        UI ui = UI.getCurrent();
        button.addClickListener(e -> {
            textArea.clear();
            chatClient.prompt().user(textField.getValue()).stream().content().subscribe(token -> {
                ui.access(() -> {
                    textArea.setValue(textArea.getValue() + token);
                });
            }, null, () -> {
                ui.access(() -> {
                    voiceEngine.play(textArea.getValue());
                });
            });
        });

        voiceEngine.addEndListener(e -> {
            textField.setValue(voiceEngine.getRecorded());
            button.click();
        });
        this.setSizeFull();
        textArea.setSizeFull();
        question.setWidthFull();
        textField.setWidthFull();

        add(question, textArea);

    }
}
