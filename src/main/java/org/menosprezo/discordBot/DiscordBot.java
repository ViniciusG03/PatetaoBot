package org.menosprezo.discordBot;

import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordBot extends JavaPlugin {

    private BotInitializer botInitializer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        botInitializer = new BotInitializer();
        botInitializer.startBot(this);
        getLogger().info("Discord bot started!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (botInitializer != null) {
            botInitializer.stopBot();
        }
    }
}
