package com.example.application.views.aichat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.voiceengine.VoiceEngine;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
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
    
    

    public AiChatView(ChatClient.Builder chatClientBuilder, @Value("${spring.ai.openai.apikey}") String apikey) {
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
        
        button.addClickShortcut(Key.ENTER);

        voiceEngine.addEndListener(e -> {
            textField.setValue(voiceEngine.getRecorded());
            button.click();
        });
        this.setSizeFull();
        textArea.setSizeFull();
        question.setWidthFull();
        textField.setWidthFull();
        
        // If there is no openAI key, we cannot continue
        if (apikey == null) {
        	textArea.setValue("$OPENAI_API_KEY environent variable is not propertly set.");
        	button.setEnabled(false);
        }

        add(question, textArea);

    }
}
