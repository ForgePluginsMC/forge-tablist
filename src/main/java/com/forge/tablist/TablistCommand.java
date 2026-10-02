package com.forge.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** Handles /ftablist. Only subcommand: reload. */
public final class TablistCommand implements CommandExecutor {

    private final ForgeTablist plugin;

    public TablistCommand(ForgeTablist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("forgetablist.admin")) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadSettings();
            sender.sendMessage(Component.text("ForgeTablist config reloaded.", NamedTextColor.GREEN));
            return true;
        }
        sender.sendMessage(Component.text("Usage: /" + label + " reload", NamedTextColor.YELLOW));
        return true;
    }
}
