package org.menosprezo.discordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.menosprezo.discordBot.commands.SlashCommandListener;
import org.menosprezo.discordBot.commands.TicketCommandListener;
import org.menosprezo.discordBot.listeners.SlashCommandRegisterer;
import org.menosprezo.discordBot.listeners.SugestionChannelListener;
import org.menosprezo.discordBot.listeners.WelcomeListener;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BotInitializer {

    private final WelcomeListener welcomeListener = new WelcomeListener();
    private final SlashCommandListener slashCommandListener = new SlashCommandListener(this);
    private final SlashCommandRegisterer slashCommandRegisterer = new SlashCommandRegisterer();
    private final TicketCommandListener ticketCommandListener = new TicketCommandListener(this);
    private final SugestionChannelListener sugestionChannelListener = new SugestionChannelListener();

    private static final List<String> MESSAGES = Arrays.asList(
      "Vem com o patetão😎 ", "O resto é resto!", "pateta.xyz", "🤫"
    );

    private static final int UPDATE_INTERVAL = 3000;

    private String token;

    private JDA jda;

    public void startBot(JavaPlugin plugin) {
        try {
            token = plugin.getConfig().getString("bot.token");

            jda = JDABuilder.createDefault(token)
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.GUILD_WEBHOOKS,
                            GatewayIntent.GUILD_INVITES
                    )
                    .addEventListeners(welcomeListener)
                    .addEventListeners(slashCommandListener)
                    .addEventListeners(slashCommandRegisterer)
                    .addEventListeners(ticketCommandListener)
                    .addEventListeners(sugestionChannelListener)
                    .build();
            jda.awaitReady();

            jda.getPresence().setStatus(OnlineStatus.IDLE);

            if (jda == null) {
                plugin.getLogger().severe("Error starting bot: JDA is null");
                return;
            }

            startStatusUpdater();

            plugin.getLogger().info("Bot is ready!");
        } catch (Exception e) {
            plugin.getLogger().severe("Error starting bot: " + e.getMessage());
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
        } else {
            System.out.println("JDA is null");
        }
    }

    public JDA getJda() {
        return jda;
    }
}
