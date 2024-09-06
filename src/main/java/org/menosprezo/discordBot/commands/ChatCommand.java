package org.menosprezo.discordBot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.menosprezo.discordBot.BotInitializer;
import org.menosprezo.discordBot.DiscordBot;
import org.menosprezo.discordBot.managers.ChatManager;
import org.menosprezo.discordBot.managers.PlayerData;

public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando está disponível apenas para jogadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("chat.reload")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        DiscordBot.getInstance().getConfig().getString("messages.permission")));
                return true;
            }


            DiscordBot.getInstance().getBotInitializer().stopBot();
            DiscordBot.getManager().getPlayers().values().forEach(PlayerData::save);
            DiscordBot.getInstance().reloadConfig();

            DiscordBot.setManager(new ChatManager());
            BotInitializer botInitializer = new BotInitializer(DiscordBot.getInstance());
            DiscordBot.getInstance().setBotInitializer(botInitializer);
            botInitializer.start();

            player.sendMessage(ChatColor.GREEN + "Configurações recarregadas com sucesso.");
            return true;
        }

        if (!player.hasPermission("chat.toggle")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    DiscordBot.getInstance().getConfig().getString("messages.permission")));
            return true;
        }

        boolean chatEnabled = DiscordBot.getInstance().getConfig().getBoolean("chat");
        DiscordBot.getInstance().getConfig().set("chat", !chatEnabled);
        DiscordBot.getInstance().saveConfig();

        if (chatEnabled) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    DiscordBot.getInstance().getConfig().getString("messages.chat.disabled")));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    DiscordBot.getInstance().getConfig().getString("messages.chat.enabled")));
        }

        return true;
    }

}

