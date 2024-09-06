package org.menosprezo.discordBot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.menosprezo.discordBot.DiscordBot;
import org.menosprezo.discordBot.managers.PlayerData;

public class JoinListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPermission("chat.staff")) {
            return;
        }

        PlayerData data = DiscordBot.getManager().getData(e.getPlayer());

        if (data.isStaff() && !DiscordBot.getManager().getStaffs().contains(e.getPlayer())) {
            DiscordBot.getManager().getStaffs().add(e.getPlayer());
        }
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent e) {
        DiscordBot.getManager().getStaffs().remove(e.getPlayer());
    }
}
