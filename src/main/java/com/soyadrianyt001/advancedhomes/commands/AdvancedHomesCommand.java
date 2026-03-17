package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class AdvancedHomesCommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    public AdvancedHomesCommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageManager msg = plugin.getMessageManager();
        if (args.length == 0) { sendHelp(sender); return true; }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("advancedhomes.reload")) { msg.send(sender, "no-permission"); return true; }
                plugin.getConfigManager().reload();
                plugin.getMessageManager().reload();
                plugin.getHomeManager().reload();
                msg.send(sender, "plugin-reloaded");
            }
            case "info" -> {
                sender.sendMessage(msg.getRaw("info-header"));
                sender.sendMessage(msg.getRaw("info-version").replace("{version}", plugin.getDescription().getVersion()));
                sender.sendMessage(msg.getRaw("info-author"));
                if (sender instanceof Player player) {
                    int count = plugin.getHomeManager().getHomeCount(player.getUniqueId());
                    int limit = plugin.getConfigManager().getHomesLimit(player);
                    sender.sendMessage(msg.getRaw("info-homes").replace("{count}", String.valueOf(count)).replace("{limit}", limit == -1 ? "∞" : String.valueOf(limit)));
                }
                sender.sendMessage(msg.getRaw("info-footer"));
            }
            case "reset" -> {
                if (!sender.hasPermission("advancedhomes.reset")) { msg.send(sender, "no-permission"); return true; }
                if (args.length < 2) { msg.send(sender, "usage-ah"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { msg.send(sender, "player-not-found", MessageManager.of("player", args[1])); return true; }
                plugin.getHomeManager().resetHomes(target.getUniqueId());
                msg.send(sender, "homes-reset", MessageManager.of("player", target.getName()));
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------");
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " AdvancedHomes " + ChatColor.GRAY + "by soyadrianyt001");
        sender.sendMessage(ChatColor.GRAY + " v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------");
        sender.sendMessage(ChatColor.YELLOW + " /home " + ChatColor.GRAY + "[nombre]");
        sender.sendMessage(ChatColor.YELLOW + " /sethome " + ChatColor.GRAY + "[nombre]");
        sender.sendMessage(ChatColor.YELLOW + " /delhome " + ChatColor.GRAY + "<nombre>");
        sender.sendMessage(ChatColor.YELLOW + " /homes");
        sender.sendMessage(ChatColor.YELLOW + " /homegui");
        sender.sendMessage(ChatColor.YELLOW + " /ah reload");
        sender.sendMessage(ChatColor.YELLOW + " /ah reset " + ChatColor.GRAY + "<jugador>");
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "----------------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("reload", "info", "reset");
        if (args.length == 2 && args[0].equalsIgnoreCase("reset"))
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        return List.of();
    }
}
