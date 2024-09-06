package org.menosprezo.discordBot.listeners;

import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.menosprezo.discordBot.DiscordBot;
import org.menosprezo.discordBot.managers.PlayerData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChatListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPermission("chat.staff")) {
            List<String> badwords = DiscordBot.getInstance().getConfig().getStringList("badwords");
            String[] messageWords = e.getMessage().toLowerCase().split(" ");
            if (Arrays.stream(messageWords).anyMatch(badwords::contains)) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', DiscordBot.getInstance().getConfig().getString("messages.badword")));
                e.setCancelled(true);
                return;
            }
        }

        PlayerData data = DiscordBot.getManager().getData(p);

        if (p.hasPermission("chat.color")) {
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        }

        if (data.isInStaffChat() && p.hasPermission("chat.staff")) {
            if (!DiscordBot.getManager().getStaffs().contains(p)) {
                data.setChat("global");
            } else {
                DiscordBot.getManager().getStaffs().stream()
                        .filter(target -> target.hasPermission("chat.staff"))
                        .forEach(target -> target.sendMessage(formatStaffChat(p, e.getMessage())));

                String avatarUrl = "https://mc-heads.net/head/" + p.getName() + "/128.png";
                DiscordBot.getManager().sendToDiscordWithAvatar(p.getName(), e.getMessage(), avatarUrl);
                e.setCancelled(true);
                return;
            }
        }

        if (data.getCooldown().after(new Date())) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', DiscordBot.getInstance().getConfig().getString("messages.cooldown")));
            e.setCancelled(true);
            return;
        }

        if (!p.hasPermission("chat.admin") && !DiscordBot.getInstance().getConfig().getBoolean("chat")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', DiscordBot.getInstance().getConfig().getString("messages.disabled")));
            e.setCancelled(true);
            return;
        }

        String message = formatGlobalChat(p, e.getMessage());
        Bukkit.getOnlinePlayers().forEach(target -> target.sendMessage(message));

        if (!p.hasPermission("chat.bypass")) {
            data.resetCooldown();
        }
    }

    private String formatStaffChat(Player p, String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("NametagEdit")) {
            String prefix = NametagEdit.getApi().getNametag(p).getPrefix();
            return ChatColor.translateAlternateColorCodes('&', DiscordBot.getInstance().getConfig().getString("formats.staff"))
                    .replace("%tag%", prefix)
                    .replace("%author%", p.getDisplayName())
                    .replace("%content%", message);
        }
        return message;
    }

    // Método para formatar mensagem do chat global
    private String formatGlobalChat(Player p, String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("NametagEdit")) {
            String prefix = NametagEdit.getApi().getNametag(p).getPrefix();
            String suffix = NametagEdit.getApi().getNametag(p).getSuffix();
            return ChatColor.translateAlternateColorCodes('&', DiscordBot.getInstance().getConfig().getString("formats.global"))
                    .replace("%tag%", prefix)
                    .replace("%author%", p.getDisplayName())
                    .replace("%suflix%", suffix)
                    .replace("%content%", message);
        }
        return message;
    }
}