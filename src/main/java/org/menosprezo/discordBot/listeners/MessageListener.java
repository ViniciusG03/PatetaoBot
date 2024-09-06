package org.menosprezo.discordBot.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.ChatColor;
import org.menosprezo.discordBot.DiscordBot;
import org.jetbrains.annotations.NotNull;

public class MessageListener implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (!(event instanceof MessageReceivedEvent)) {
            return;
        }

        MessageReceivedEvent msg = (MessageReceivedEvent) event;

        if (msg.getAuthor().isBot()) {
            return;
        }

        String channelId = DiscordBot.getInstance().getConfig().getString("discord.channel");
        if (!msg.getChannel().getId().equals(channelId)) {
            return;
        }

        if (msg.getMessage().getContentDisplay().trim().isEmpty()) {
            return;
        }

        String formattedMessage = ChatColor.translateAlternateColorCodes('&',
                DiscordBot.getInstance().getConfig().getString("formats.staff")
                        .replace("%tag%", "")
                        .replace("%author%", msg.getAuthor().getName())
                        .replace("%content%", msg.getMessage().getContentDisplay()));

        DiscordBot.getManager().getStaffs().stream()
                .filter(player -> player.hasPermission("chat.staff"))
                .forEach(player -> player.sendMessage(formattedMessage));
    }
}