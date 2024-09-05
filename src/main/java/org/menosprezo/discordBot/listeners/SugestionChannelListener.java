package org.menosprezo.discordBot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SugestionChannelListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Ignora mensagens de bots
        if (event.getAuthor().isBot()) return;

        // Verifica se a mensagem foi enviada no canal correto
        if (event.getChannel().getIdLong() == 1279257254536806502L) {
            // Cria o embed de sugestão
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Sugestão de " + event.getAuthor().getName());
            embed.setDescription("a");
            embed.setFooter("ID: " + event.getAuthor().getId());

            // Verifica se o autor tem avatar
            if (event.getAuthor().getAvatarUrl() != null) {
                embed.setThumbnail(event.getAuthor().getAvatarUrl());
            }

            // Obtém o canal de sugestões, tentando recuperar do cache
            TextChannel suggestionChannel = event.getGuild().getTextChannelById(1279257254536806503L);

            if (suggestionChannel != null) {
                // Canal encontrado
                suggestionChannel.sendMessageEmbeds(embed.build()).queue(
                        success -> System.out.println("Embed enviado com sucesso!"),
                        error -> System.err.println("Erro ao enviar o embed: " + error.getMessage())
                );
            } else {
                // Se o canal não estiver no cache, exibe uma mensagem de erro
                event.getChannel().sendMessage("Erro: O canal de sugestões não foi encontrado!").queue();
            }

            // Adiciona reações para votação
            event.getMessage().addReaction(Emoji.fromFormatted("✅")).queue();
            event.getMessage().addReaction(Emoji.fromFormatted("❌")).queue();
        }
    }
}