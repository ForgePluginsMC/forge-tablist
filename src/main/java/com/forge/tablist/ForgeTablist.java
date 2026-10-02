package com.forge.tablist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * ForgeTablist — animated tablist header/footer, per-group tab names,
 * and an animated server-list MOTD. All text is MiniMessage.
 */
public final class ForgeTablist extends JavaPlugin {

    private final List<List<String>> headerFrames = new ArrayList<>();
    private final List<List<String>> footerFrames = new ArrayList<>();
    private final List<List<String>> motdFrames = new ArrayList<>();
    private final List<NameGroup> nameGroups = new ArrayList<>();

    private long frameIntervalTicks = 60L;
    private long motdIntervalSeconds = 5L;
    private int motdMaxPlayers = -1;

    private int frameIndex = 0;
    private BukkitTask tablistTask;

    /** A tab-name style. Empty permission matches every player (fallback group). */
    public record NameGroup(String permission, String prefix, String suffix) {}

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(new TablistListener(this), this);
        var command = getCommand("ftablist");
        if (command != null) {
            command.setExecutor(new TablistCommand(this));
        }
        startTasks();
        applyToAll();
        getLogger().info("ForgeTablist 1.0.0 enabled.");
    }

    @Override
    public void onDisable() {
        if (tablistTask != null) {
            tablistTask.cancel();
            tablistTask = null;
        }
    }

    /** Reload config from disk and re-apply everything live. */
    public void reloadSettings() {
        reloadConfig();
        if (tablistTask != null) {
            tablistTask.cancel();
            tablistTask = null;
        }
        frameIndex = 0;
        loadSettings();
        startTasks();
        applyToAll();
    }

    private void loadSettings() {
        headerFrames.clear();
        footerFrames.clear();
        motdFrames.clear();
        nameGroups.clear();

        headerFrames.addAll(readFrames("header-frames"));
        footerFrames.addAll(readFrames("footer-frames"));
        motdFrames.addAll(readFrames("motd-frames"));

        long intervalSeconds = getConfig().getLong("frame-interval-seconds", 3L);
        frameIntervalTicks = Math.max(1L, intervalSeconds) * 20L;
        motdIntervalSeconds = Math.max(1L, getConfig().getLong("motd-interval-seconds", 5L));
        motdMaxPlayers = getConfig().getInt("motd-max-players", -1);

        for (Map<?, ?> entry : getConfig().getMapList("groups")) {
            nameGroups.add(new NameGroup(
                    str(entry, "permission"),
                    str(entry, "prefix"),
                    str(entry, "suffix")));
        }
    }

    private static String str(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private List<List<String>> readFrames(String path) {
        List<List<String>> frames = new ArrayList<>();
        for (Object frame : getConfig().getList(path, List.of())) {
            if (frame instanceof List<?> lines) {
                List<String> clean = new ArrayList<>();
                for (Object line : lines) {
                    if (line instanceof String text) {
                        clean.add(text);
                    }
                }
                if (!clean.isEmpty()) {
                    frames.add(List.copyOf(clean));
                }
            }
        }
        return frames;
    }

    private void startTasks() {
        tablistTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            frameIndex++;
            applyToAll();
        }, frameIntervalTicks, frameIntervalTicks);
    }

    /** Push the current header/footer frame and refresh the tab name of every online player. */
    public void applyToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyTablistName(player);
            pushCurrentFrame(player);
        }
    }

    /** Set the player's tablist entry name from the first matching group. */
    public void applyTablistName(Player player) {
        for (NameGroup group : nameGroups) {
            if (group.permission().isEmpty() || player.hasPermission(group.permission())) {
                // Parse prefix + name + suffix as ONE MiniMessage string so paired
                // tags like <gray>...</gray> resolve instead of leaking literally.
                player.playerListName(TextUtil.safe(group.prefix() + player.getName() + group.suffix()));
                return;
            }
        }
        // No groups configured: fall back to the plain player name.
        player.playerListName(Component.text(player.getName()));
    }

    /** Send the current animated header/footer frame to one player. */
    public void pushCurrentFrame(Player player) {
        Component header = frameComponent(headerFrames, player.getName(),
                Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
        Component footer = frameComponent(footerFrames, player.getName(),
                Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    private Component frameComponent(List<List<String>> frames, String playerName, int online, int max) {
        if (frames.isEmpty()) {
            return Component.empty();
        }
        return TextUtil.parseLines(frames.get(Math.floorMod(frameIndex, frames.size())), playerName, online, max);
    }

    /**
     * Build the current MOTD. The frame is derived from wall-clock time so it
     * animates smoothly without extra tasks, and it is safe to call off the
     * main thread (server-list pings arrive on Netty threads).
     */
    public Component currentMotd(int online, int max) {
        if (motdFrames.isEmpty()) {
            return Component.empty();
        }
        long slot = System.currentTimeMillis() / (motdIntervalSeconds * 1000L);
        List<String> frame = motdFrames.get((int) (slot % motdFrames.size()));
        return TextUtil.parseLines(frame, null, online, max);
    }

    public int motdMaxPlayers() {
        return motdMaxPlayers;
    }
}
