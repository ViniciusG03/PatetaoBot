package org.menosprezo.discordBot.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.menosprezo.discordBot.DiscordBot;

import java.util.Date;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;

    private Player player;

    private String chat;

    private boolean staff;

    private Date cooldown;

    public PlayerData(UUID uuid, Player player, String chat, boolean staff, Date cooldown) {
        this.uuid = uuid;
        this.player = player;
        this.chat = chat;
        this.staff = staff;
        this.cooldown = cooldown;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    public void setCooldown(Date cooldown) {
        this.cooldown = cooldown;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getChat() {
        return this.chat;
    }

    public boolean isStaff() {
        return this.staff;
    }

    public Date getCooldown() {
        return this.cooldown;
    }

    public PlayerData(String uuid) {
        if (!DiscordBot.getInstance().getConfig().contains("data." + uuid))
            return;
        this.uuid = UUID.fromString(uuid);
        this.player = Bukkit.getPlayer(this.uuid);
        this.chat = DiscordBot.getInstance().getConfig().getString("data." + uuid + ".chat");
        this.staff = DiscordBot.getInstance().getConfig().getBoolean("data." + uuid + ".staff");
        this.cooldown = new Date();
    }

    public boolean isInStaffChat() {
        return this.chat.equals("staff");
    }

    public void save() {
        DiscordBot.getInstance().getConfig().set("data." + this.uuid.toString() + ".chat", this.chat);
        DiscordBot.getInstance().getConfig().set("data." + this.uuid.toString() + ".staff", Boolean.valueOf(this.staff));
    }

    public void resetCooldown() {
        this.cooldown = new Date(System.currentTimeMillis() + DiscordBot.getInstance().getConfig().getLong("cooldown") * 1000L);
    }
}
