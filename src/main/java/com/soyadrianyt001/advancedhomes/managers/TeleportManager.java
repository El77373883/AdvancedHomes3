package com.soyadrianyt001.advancedhomes.managers;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final AdvancedHomes plugin;
    private final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();
    private final Map<UUID, Long>           cooldowns        = new HashMap<>();
    private final Map<UUID, Location>       startLocations   = new HashMap<>();

    public TeleportManager(AdvancedHomes plugin) { this.plugin = plugin; }

    public boolean isOnCooldown(Player player) {
        if (player.hasPermission("advancedhomes.bypass.cooldown")) return false;
        int secs = plugin.getConfigManager().getTeleportCooldown();
        if (secs <= 0) return false;
        Long last = cooldowns.get(player.getUniqueId());
        if (last == null) return false;
        return (System.currentTimeMillis() - last) / 1000 < secs;
    }

    public int getRemainingCooldown(Player player) {
        int secs = plugin.getConfigManager().getTeleportCooldown();
        Long last = cooldowns.get(player.getUniqueId());
        if (last == null) return 0;
        return (int) Math.max(0, secs - (System.currentTimeMillis() - last) / 1000);
    }

    public boolean hasPendingTeleport(UUID uuid) { return pendingTeleports.containsKey(uuid); }

    public void cancelTeleport(UUID uuid) {
        BukkitRunnable task = pendingTeleports.remove(uuid);
        if (task != null) task.cancel();
        startLocations.remove(uuid);
    }

    public void teleportHome(Player player, Home home) {
        MessageManager msg = plugin.getMessageManager();
        if (plugin.getConfigManager().isBlockedWorld(player.getWorld().getName())) {
            msg.send(player, "teleport-blocked-world");
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
            return;
        }
        Location dest = home.getLocation();
        if (dest == null) {
            player.sendMessage(MessageManager.colorize("&cEl mundo del hogar no esta cargado."));
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
            return;
        }
        cancelTeleport(player.getUniqueId());
        int delay = plugin.getConfigManager().getTeleportDelay();
        if (delay <= 0 || player.hasPermission("advancedhomes.bypass.cooldown")) {
            executeTeleport(player, home, dest);
            return;
        }
        startLocations.put(player.getUniqueId(), player.getLocation().clone());
        msg.send(player, "home-teleporting", MessageManager.of("home", home.getName(), "delay", String.valueOf(delay)));
        SoundUtil.playSound(player, plugin.getConfigManager().getSound("countdown"));

        BukkitRunnable task = new BukkitRunnable() {
            int remaining = delay;
            @Override public void run() {
                if (!player.isOnline()) { cancelTeleport(player.getUniqueId()); return; }
                if (plugin.getConfigManager().cancelOnMove()) {
                    Location start = startLocations.get(player.getUniqueId());
                    if (start != null) {
                        Location cur = player.getLocation();
                        if (Math.abs(cur.getX()-start.getX())>0.15 || Math.abs(cur.getY()-start.getY())>0.15 || Math.abs(cur.getZ()-start.getZ())>0.15) {
                            msg.send(player, "teleport-cancelled-move");
                            SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
                            cancelTeleport(player.getUniqueId()); return;
                        }
                    }
                }
                remaining--;
                if (remaining <= 0) { executeTeleport(player, home, dest); cancelTeleport(player.getUniqueId()); }
                else SoundUtil.playSound(player, plugin.getConfigManager().getSound("countdown"));
            }
        };
        pendingTeleports.put(player.getUniqueId(), task);
        task.runTaskTimer(plugin, 20L, 20L);
    }

    private void executeTeleport(Player player, Home home, Location dest) {
        player.teleport(dest);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        startLocations.remove(player.getUniqueId());
        plugin.getMessageManager().send(player, "home-teleported", MessageManager.of("home", home.getName()));
        if (plugin.getConfigManager().areSoundsEnabled())    SoundUtil.playSound(player, plugin.getConfigManager().getSound("teleport"));
        if (plugin.getConfigManager().areParticlesEnabled()) SoundUtil.spawnParticles(player, plugin.getConfigManager().getTeleportParticle());
    }

    public Map<UUID, Location> getStartLocations() { return startLocations; }
}
