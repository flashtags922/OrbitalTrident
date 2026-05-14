package me.anti.orbital;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class OrbitalTrident extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        ItemStack trident = new ItemStack(Material.TRIDENT);

        ItemMeta meta = trident.getItemMeta();
        meta.setDisplayName("§cOrbital Trident");

        trident.setItemMeta(meta);

        player.getInventory().addItem(trident);

        return true;
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

        world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 3.0f, 0.5f);
        world.strikeLightningEffect(center);

        // BIG ORBITAL NUKE
        for (int i = 0; i < 200; i++) {

            double angle = Math.toRadians(i * 1.8);

            double radius = 12;

            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location spawn = center.clone().add(x, 120, z);

            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(spawn, EntityType.TNT);

            // long fuse so it always hits ground first
            tnt.setFuseTicks(999);

            // stronger explosion
            tnt.setYield(10f);
        }
    }
}
