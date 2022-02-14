package lu.r3flexi0n.bungeeonlinetimeexpansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class BungeeOnlineTimeExpansion extends PlaceholderExpansion implements PluginMessageListener, Listener {

    private final HashMap<UUID, Long> onlineTimes = new HashMap<>();

    private final String pluginMessageChannel = "bungeeonlinetime:get";

    @Override
    public @NotNull String getIdentifier() {
        return "onlinetime";
    }

    @Override
    public @NotNull String getAuthor() {
        return "R3fleXi0n";
    }

    @Override
    public @NotNull String getVersion() {
        return "8.2";
    }

    @Override
    public boolean register() {

        Bukkit.getPluginManager().registerEvents(this, this.getPlaceholderAPI());
        Bukkit.getMessenger().registerIncomingPluginChannel(this.getPlaceholderAPI(), "bungeeonlinetime:get", this);

        return super.register();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        if (player == null) {
            return "";
        }

        Long seconds = onlineTimes.get(player.getUniqueId());
        if (seconds == null) {
            return "";
        }

        int value;
        if (params.equalsIgnoreCase("days")) {
            value = (int) (seconds / 86400L);
        } else if (params.equalsIgnoreCase("hours")) {
            value = (int) (seconds / 3600L);
        } else if (params.equalsIgnoreCase("minutes")) {
            value = (int) (seconds / 60L);
        } else {
            return "UNIT ERROR";
        }

        return String.valueOf(value);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] data) {
        if (!channel.equals(pluginMessageChannel)) {
            return;
        }

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream in = new DataInputStream(byteArrayInputStream);
            UUID uuid = UUID.fromString(in.readUTF());
            long time = in.readLong();
            onlineTimes.put(uuid, time);
        } catch (IOException ex) {
            System.out.println("[BungeeOnlineTime] Error while receiving plugin message.");
            ex.printStackTrace();
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        onlineTimes.remove(e.getPlayer().getUniqueId());
    }
}
