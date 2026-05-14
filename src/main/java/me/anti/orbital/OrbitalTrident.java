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

        for (int i = 0; i < 40; i++) {

            double angle = Math.toRadians(i * 9);

            double x = Math.cos(angle) * 5;
            double z = Math.sin(angle) * 5;

            // higher spawn so it looks like it's coming from orbit
            Location spawn = center.clone().add(x, 120, z);

            TNTPrimed tnt =
                    (TNTPrimed) world.spawnEntity(
                            spawn,
                            EntityType.TNT
                    );

            // longer fuse so TNT reaches ground first
            tnt.setFuseTicks(140);

            tnt.setYield(8f);
        }
    }
}
