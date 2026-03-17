package com.soyadrianyt001.advancedhomes.listeners;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener de jugadores para cancelar teletransporte.
 * AdvancedHomes - by soyadrianyt001
 */
public class PlayerListener implements Listener {

    private final AdvancedHomes plugin;

    public PlayerListener(AdvancedHomes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getTeleportManager().hasPendingTeleport(player.getUniqueId())) return;
        if (!plugin.getConfigManager().cancelOnMove()) return;

        // Solo cancelar si cambia bloque
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        plugin.getTeleportManager().cancelTeleport(player.getUniqueId());
        plugin.getMessageManager().send(player, "teleport-cancelled-move");
        SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getTeleportManager().hasPendingTeleport(player.getUniqueId())) return;
        if (!plugin.getConfigManager().cancelOnDamage()) return;

        plugin.getTeleportManager().cancelTeleport(player.getUniqueId());
        plugin.getMessageManager().send(player, "teleport-cancelled-damage");
        SoundUtil.playSound(player, plugin.getConfigManager().getSound("error"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getTeleportManager().cancelTeleport(player.getUniqueId());
        plugin.getHomeGUI().removePlayer(player.getUniqueId());
    }
}
