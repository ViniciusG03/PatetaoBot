package org.menosprezo.discordBot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.menosprezo.discordBot.BotInitializer;

public class SlashCommandRegisterer extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Guild guild = event.getJDA().getGuildById("1276208320990023702");

        if (guild != null) {
          guild.updateCommands().addCommands(
                  Commands.slash("ip", "Pega o ip do server!"),
                  Commands.slash("ticket", "Cria um menu de categorias de ticket")
                          .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                  Commands.slash("pix", "Mostra o pix do servidor!")
          ).queue(
                  sucess -> System.out.println("Comandos registrados com sucesso!"),
                  error -> System.err.println("Erro ao registrar comandos: " + error.getMessage())
          );

        } else {
            System.err.println("Guild not found!");
        }

        event.getJDA().updateCommands().addCommands(
                Commands.slash("help", "Mostra os comandos disponíveis!")
        ).queue(
                success -> System.out.println("Comando global 'help' registrado com sucesso!"),
                error -> System.err.println("Erro ao registrar comando global 'help': " + error.getMessage())
        );
    }
}
