package org.menosprezo.discordBot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.menosprezo.discordBot.DiscordBot;

public class StaffChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando está disponível apenas para jogadores.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("chat.staff")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    DiscordBot.getInstance().getConfig().getString("messages.permission")));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("toggle")) {
            if (DiscordBot.getManager().getStaffs().contains(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        DiscordBot.getInstance().getConfig().getString("messages.chat.receiveDisabled")));
                DiscordBot.getManager().getStaffs().remove(player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        DiscordBot.getInstance().getConfig().getString("messages.chat.receiveEnabled")));
                DiscordBot.getManager().getStaffs().add(player);
            }
            return true;
        }

        if (DiscordBot.getManager().getData(player).isInStaffChat()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    DiscordBot.getInstance().getConfig().getString("messages.chat.global")));
            DiscordBot.getManager().getData(player).setChat("global");
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    DiscordBot.getInstance().getConfig().getString("messages.chat.staff")));
            DiscordBot.getManager().getData(player).setChat("staff");

            if (!DiscordBot.getManager().getStaffs().contains(player)) {
                DiscordBot.getManager().getStaffs().add(player);
            }
        }
        return true;
    }
}