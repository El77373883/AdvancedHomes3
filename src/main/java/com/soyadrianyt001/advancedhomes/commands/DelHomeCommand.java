package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.stream.Collectors;

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    public DelHomeCommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { plugin.getMessageManager().send(sender, "player-only"); return true; }
        if (!player.hasPermission("advancedhomes.delhome")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
        if (args.length == 0) { plugin.getMessageManager().send(player, "usage-delhome"); return true; }
        boolean deleted = plugin.getHomeManager().deleteHome(player.getUniqueId(), args[0]);
        if (!deleted) { plugin.getMessageManager().send(player, "home-not-found", MessageManager.of("home", args[0])); SoundUtil.playSound(player, plugin.getConfigManager().getSound("error")); return true; }
        plugin.getMessageManager().send(player, "home-deleted", MessageManager.of("home", args[0]));
        if (plugin.getConfigManager().areSoundsEnabled()) SoundUtil.playSound(player, plugin.getConfigManager().getSound("delete-home"));
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
