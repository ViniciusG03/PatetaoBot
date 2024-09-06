package org.menosprezo.discordBot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player sender;
    private final String message;
    private final String displayMessage;

    public ChatEvent(Player sender, String message, String displayMessage) {
        this.sender = sender;
        this.message = message;
        this.displayMessage = displayMessage;
    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
