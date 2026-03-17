package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class HomeGUICommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    public HomeGUICommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { plugin.getMessageManager().send(sender, "player-only"); return true; }
        if (!player.hasPermission("advancedhomes.gui")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
        Player target = player;
        if (args.length > 0 && player.hasPermission("advancedhomes.admin")) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { plugin.getMessageManager().send(player, "player-not-found", MessageManager.of("player", args[0])); return true; }
        }
        plugin.getHomeGUI().openGUI(target, 0);
        SoundUtil.playSound(target, plugin.getConfigManager().getSound("open-gui"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("advancedhomes.admin")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) names.add(p.getName());
            return names;
        }
        return new ArrayList<>();
    }
}
