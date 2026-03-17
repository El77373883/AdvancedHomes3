package com.soyadrianyt001.advancedhomes.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.UUID;

public class Home {

    private final UUID ownerUUID;
    private final String name;
    private String worldName;
    private double x, y, z;
    private float yaw, pitch;
    private long createdAt;

    public Home(UUID ownerUUID, String name, Location location) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.createdAt = System.currentTimeMillis();
    }

    public Home(UUID ownerUUID, String name, String worldName,
                double x, double y, double z, float yaw, float pitch, long createdAt) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.worldName = worldName;
        this.x = x; this.y = y; this.z = z;
        this.yaw = yaw; this.pitch = pitch;
        this.createdAt = createdAt;
    }

    public UUID getOwnerUUID()   { return ownerUUID; }
    public String getName()      { return name; }
    public String getWorldName() { return worldName; }
    public double getX()         { return x; }
    public double getY()         { return y; }
    public double getZ()         { return z; }
    public float getYaw()        { return yaw; }
    public float getPitch()      { return pitch; }
    public long getCreatedAt()   { return createdAt; }
    public int getBlockX()       { return (int) Math.floor(x); }
    public int getBlockY()       { return (int) Math.floor(y); }
    public int getBlockZ()       { return (int) Math.floor(z); }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean isWorldLoaded() {
        return Bukkit.getWorld(worldName) != null;
    }
}
