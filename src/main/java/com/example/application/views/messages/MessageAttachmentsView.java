package com.example.application.views.messages;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.messages.MessageListItem.Attachment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;

@PageTitle("Message attachments")
@Route("message-attachments")
@PermitAll
@Menu(order = 17, icon = LineAwesomeIconUrl.PAPERCLIP_SOLID)
public class MessageAttachmentsView extends VerticalLayout {

    public MessageAttachmentsView() {
        addClassName("message-attachments-view");
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("MessageList attachments (stable in 25.2)");
        Paragraph subtitle = new Paragraph(
                "MessageListItem now exposes setAttachments(List<Attachment>) with the Attachment(name, url, mimeType) record. "
                        + "Attachments render below the message text; images preview inline while other MIME types render as a download link.");
        subtitle.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);
        add(title, subtitle);

        MessageList list = new MessageList();
        list.setWidthFull();

        Instant now = Instant.now();
        MessageListItem alice = new MessageListItem(
                "Here is the floor plan we discussed.",
                now.minus(15, ChronoUnit.MINUTES),
                "Alice");
        alice.setAttachments(List.of(
                new Attachment("floor-plan.png", "/images/empty-plant.png", "image/png"),
                new Attachment("notes.txt", "data:text/plain;charset=utf-8,Living%20room%20goes%20on%20the%20south%20wall.",
                        "text/plain")));

        MessageListItem bob = new MessageListItem(
                "Looks great. I attached the brochure too.",
                now.minus(2, ChronoUnit.MINUTES),
                "Bob");
        bob.setAttachments(List.of(
                new Attachment("brochure.pdf",
                        "data:application/pdf;base64,JVBERi0xLjQKJfbk/N8KMSAwIG9iaiA8PC9UeXBlL0NhdGFsb2c+PiBlbmRvYmoKMiAwIG9iaiA8PC9MZW5ndGggMz4+IHN0cmVhbQpoaQplbmRzdHJlYW0gZW5kb2JqCnRyYWlsZXIgPDwvUm9vdCAxIDAgUj4+CiUlRU9G",
                        "application/pdf")));

        list.setItems(List.of(alice, bob));
        add(list);
    }
}
