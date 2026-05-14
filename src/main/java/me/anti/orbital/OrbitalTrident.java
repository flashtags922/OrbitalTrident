package me.anti.orbital;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OrbitalTrident extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("OrbitalTrident enabled");
    }

    @EventHandler
    public void onTridentHit(ProjectileHitEvent event) {

        if (!(event.getEntity() instanceof Trident)) return;
        if (event.getHitBlock() == null) return;

        Location target = event.getHitBlock().getLocation();

        spawnOrbitalStrike(target);
    }

    private void spawnOrbitalStrike(Location center) {

        World world = center.getWorld();

        for (int i = 0; i < 40; i++) {

            double angle = Math.toRadians(i * 9);
            double radius = 5;

            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location spawn = center.clone().add(x, 80, z);

            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(
                    spawn,
                    EntityType.TNT
            );

            tnt.setFuseTicks(60);
            tnt.setYield(8f);
        }
    }
}
