package org.menosprezo.discordBot;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.menosprezo.discordBot.commands.ChatCommand;
import org.menosprezo.discordBot.commands.StaffChatCommand;
import org.menosprezo.discordBot.listeners.ChatListener;
import org.menosprezo.discordBot.listeners.JoinListener;
import org.menosprezo.discordBot.managers.ChatManager;
import org.menosprezo.discordBot.managers.PlayerData;

public final class DiscordBot extends JavaPlugin {

    private static DiscordBot instance;
    private static ChatManager manager;
    private BotInitializer botInitializer;

    public static DiscordBot getInstance() {
        return instance;
    }

    public static ChatManager getManager() {
        return manager;
    }

    public static void setManager(ChatManager manager) {
        DiscordBot.manager = manager;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        manager = new ChatManager();

        // Integrando o bot Discord no Chat
        botInitializer = new BotInitializer(this);
        botInitializer.start();

        // Registrando comandos e eventos
        getCommand("staffchat").setExecutor((CommandExecutor) new StaffChatCommand());
        getCommand("chat").setExecutor((CommandExecutor) new ChatCommand());
        Bukkit.getPluginManager().registerEvents((Listener) new ChatListener(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents((Listener) new JoinListener(), (Plugin) this);
    }

    @Override
    public void onDisable() {
        // Salvando dados e interrompendo o bot
        if (getManager() != null) {
            getManager().getPlayers().values().forEach(PlayerData::save);
        }
        if (botInitializer != null) {
            botInitializer.stopBot();
        }
        saveConfig();
    }

    public BotInitializer getBotInitializer() {
        return botInitializer;
    }

    public void setBotInitializer(BotInitializer botInitializer) {
        this.botInitializer = botInitializer;
    }
}

