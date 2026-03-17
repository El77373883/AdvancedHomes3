package com.soyadrianyt001.advancedhomes;

import com.soyadrianyt001.advancedhomes.commands.*;
import com.soyadrianyt001.advancedhomes.gui.HomeGUI;
import com.soyadrianyt001.advancedhomes.listeners.GUIListener;
import com.soyadrianyt001.advancedhomes.listeners.PlayerListener;
import com.soyadrianyt001.advancedhomes.managers.HomeManager;
import com.soyadrianyt001.advancedhomes.managers.TeleportManager;
import com.soyadrianyt001.advancedhomes.utils.ConfigManager;
import com.soyadrianyt001.advancedhomes.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AdvancedHomes - Plugin principal
 * Hecho por soyadrianyt001
 * Version: 1.0.0
 */
public class AdvancedHomes extends JavaPlugin {

    private static AdvancedHomes instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private HomeManager homeManager;
    private TeleportManager teleportManager;
    private HomeGUI homeGUI;

    @Override
    public void onEnable() {
        instance = this;

        // Guardar configuracion por defecto
        saveDefaultConfig();

        // Inicializar managers
        this.configManager  = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.homeManager    = new HomeManager(this);
        this.teleportManager = new TeleportManager(this);
        this.homeGUI        = new HomeGUI(this);

        // Registrar comandos
        registerCommands();

        // Registrar eventos
        registerListeners();

        // Guardado automatico cada 5 minutos
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->
                homeManager.saveData(), 6000L, 6000L);

        printBanner();
    }

    @Override
    public void onDisable() {
        if (homeManager != null) homeManager.saveData();
        getLogger().info("AdvancedHomes desactivado. Datos guardados.");
    }

    private void registerCommands() {
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("home").setTabCompleter(new HomeCommand(this));

        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("sethome").setTabCompleter(new SetHomeCommand(this));

        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("delhome").setTabCompleter(new DelHomeCommand(this));

        getCommand("homes").setExecutor(new HomesCommand(this));
        getCommand("homes").setTabCompleter(new HomesCommand(this));

        getCommand("homegui").setExecutor(new HomeGUICommand(this));
        getCommand("homegui").setTabCompleter(new HomeGUICommand(this));

        AdvancedHomesCommand ahCmd = new AdvancedHomesCommand(this);
        getCommand("advancedhomes").setExecutor(ahCmd);
        getCommand("advancedhomes").setTabCompleter(ahCmd);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void printBanner() {
        String gold = ChatColor.GOLD.toString();
        String gray = ChatColor.GRAY.toString();
        String reset = ChatColor.RESET.toString();
        Bukkit.getConsoleSender().sendMessage(gold + "  ___       _                               _ _   _                       ");
        Bukkit.getConsoleSender().sendMessage(gold + " / _ \\   __| |_   ____ _ _ __   ___ ___  __| | | | |  ___  _ __ ___   ___  ___");
        Bukkit.getConsoleSender().sendMessage(gold + "| | | | / _` \\ \\ / / _` | '_ \\ / __/ _ \\/ _` | |_| | / _ \\| '_ ` _ \\ / _ \\/ __|");
        Bukkit.getConsoleSender().sendMessage(gold + "| |_| || (_| |\\ V / (_| | | | | (_|  __/ (_| |  _  || (_) | | | | | |  __/\\__ \\");
        Bukkit.getConsoleSender().sendMessage(gold + " \\___/  \\__,_| \\_/ \\__,_|_| |_|\\___\\___|\\__,_|_| |_| \\___/|_| |_| |_|\\___||___/");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(gray + "  Plugin: " + gold + "AdvancedHomes v" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(gray + "  Autor:  " + gold + "soyadrianyt001");
        Bukkit.getConsoleSender().sendMessage(gray + "  Estado: " + gold + "✔ Activo y listo");
        Bukkit.getConsoleSender().sendMessage("");
    }

    // ─── Getters ──────────────────────────────────────────────

    public static AdvancedHomes getInstance() { return instance; }
    public ConfigManager getConfigManager()   { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public HomeManager getHomeManager()       { return homeManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public HomeGUI getHomeGUI()               { return homeGUI; }
}
