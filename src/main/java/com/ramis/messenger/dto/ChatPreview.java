package com.ramis.messenger.dto;

import lombok.*;

@Getter
@Setter
public class ChatPreview {
    private Long id;
    private String name;
    private String lastMessage;
    private String lastMessagePreview;
    private String lastMessageSender;

    public ChatPreview(Long id, String name, String lastMessage, String lastMessageSender) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageSender = lastMessageSender;
        this.lastMessagePreview = formatPreview(lastMessage);
    }

    private String formatPreview(String lastMessage) {
        if (lastMessage == null) {
            return "";
        }
        return lastMessage.length() > 50 ? lastMessage.substring(0, 47) + "..." : lastMessage;
    }
}
