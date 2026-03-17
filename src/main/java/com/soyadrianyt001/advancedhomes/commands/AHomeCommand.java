package com.soyadrianyt001.advancedhomes.commands;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.stream.Collectors;

public class AHomeCommand implements CommandExecutor, TabCompleter {

    private final AdvancedHomes plugin;
    private static final String NAME_REGEX = "^[a-zA-Z0-9_]{1,16}$";

    public AHomeCommand(AdvancedHomes plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        // /ahome sin argumentos = abrir GUI
        if (args.length == 0) {
            if (!player.hasPermission("advancedhomes.gui")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
            plugin.getHomeGUI().openGUI(player, 0);
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("open-gui"));
            return true;
        }

        switch (args[0].toLowerCase()) {

            // /ahome create [nombre]
            case "create" -> {
                if (!player.hasPermission("advancedhomes.sethome")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                String name = args.length > 1 ? args[1] : "home";
                if (!name.matches(NAME_REGEX)) {
                    plugin.getMessageManager().send(player, name.length() > 16 ? "home-name-too-long" : "home-name-invalid");
                    SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
                    return true;
                }
                int count = plugin.getHomeManager().getHomeCount(player.getUniqueId());
                int limit = plugin.getConfigManager().getHomesLimit(player);
                boolean exists = plugin.getHomeManager().hasHome(player.getUniqueId(), name);
                if (!exists && limit != -1 && count >= limit) {
                    plugin.getMessageManager().send(player, "home-limit-reached", MessageManager.of("limit", String.valueOf(limit)));
                    SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
                    return true;
                }
                plugin.getHomeManager().setHome(player.getUniqueId(), name, player.getLocation());
                plugin.getMessageManager().send(player, "home-set", MessageManager.of("home", name));
                if (plugin.getConfigManager().areSoundsEnabled()) SoundUtil.playSound(player, plugin.getConfigManager().getSound("set-home"));
            }

            // /ahome remove <nombre>
            case "remove", "delete" -> {
                if (!player.hasPermission("advancedhomes.delhome")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (args.length < 2) { plugin.getMessageManager().send(player, "usage-remove"); return true; }
                boolean deleted = plugin.getHomeManager().deleteHome(player.getUniqueId(), args[1]);
                if (!deleted) {
                    plugin.getMessageManager().send(player, "home-not-found", MessageManager.of("home", args[1]));
                    SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
                    return true;
                }
                plugin.getMessageManager().send(player, "home-deleted", MessageManager.of("home", args[1]));
                if (plugin.getConfigManager().areSoundsEnabled()) SoundUtil.playSound(player, plugin.getConfigManager().getSound("delete-home"));
            }

            // /ahome list
            case "list" -> {
                if (!player.hasPermission("advancedhomes.homes")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                List<Home> homes = plugin.getHomeManager().getHomeList(player.getUniqueId());
                int limit = plugin.getConfigManager().getHomesLimit(player);
                String limitStr = limit == -1 ? "∞" : String.valueOf(limit);
                if (homes.isEmpty()) { plugin.getMessageManager().send(player, "home-list-empty"); return true; }
                player.sendMessage(plugin.getMessageManager().getRaw("home-list-header")
                        .replace("{count}", String.valueOf(homes.size())).replace("{limit}", limitStr));
                for (Home home : homes)
                    player.sendMessage(plugin.getMessageManager().getRaw("home-list-entry")
                            .replace("{home}", home.getName()).replace("{world}", home.getWorldName())
                            .replace("{x}", String.valueOf(home.getBlockX()))
                            .replace("{y}", String.valueOf(home.getBlockY()))
                            .replace("{z}", String.valueOf(home.getBlockZ())));
            }

            // /ahome gui
            case "gui" -> {
                if (!player.hasPermission("advancedhomes.gui")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                plugin.getHomeGUI().openGUI(player, 0);
                SoundUtil.playSound(player, plugin.getConfigManager().getSound("open-gui"));
            }

            // /ahome <nombre> = ir a ese hogar
            default -> {
                if (!player.hasPermission("advancedhomes.home")) { plugin.getMessageManager().send(player, "no-permission"); return true; }
                if (plugin.getTeleportManager().isOnCooldown(player)) {
                    plugin.getMessageManager().send(player, "teleport-cooldown",
                            MessageManager.of("time", String.valueOf(plugin.getTeleportManager().getRemainingCooldown(player))));
                    SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
                    return true;
                }
                Home home = plugin.getHomeManager().getHome(player.getUniqueId(), args[0]);
                if (home == null) {
                    plugin.getMessageManager().send(player, "home-not-found", MessageManager.of("home", args[0]));
                    SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
                    return true;
                }
                plugin.getTeleportManager().teleportHome(player, home);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return new ArrayList<>();

        if (args.length == 1) {
            List<String> subs = Arrays.asList("create", "remove", "list", "gui");
            List<String> result = new ArrayList<>(subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList()));
            // tambien autocompletar nombres de hogares
            plugin.getHomeManager().getHomeList(player.getUniqueId()).stream()
                    .map(Home::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                    .forEach(result::add);
            return result;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {
            return plugin.getHomeManager().getHomeList(player.getUniqueId()).stream()
                    .map(Home::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
