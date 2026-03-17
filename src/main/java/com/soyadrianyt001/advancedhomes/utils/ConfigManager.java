package com.soyadrianyt001.advancedhomes.utils;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigManager {

    private final AdvancedHomes plugin;
    private FileConfiguration config;

    public ConfigManager(AdvancedHomes plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public int getDefaultLimit()     { return config.getInt("general.default-homes-limit", 5); }

    public int getHomesLimit(Player player) {
        if (player.hasPermission("advancedhomes.bypass.limit"))    return -1;
        if (player.hasPermission("advancedhomes.limit.unlimited")) return -1;
        if (player.hasPermission("advancedhomes.limit.premium"))   return config.getInt("general.homes-limit.premium", 25);
        if (player.hasPermission("advancedhomes.limit.vip"))       return config.getInt("general.homes-limit.vip", 10);
        return getDefaultLimit();
    }

    public int getTeleportDelay()        { return config.getInt("teleport.delay", 3); }
    public boolean cancelOnMove()        { return config.getBoolean("teleport.cancel-on-move", true); }
    public boolean cancelOnDamage()      { return config.getBoolean("teleport.cancel-on-damage", true); }
    public int getTeleportCooldown()     { return config.getInt("teleport.cooldown", 5); }
    public boolean allowCrossWorld()     { return config.getBoolean("teleport.allow-cross-world", true); }
    public boolean isBlockedWorld(String w) { return config.getStringList("teleport.blocked-worlds").contains(w); }
    public String getGuiTitle()          { return MessageManager.colorize(config.getString("gui.title", "&6AdvancedHomes")); }
    public int getGuiRows()              { return Math.max(3, Math.min(6, config.getInt("gui.rows", 6))); }
    public String getHomeMaterial()      { return config.getString("gui.home-item", "CYAN_BED"); }
    public boolean fillEmpty()           { return config.getBoolean("gui.fill-empty", true); }
    public String getFillMaterial()      { return config.getString("gui.fill-material", "BLACK_STAINED_GLASS_PANE"); }
    public boolean areSoundsEnabled()    { return config.getBoolean("sounds.enabled", true); }
    public String getSound(String key)   { return config.getString("sounds." + key, ""); }
    public boolean areParticlesEnabled() { return config.getBoolean("particles.enabled", true); }
    public String getTeleportParticle()  { return config.getString("particles.teleport-effect", "PORTAL"); }
    public String getDatabaseType()      { return config.getString("database.type", "YAML").toUpperCase(); }
}
