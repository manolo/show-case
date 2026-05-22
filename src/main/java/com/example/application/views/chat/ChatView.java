package com.example.application.views.chat;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationMessageInput;
import com.vaadin.collaborationengine.CollaborationMessageList;
import com.vaadin.collaborationengine.MessageManager;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Aside;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import java.util.UUID;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Chat")
@Route("chat")
@PermitAll
@Menu(order = 13, icon = LineAwesomeIconUrl.COMMENTS)
public class ChatView extends HorizontalLayout {

    public static class ChatTab extends Tab {
        private final ChatInfo chatInfo;

        public ChatTab(ChatInfo chatInfo) {
            this.chatInfo = chatInfo;
        }

        public ChatInfo getChatInfo() {
            return chatInfo;
        }
    }

    public static class ChatInfo {
        private final String name;
        private final ValueSignal<Integer> unread = new ValueSignal<>(0);

        private ChatInfo(String name) {
            this.name = name;
        }

        public void resetUnread() {
            unread.set(0);
        }

        public void incrementUnread() {
            unread.update(u -> u + 1);
        }

        public ValueSignal<Integer> unreadSignal() {
            return unread;
        }

        public String getCollaborationTopic() {
            return "chat/" + name;
        }
    }

    private final ChatInfo[] chats = new ChatInfo[]{new ChatInfo("general"), new ChatInfo("support"),
            new ChatInfo("casual")};
    private ChatInfo currentChat = chats[0];
    private final Tabs tabs;

    public ChatView() {
        addClassNames("chat-view", Width.FULL, Display.FLEX, Flex.AUTO);
        setSpacing(false);

        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        tabs = new Tabs();
        for (ChatInfo chat : chats) {
            MessageManager mm = new MessageManager(this, userInfo, chat.getCollaborationTopic());
            mm.setMessageHandler(context -> {
                if (currentChat != chat) {
                    chat.incrementUnread();
                }
            });

            tabs.add(createTab(chat));
        }
        tabs.setOrientation(Orientation.VERTICAL);
        tabs.addClassNames(Flex.GROW, Flex.SHRINK, Overflow.HIDDEN);

        CollaborationMessageList list = new CollaborationMessageList(userInfo, currentChat.getCollaborationTopic());
        list.setSizeFull();

        CollaborationMessageInput input = new CollaborationMessageInput(list);
        input.setWidthFull();

        VerticalLayout chatContainer = new VerticalLayout();
        chatContainer.addClassNames(Flex.AUTO, Overflow.HIDDEN);

        Aside side = new Aside();
        side.addClassNames(Display.FLEX, FlexDirection.COLUMN, Flex.GROW_NONE, Flex.SHRINK_NONE, Background.CONTRAST_5);
        side.setWidth("18rem");
        Header header = new Header();
        header.addClassNames(Display.FLEX, FlexDirection.ROW, Width.FULL, AlignItems.CENTER, Padding.MEDIUM,
                BoxSizing.BORDER);
        H3 channels = new H3("Channels");
        channels.addClassNames(Flex.GROW, Margin.NONE);
        CollaborationAvatarGroup avatarGroup = new CollaborationAvatarGroup(userInfo, "chat");
        avatarGroup.setMaxItemsVisible(4);
        avatarGroup.addClassNames(Width.AUTO);

        header.add(channels, avatarGroup);

        side.add(header, tabs);

        chatContainer.add(list, input);
        add(chatContainer, side);
        setSizeFull();
        expand(list);

        tabs.addSelectedChangeListener(event -> {
            currentChat = ((ChatTab) event.getSelectedTab()).getChatInfo();
            currentChat.resetUnread();
            list.setTopic(currentChat.getCollaborationTopic());
        });
    }

    private ChatTab createTab(ChatInfo chat) {
        ChatTab tab = new ChatTab(chat);
        tab.addClassNames(JustifyContent.BETWEEN);

        Span badge = new Span();
        badge.getElement().getThemeList().add("badge small contrast");
        badge.bindText(chat.unreadSignal().map(String::valueOf));
        badge.bindVisible(chat.unreadSignal().map(u -> u != 0));
        tab.add(new Span("#" + chat.name), badge);

        return tab;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        Signal.effect(tabs, () -> {
            var size = attachEvent.getUI().getPage().windowSizeSignal().get();
            tabs.setOrientation(size != null && size.width() < 740 ? Orientation.HORIZONTAL : Orientation.VERTICAL);
        });
    }

}
