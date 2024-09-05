package org.menosprezo.discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.menosprezo.discordBot.BotInitializer;

import java.awt.*;

public class SlashCommandListener extends ListenerAdapter {

    private static final long MENOSPREZO_ID = 905475185560399893l;
    private final BotInitializer botInitializer;

    public SlashCommandListener(BotInitializer botInitializer) {
        this.botInitializer = botInitializer;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("ip")) {
            event.reply("pateta.xyz").queue();
        }

        if (event.getName().equals("pix")) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setAuthor("PatetaMC", null, botInitializer.getJda().getSelfUser().getEffectiveAvatarUrl())
                    .setImage("https://cdn.discordapp.com/attachments/1279164582077661184/1279952955025522730/Sem_titulo.png?ex=66d65094&is=66d4ff14&hm=1e4f6c1ac33fe541863e2f1437874a9e008473609a23fa7b2467f0557f241e09&")
                    .setDescription("Email: eopix@pateta.xyz")
                    .setColor(Color.CYAN);
            event.replyEmbeds(builder.build()).queue();
        }

        Guild guild = event.getGuild();

        if (guild != null) {
            guild.retrieveMemberById(MENOSPREZO_ID).queue(member -> {
                        if (member != null) {
                            String avatarUrl = member.getUser().getEffectiveAvatarUrl();

                            if (event.getName().equals("help")) {
                                EmbedBuilder builder = new EmbedBuilder()
                                        .setAuthor("PatetaMC", null, botInitializer.getJda().getSelfUser().getEffectiveAvatarUrl())
                                        .setDescription("Comandos disponíveis:\n\n/ip - Pega o ip do servidor\n/pix - Mostra o pix do servidor")
                                        .setFooter("Desenvolvido por Menosprezo", avatarUrl)
                                        .setColor(Color.CYAN);
                                event.replyEmbeds(builder.build()).queue();
                            }

                        } else {
                            System.err.println("Membro não encontrado!");
                        }
                    },
                    error -> System.err.println("Erro ao recuperar membro: " + error.getMessage())
            );
        } else {
            System.err.println("Guild não encontrada!");
        }


    }
}
