package org.menosprezo.discordBot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.menosprezo.discordBot.BotInitializer;

import java.awt.*;
import java.util.Arrays;

public class TicketCommandListener extends ListenerAdapter {

    private final BotInitializer botInitializer;

    public TicketCommandListener(BotInitializer botInitializer) {
        this.botInitializer = botInitializer;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("Comando Slash recebido: " + event.getName());
        if (event.getName().equals("ticket")) {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("PatetaMC", null, botInitializer.getJda().getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("Escolha uma categoria para abrir um ticket de acordo com suas necessidades!\n\n 🔎** | Suporte.\n** **📑 | Appeal.**\n**🚨 | Denúncia.**\n**🛒 | Loja.**")
                    .setColor(Color.CYAN);
            StringSelectMenu selectMenu = StringSelectMenu.create("ticket:category")
                    .setPlaceholder("Selecione uma categoria")
                    .addOption("📢 Appeal", "appeal")
                    .addOption("⛔ Denúncia", "denuncia")
                    .addOption("📚 Suporte", "suporte")
                    .addOption("🛒 Loja", "loja")
                    .build();
            event.replyEmbeds(embedBuilder.build()).addActionRow(selectMenu).queue();
            System.out.println("Mensagem de seleção de categoria enviada.");
        }
    }


    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        System.out.println("Seleção de string recebida: " + event.getSelectedOptions().get(0).getValue());
        if (event.getComponentId().equals("ticket:category")) {
            String category = event.getSelectedOptions().get(0).getValue();
            Guild guild = event.getGuild();

            if (guild != null) {
                System.out.println("Guild encontrada. Tentando criar canal para a categoria: " + category);
                String channelName = "ticket-" + category + "-" + event.getUser().getId();
                Category ticketCategory = guild.getCategoriesByName("Ticket", true).stream().findFirst().orElse(null);

                Member member = guild.getMember(event.getUser());

                if (member == null) {
                    System.out.println("Membro não encontrado no cache. Tentando recuperar...");
                    guild.retrieveMember(event.getUser()).queue(
                            retrievedMember -> {
                                createTicketChannel(retrievedMember, guild, channelName, ticketCategory, category, event);
                            },
                            throwable -> {
                                System.err.println("Erro ao recuperar membro: " + throwable.getMessage());
                                event.reply("Erro ao criar ticket: Não foi possível encontrar o membro.").queue();
                            }
                    );
                } else {
                    createTicketChannel(member, guild, channelName, ticketCategory, category, event);
                }
            } else {
                System.err.println("Guild não encontrada.");
                event.reply("Erro ao criar ticket: Guild não encontrada!").queue();
            }
        }
    }


    private void createTicketChannel(Member member, Guild guild, String channelName, Category ticketCategory, String category, StringSelectInteractionEvent event) {
        System.out.println("Criando canal de ticket: " + channelName);
        Role donoRole = guild.getRoleById("1278839428625858653");

        guild.createTextChannel(channelName)
                .setParent(ticketCategory)
                .addPermissionOverride(member, Arrays.asList(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .addPermissionOverride(guild.getPublicRole(), null, Arrays.asList(Permission.VIEW_CHANNEL))
                .queue(ticketChannel -> {
                    System.out.println("Canal criado: " + ticketChannel.getName());
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setAuthor("PatetaMC", null, botInitializer.getJda().getSelfUser().getEffectiveAvatarUrl())
                            .setDescription("Olá " + member.getUser().getAsMention() + ", seu ticket para a categoria " + category + " foi criado com sucesso!\n\nAguarde que em instantes você será atendido")
                            .setColor(Color.CYAN)
                            .setFooter("PatetaMC", botInitializer.getJda().getSelfUser().getEffectiveAvatarUrl());

                    ticketChannel.sendMessageEmbeds(embedBuilder.build())
                            .addActionRow(Button.danger("closeTicket", "Fechar ticket"))
                            .queue(sentMessage -> {
                                System.out.println("Mensagem de embed enviada para o canal.");
                                ticketChannel.sendMessage(member.getUser().getAsMention() + donoRole.getAsMention())
                                        .queue(mentionMessage -> {
                                            System.out.println("Mensagem de menção enviada. Agendando exclusão.");
                                            new java.util.Timer().schedule(new java.util.TimerTask() {
                                                @Override
                                                public void run() {
                                                    mentionMessage.delete().queue();
                                                    System.out.println("Mensagem de menção excluída.");
                                                }
                                            }, 1000);
                                        });

                                if (donoRole != null) {
                                    ticketChannel.getManager().putPermissionOverride(donoRole, Arrays.asList(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();
                                    System.out.println("Permissões do cargo 'dono' definidas.");
                                } else {
                                    System.err.println("Erro: O cargo 'dono' não foi encontrado no servidor.");
                                    event.reply("Erro: O cargo 'dono' não foi encontrado no servidor.").queue();
                                }
                            });

                    event.reply("Seu ticket para a categoria " + category + " foi criado com sucesso!").setEphemeral(true).queue();
                });
    }



    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("closeTicket")) {
            MessageChannel channel = event.getChannel();

            if (channel != null) {
                channel.delete().queue(
                        success -> System.out.println("Canal de ticket fechado com sucesso!"),
                        error -> System.err.println("Erro ao fechar canal de ticket: " + error.getMessage())
                );
            } else {
                System.err.println("Canal não encontrado.");
            }
        }
    }
}
