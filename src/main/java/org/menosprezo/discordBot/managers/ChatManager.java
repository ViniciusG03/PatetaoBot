package org.menosprezo.discordBot.managers;

import okhttp3.*;
import org.bukkit.entity.Player;
import org.menosprezo.discordBot.DiscordBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ChatManager {

    private final String webhookUrl;
    private final HashMap<UUID, PlayerData> players = new HashMap<>();
    private final ArrayList<Player> staffs = new ArrayList<>();

    public ChatManager() {
        this.webhookUrl = DiscordBot.getInstance().getConfig().getString("discord.webhook");
    }

    public void sendToDiscord(String username, String content) {
        OkHttpClient client = new OkHttpClient();


        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("content", content)
                .build();

        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Erro ao enviar mensagem para o Discord. Código de resposta: " + response.code() + " - " + response.message());
                } else {
                    System.out.println("Mensagem enviada com sucesso para o Discord.");
                }
                response.close();
            }
        });
    }

    public void sendToDiscordWithAvatar(String username, String content, String avatarUrl) {
        OkHttpClient client = new OkHttpClient();

        String json = "{\"username\": \"" + username + "\", " +
                "\"avatar_url\": \"" + avatarUrl + "\", " +
                "\"content\": \"" + content + "\"}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Erro ao enviar mensagem para o Discord. Código de resposta: " + response.code() + " - " + response.message());
                } else {
                    System.out.println("Mensagem enviada com sucesso para o Discord.");
                }
                response.close();
            }
        });
    }


    public PlayerData getData(Player p) {
        if (this.players.containsKey(p.getUniqueId())) {
            return this.players.get(p.getUniqueId());
        }

        if (DiscordBot.getInstance().getConfig().contains("data." + p.getUniqueId().toString())) {
            PlayerData playerData = new PlayerData(p.getUniqueId().toString());
            this.players.put(p.getUniqueId(), playerData);
            return playerData;
        }

        PlayerData data = new PlayerData(p.getUniqueId(), p, "global", p.hasPermission("staff.re"), new Date());
        this.players.put(p.getUniqueId(), data);
        return data;
    }

    public HashMap<UUID, PlayerData> getPlayers() {
        return this.players;
    }

    public ArrayList<Player> getStaffs() {
        return this.staffs;
    }
}

