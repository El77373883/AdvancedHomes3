package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class HomesCommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    public HomesCommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { plugin.getMessageManager().send(sender, "player-only"); return true; }
        if (!player.hasPermission("advancedhomes.homes")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
        MessageManager msg = plugin.getMessageManager();
        UUID targetUUID = player.getUniqueId();
        if (args.length > 0) {
            if (!player.hasPermission("advancedhomes.homes.others")) { msg.send(player, "no-permission"); return true; }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { msg.send(player, "player-not-found", MessageManager.of("player", args[0])); return true; }
            targetUUID = target.getUniqueId();
        }
        List<Home> homes = plugin.getHomeManager().getHomeList(targetUUID);
        int limit = plugin.getConfigManager().getHomesLimit(player);
        String limitStr = limit == -1 ? "∞" : String.valueOf(limit);
        if (homes.isEmpty()) { msg.send(player, "home-list-empty"); return true; }
        player.sendMessage(msg.getRaw("home-list-header").replace("{count}", String.valueOf(homes.size())).replace("{limit}", limitStr));
        for (Home home : homes)
            player.sendMessage(msg.getRaw("home-list-entry")
                    .replace("{home}", home.getName()).replace("{world}", home.getWorldName())
                    .replace("{x}", String.valueOf(home.getBlockX())).replace("{y}", String.valueOf(home.getBlockY())).replace("{z}", String.valueOf(home.getBlockZ())));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("advancedhomes.homes.others")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) names.add(p.getName());
            return names;
        }
        return new ArrayList<>();
    }
}
