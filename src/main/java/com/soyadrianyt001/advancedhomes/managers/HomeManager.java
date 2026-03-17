package com.soyadrianyt001.advancedhomes.managers;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import com.soyadrianyt001.advancedhomes.models.Home;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    private final AdvancedHomes plugin;
    private final Map<UUID, Map<String, Home>> homesCache = new HashMap<>();
    private File dataFile;
    private YamlConfiguration dataConfig;

    public HomeManager(AdvancedHomes plugin) {
        this.plugin = plugin;
        loadData();
    }

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); }
            catch (IOException e) { plugin.getLogger().severe("No se pudo crear homes.yml: " + e.getMessage()); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        homesCache.clear();
        if (!dataConfig.contains("homes")) return;

        for (String uuidStr : Objects.requireNonNull(dataConfig.getConfigurationSection("homes")).getKeys(false)) {
            UUID uuid;
            try { uuid = UUID.fromString(uuidStr); }
            catch (IllegalArgumentException e) { continue; }
            Map<String, Home> playerHomes = new HashMap<>();
            var section = dataConfig.getConfigurationSection("homes." + uuidStr);
            if (section == null) continue;
            for (String homeName : section.getKeys(false)) {
                String path = "homes." + uuidStr + "." + homeName + ".";
                String world   = dataConfig.getString(path + "world", "world");
                double x       = dataConfig.getDouble(path + "x", 0);
                double y       = dataConfig.getDouble(path + "y", 64);
                double z       = dataConfig.getDouble(path + "z", 0);
                float yaw      = (float) dataConfig.getDouble(path + "yaw", 0);
                float pitch    = (float) dataConfig.getDouble(path + "pitch", 0);
                long createdAt = dataConfig.getLong(path + "createdAt", System.currentTimeMillis());
                playerHomes.put(homeName.toLowerCase(), new Home(uuid, homeName, world, x, y, z, yaw, pitch, createdAt));
            }
            homesCache.put(uuid, playerHomes);
        }
        plugin.getLogger().info("Hogares cargados: " + homesCache.values().stream().mapToInt(Map::size).sum());
    }

    public void saveData() {
        dataConfig = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Home>> entry : homesCache.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Home home : entry.getValue().values()) {
                String path = "homes." + uuidStr + "." + home.getName() + ".";
                dataConfig.set(path + "world",     home.getWorldName());
                dataConfig.set(path + "x",         home.getX());
                dataConfig.set(path + "y",         home.getY());
                dataConfig.set(path + "z",         home.getZ());
                dataConfig.set(path + "yaw",       home.getYaw());
                dataConfig.set(path + "pitch",     home.getPitch());
                dataConfig.set(path + "createdAt", home.getCreatedAt());
            }
        }
        try { dataConfig.save(dataFile); }
        catch (IOException e) { plugin.getLogger().severe("Error guardando homes.yml: " + e.getMessage()); }
    }

    public boolean setHome(UUID uuid, String name, Location location) {
        homesCache.computeIfAbsent(uuid, k -> new HashMap<>());
        homesCache.get(uuid).put(name.toLowerCase(), new Home(uuid, name, location));
        saveData();
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Home> homes = homesCache.get(uuid);
        if (homes == null) return false;
        boolean removed = homes.remove(name.toLowerCase()) != null;
        if (removed) saveData();
        return removed;
    }

    public Home getHome(UUID uuid, String name) {
        Map<String, Home> homes = homesCache.get(uuid);
        if (homes == null) return null;
        return homes.get(name.toLowerCase());
    }

    public Map<String, Home> getHomes(UUID uuid)  { return homesCache.getOrDefault(uuid, new HashMap<>()); }
    public List<Home> getHomeList(UUID uuid)       { return new ArrayList<>(getHomes(uuid).values()); }
    public int getHomeCount(UUID uuid)             { return getHomes(uuid).size(); }
    public boolean hasHome(UUID uuid, String name) { return getHome(uuid, name) != null; }

    public boolean resetHomes(UUID uuid) {
        homesCache.remove(uuid);
        saveData();
        return true;
    }

    public void reload() { loadData(); }
}
