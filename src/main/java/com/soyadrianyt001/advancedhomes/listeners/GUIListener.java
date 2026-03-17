package com.soyadrianyt001.advancedhomes.listeners;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import com.soyadrianyt001.advancedhomes.utils.SoundUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIListener implements Listener {

    private final AdvancedHomes plugin;
    public GUIListener(AdvancedHomes plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!plugin.getHomeGUI().isGUITitle(event.getView().getTitle())) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        int rows = plugin.getConfigManager().getGuiRows();
        int navBase = (rows * 9) - 9;
        int slot = event.getRawSlot();
        if (slot == navBase) {
            int cur = plugin.getHomeGUI().getPage(player.getUniqueId());
            if (cur > 0) plugin.getHomeGUI().openGUI(player, cur - 1);
            return;
        }
        if (slot == navBase + 8) {
            if (displayName.contains("→") || displayName.contains("Siguiente"))
                plugin.getHomeGUI().openGUI(player, plugin.getHomeGUI().getPage(player.getUniqueId()) + 1);
            else
                player.closeInventory();
            return;
        }
        if (slot >= navBase) return;
        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), displayName);
        if (home == null) return;
        if (event.isRightClick()) {
            plugin.getHomeManager().deleteHome(player.getUniqueId(), home.getName());
            plugin.getMessageManager().send(player, "home-deleted", MessageManager.of("home", home.getName()));
            SoundUtil.playSound(player, plugin.getConfigManager().getSound("delete-home"));
            plugin.getHomeGUI().openGUI(player, plugin.getHomeGUI().getPage(player.getUniqueId()));
        } else {
            player.closeInventory();
            plugin.getTeleportManager().teleportHome(player, home);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (plugin.getHomeGUI().isGUITitle(event.getView().getTitle()))
            plugin.getHomeGUI().removePlayer(player.getUniqueId());
    }
}
