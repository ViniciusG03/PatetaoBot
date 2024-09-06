package org.menosprezo.discordBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class SuggestionListener extends ListenerAdapter {

    private final String SUGGESTION_CHANNEL_ID = "1279257254536806502";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild()
                && !event.getAuthor().isBot()
                && event.getChannel().getId().equals(SUGGESTION_CHANNEL_ID)
                && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            Message message = event.getMessage();
            User user = event.getAuthor();
            String suggestion = message.getContentDisplay();

            EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("**Sugestão**");
                    embedBuilder.setDescription(suggestion);
                    embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());
                    embedBuilder.setFooter("Enviado por " + user.getName(), user.getEffectiveAvatarUrl());
                    embedBuilder.setColor(Color.CYAN);

                    event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue(sentMessage -> {
                        sentMessage.addReaction(Emoji.fromFormatted("✅")).queue();
                        sentMessage.addReaction(Emoji.fromFormatted("❌")).queue();
                    });
                    message.delete().queue();
        }
    }
}