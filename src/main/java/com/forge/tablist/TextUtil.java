package com.forge.tablist;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;

/** MiniMessage parsing plus ForgeTablist's %placeholder% replacement. */
public final class TextUtil {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private TextUtil() {}

    /** Parse a single MiniMessage string with no placeholders. */
    public static Component mini(String raw) {
        return MINI.deserialize(raw);
    }

    /**
     * Parse MiniMessage, falling back to literal text when the input has
     * malformed tags instead of throwing.
     */
    public static Component safe(String raw) {
        try {
            return MINI.deserialize(raw);
        } catch (Exception e) {
            return Component.text(raw);
        }
    }

    /**
     * Parse a multi-line frame into one Component, expanding placeholders.
     *
     * @param lines      raw MiniMessage lines
     * @param playerName player name for %player%, or null when there is no player (MOTD)
     * @param online     value for %online%
     * @param max        value for %max%
     */
    public static Component parseLines(List<String> lines, @Nullable String playerName, int online, int max) {
        double tps = tps();
        List<Component> parts = new ArrayList<>(lines.size());
        for (String line : lines) {
            parts.add(MINI.deserialize(applyPlaceholders(line, playerName, online, max, tps)));
        }
        return Component.join(JoinConfiguration.newlines(), parts);
    }

    private static String applyPlaceholders(String raw, @Nullable String playerName, int online, int max, double tps) {
        return raw.replace("%player%", playerName == null ? "" : playerName)
                .replace("%online%", String.valueOf(online))
                .replace("%max%", String.valueOf(max))
                .replace("%tps%", String.format("%.1f", tps));
    }

    private static double tps() {
        try {
            double[] values = org.bukkit.Bukkit.getTPS();
            return values.length > 0 ? values[0] : 20.0;
        } catch (Exception e) {
            return 20.0;
        }
    }
}
