package org.menosprezo.discordBot.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class WelcomeListener extends ListenerAdapter {

    private static final long WELCOME_CHANNEL_ID = 1276208321543540768L;
    private static final long EMOTE_ID = 1279473775334789151L;
    private static final long ROLE_ID = 1280283605243002921L;

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        System.out.println("Novo membro: " + event.getMember().getUser().getName());

        Member member = event.getMember();
        guild.addRoleToMember(member, guild.getRoleById(ROLE_ID)).queue(
                success -> System.out.println("Role adicionada com sucesso."),
                error -> System.err.println("Erro ao adicionar role: " + error.getMessage())
        );

        TextChannel welcomeChannel = guild.getTextChannelById(WELCOME_CHANNEL_ID);

        if (welcomeChannel != null) {
            String welcomeMessage = "Bem-vindo ao servidor, " + event.getMember().getAsMention() + " Tmj cachorro!";
            welcomeChannel.sendMessage(welcomeMessage).queue(message -> {
                CustomEmoji customEmoji = guild.getEmojiById(EMOTE_ID);

                if (customEmoji == null) {
                    System.err.println("Emoji não encontrado!");
                    return;
                }
                message.addReaction(Emoji.fromCustom(customEmoji)).queue();
            });

        } else {
            System.err.println("Canal de boas-vindas não encontrado!");
        }
    }
}