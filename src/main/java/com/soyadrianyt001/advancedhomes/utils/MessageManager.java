package com.soyadrianyt001.advancedhomes.utils;

import com.soyadrianyt001.advancedhomes.AdvancedHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final AdvancedHomes plugin;
    private FileConfiguration messages;
    private String prefix;

    public MessageManager(AdvancedHomes plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) plugin.saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(file);
        prefix = colorize(messages.getString("prefix", "&8[&6AdvancedHomes&8] &r"));
    }

    public String get(String key) {
        String msg = messages.getString("messages." + key, "&cMensaje no encontrado: " + key);
        return colorize(prefix + msg);
    }

    public String getRaw(String key) {
        return colorize(messages.getString("messages." + key, key));
    }

    public String get(String key, Map<String, String> placeholders) {
        String msg = get(key);
        for (Map.Entry<String, String> e : placeholders.entrySet())
            msg = msg.replace("{" + e.getKey() + "}", e.getValue());
        return msg;
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(key));
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(get(key, placeholders));
    }

    public void sendRaw(CommandSender sender, String key) {
        sender.sendMessage(colorize(getRaw(key)));
    }

    public static String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Map<String, String> of(String... pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2)
            map.put(pairs[i], pairs[i + 1]);
        return map;
    }
}
