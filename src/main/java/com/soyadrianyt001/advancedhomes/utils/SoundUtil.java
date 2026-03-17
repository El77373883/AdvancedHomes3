package com.soyadrianyt001.advancedhomes.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playSound(Player player, String soundName) {
        if (soundName == null || soundName.isEmpty()) return;
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {}
    }

    public static void spawnParticles(Player player, String particleName) {
        if (particleName == null || particleName.isEmpty()) return;
        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            Location loc = player.getLocation().add(0, 1, 0);
            player.getWorld().spawnParticle(particle, loc, 30, 0.5, 0.5, 0.5, 0.1);
        } catch (IllegalArgumentException ignored) {}
    }
}
