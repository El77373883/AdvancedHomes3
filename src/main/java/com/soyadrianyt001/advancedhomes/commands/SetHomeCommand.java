package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class SetHomeCommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    private static final String NAME_REGEX = "^[a-zA-Z0-9_]{1,16}$";
    public SetHomeCommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { plugin.getMessageManager().send(sender, "player-only"); return true; }
        if (!player.hasPermission("advancedhomes.sethome")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
        MessageManager msg = plugin.getMessageManager();
        String homeName = args.length > 0 ? args[0] : "home";
        if (!homeName.matches(NAME_REGEX)) {
            msg.send(player, homeName.length() > 16 ? "home-name-too-long" : "home-name-invalid");
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
            return true;
        }
        int count = plugin.getHomeManager().getHomeCount(player.getUniqueId());
        int limit = plugin.getConfigManager().getHomesLimit(player);
        boolean exists = plugin.getHomeManager().hasHome(player.getUniqueId(), homeName);
        if (!exists && limit != -1 && count >= limit) {
            msg.send(player, "home-limit-reached", MessageManager.of("limit", String.valueOf(limit)));
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
            return true;
        }
        plugin.getHomeManager().setHome(player.getUniqueId(), homeName, player.getLocation());
        msg.send(player, "home-set", MessageManager.of("home", homeName));
        if (plugin.getConfigManager().areSoundsEnabled()) SoundUtil.playSound(player, plugin.getConfigManager().getSound("set-home"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return new ArrayList<>(); }
}
