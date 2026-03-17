package com.soyadrianyt001.advancedhomes.gui;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class HomeGUI {

    private final AdvancedHomes plugin;
    private final Map<UUID, Integer> openPages = new HashMap<>();

    public HomeGUI(AdvancedHomes plugin) { this.plugin = plugin; }

    public void openGUI(Player player, int page) {
        int rows = plugin.getConfigManager().getGuiRows();
        int slots = rows * 9;
        int contentSlots = slots - 9;
        List<Home> homes = plugin.getHomeManager().getHomeList(player.getUniqueId());
        int totalPages = Math.max(1, (int) Math.ceil((double) homes.size() / contentSlots));
        page = Math.max(0, Math.min(page, totalPages - 1));

        String title = plugin.getConfigManager().getGuiTitle() + " &8(" + (page + 1) + "/" + totalPages + ")";
        Inventory inv = Bukkit.createInventory(null, slots, MessageManager.colorize(title));

        if (plugin.getConfigManager().fillEmpty()) {
            Material fill = getMaterial(plugin.getConfigManager().getFillMaterial(), Material.BLACK_STAINED_GLASS_PANE);
            ItemStack filler = buildItem(fill, " ", null);
            for (int i = 0; i < slots; i++) inv.setItem(i, filler);
        }

        Material homeMat = getMaterial(plugin.getConfigManager().getHomeMaterial(), Material.CYAN_BED);
        int start = page * contentSlots;
        int end = Math.min(start + contentSlots, homes.size());

        for (int i = start; i < end; i++) {
            Home home = homes.get(i);
            List<String> lore = new ArrayList<>();
            lore.add(MessageManager.colorize("&7Mundo: &f" + home.getWorldName()));
            lore.add(MessageManager.colorize("&7Coords: &f" + home.getBlockX() + ", " + home.getBlockY() + ", " + home.getBlockZ()));
            lore.add("");
            lore.add(MessageManager.colorize("&a▶ Clic izquierdo &7para ir"));
            lore.add(MessageManager.colorize("&c▶ Clic derecho &7para eliminar"));
            ItemStack item = home.isWorldLoaded()
                    ? buildItem(homeMat, "&6" + home.getName(), lore)
                    : buildItem(Material.BARRIER, "&c" + home.getName() + " &7(mundo no cargado)", null);
            inv.setItem(i - start, item);
        }

        int navBase = slots - 9;
        int limit = plugin.getConfigManager().getHomesLimit(player);
        String limitStr = limit == -1 ? "∞" : String.valueOf(limit);

        if (page > 0)
            inv.setItem(navBase, buildItem(Material.ARROW, "&e← Anterior", null));

        inv.setItem(navBase + 4, buildItem(Material.NETHER_STAR, "&6&lAdvancedHomes",
                Arrays.asList(
                    MessageManager.colorize("&7Hogares: &e" + homes.size() + "&7/&e" + limitStr),
                    MessageManager.colorize("&7Pagina: &e" + (page+1) + "&7/&e" + totalPages)
                )));

        if (page < totalPages - 1)
            inv.setItem(navBase + 8, buildItem(Material.ARROW, "&eSiguiente →", null));
        else
            inv.setItem(navBase + 8, buildItem(Material.BARRIER, "&cCerrar", null));

        openPages.put(player.getUniqueId(), page);
        player.openInventory(inv);
    }

    public int getPage(UUID uuid)       { return openPages.getOrDefault(uuid, 0); }
    public void removePlayer(UUID uuid) { openPages.remove(uuid); }
    public boolean isGUITitle(String title) { return title.contains("AdvancedHomes"); }

    private ItemStack buildItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageManager.colorize(name));
            if (lore != null) meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return item;
    }

    private Material getMaterial(String name, Material fallback) {
        try { return Material.valueOf(name.toUpperCase()); }
        catch (IllegalArgumentException e) { return fallback; }
    }
}
