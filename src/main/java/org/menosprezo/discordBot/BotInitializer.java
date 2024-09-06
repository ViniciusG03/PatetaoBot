package org.menosprezo.discordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.menosprezo.discordBot.commands.SlashCommandListener;
import org.menosprezo.discordBot.commands.TicketCommandListener;
import org.menosprezo.discordBot.listeners.MessageListener;
import org.menosprezo.discordBot.listeners.SlashCommandRegisterer;
import org.menosprezo.discordBot.listeners.SuggestionListener;
import org.menosprezo.discordBot.listeners.WelcomeListener;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BotInitializer extends Thread {

    private final WelcomeListener welcomeListener = new WelcomeListener();
    private final SlashCommandListener slashCommandListener = new SlashCommandListener(this);
    private final SlashCommandRegisterer slashCommandRegisterer = new SlashCommandRegisterer();
    private final TicketCommandListener ticketCommandListener = new TicketCommandListener(this);
    private final SuggestionListener suggestionListener = new SuggestionListener();
    private final MessageListener messageListener = new MessageListener();

    private static final List<String> MESSAGES = Arrays.asList(
            "Vem com o patetão😎", "O resto é resto!", "pateta.xyz", "🤫"
    );
    private static final int UPDATE_INTERVAL = 3000;

    private String token;
    private JDA jda;
    private final JavaPlugin plugin;

    public BotInitializer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            JDALogger.setFallbackLoggerEnabled(false);
            token = plugin.getConfig().getString("discord.token");

            jda = JDABuilder.createDefault(token)
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.GUILD_WEBHOOKS,
                            GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS
                    )
                    .addEventListeners(welcomeListener)
                    .addEventListeners(slashCommandListener)
                    .addEventListeners(slashCommandRegisterer)
                    .addEventListeners(ticketCommandListener)
                    .addEventListeners(suggestionListener)
                    .addEventListeners(messageListener)
                    .build();

            jda.awaitReady();
            jda.getPresence().setStatus(OnlineStatus.IDLE);

            // Iniciar o atualizador de status
            startStatusUpdater();

            plugin.getLogger().info("Bot Discord pronto!");

        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao iniciar o bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startStatusUpdater() {
        Timer timer = new Timer(true);
        TimerTask statusUpdater = new TimerTask() {
            private int currentIndex = 0;

            @Override
            public void run() {
                if (jda != null) {
                    String activityMessage;

                    if (currentIndex == MESSAGES.size()) {
                        int playerCount = Bukkit.getOnlinePlayers().size();
                        activityMessage = playerCount + " jogadores online";
                    } else {
                        activityMessage = MESSAGES.get(currentIndex);
                    }

                    Activity activity = Activity.playing(activityMessage);
                    jda.getPresence().setActivity(activity);

                    currentIndex = (currentIndex + 1) % (MESSAGES.size() + 1);
                }
            }
        };
        timer.scheduleAtFixedRate(statusUpdater, 0, UPDATE_INTERVAL);
    }

    public void stopBot() {
        if (jda != null) {
            jda.shutdown();
            plugin.getLogger().info("Bot Discord desligado.");
        } else {
            plugin.getLogger().warning("JDA está nulo. Não foi possível desligar o bot.");
        }
    }

    public JDA getJda() {
        return jda;
    }
}