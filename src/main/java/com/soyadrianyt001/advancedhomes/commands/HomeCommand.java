package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.stream.Collectors;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    public HomeCommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { plugin.getMessageManager().send(sender, "player-only"); return true; }
        if (!player.hasPermission("advancedhomes.home")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
        MessageManager msg = plugin.getMessageManager();
        if (plugin.getTeleportManager().isOnCooldown(player)) {
            msg.send(player, "teleport-cooldown", MessageManager.of("time", String.valueOf(plugin.getTeleportManager().getRemainingCooldown(player))));
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
            return true;
        }
        List<Home> homes = plugin.getHomeManager().getHomeList(player.getUniqueId());
        String homeName;
        if (args.length == 0) {
            if (homes.isEmpty()) { msg.send(player, "home-list-empty"); SoundUtil.playSound(player, plugin.getConfigManager().getSound("error")); return true; }
            if (homes.size() == 1) { homeName = homes.get(0).getName(); }
            else if (plugin.getHomeManager().hasHome(player.getUniqueId(), "home")) { homeName = "home"; }
            else { plugin.getHomeGUI().openGUI(player, 0); SoundUtil.playSound(player, plugin.getConfigManager().getSound("open-gui")); return true; }
        } else { homeName = args[0]; }
        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
        if (home == null) { msg.send(player, "home-not-found", MessageManager.of("home", homeName)); SoundUtil.playSound(player, plugin.getConfigManager().getSound("error")); return true; }
        plugin.getTeleportManager().teleportHome(player, home);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();
        if (args.length == 1)
            return plugin.getHomeManager().getHomeList(player.getUniqueId()).stream()
                    .map(Home::getName).filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        return new ArrayList<>();
    }
}
