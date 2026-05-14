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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class OrbitalTrident extends JavaPlugin implements Listener {

    private NamespacedKey orbitalKey;

    @Override
    public void onEnable() {
        orbitalKey = new NamespacedKey(this, "orbital_trident");
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

        meta.getPersistentDataContainer().set(
                orbitalKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        trident.setItemMeta(meta);

        player.getInventory().addItem(trident);

        return true;
    }

    @EventHandler
public void onTridentHit(ProjectileHitEvent event) {

    if (!(event.getEntity() instanceof Trident)) return;
    Trident trident = (Trident) event.getEntity();

    if (event.getHitBlock() == null) return;

    ItemMeta meta = trident.getItemStack().getItemMeta();
    if (meta == null) return;

    Byte tag = meta.getPersistentDataContainer().get(
            orbitalKey,
            PersistentDataType.BYTE
    );

    if (tag == null || tag != 1) return;

    Location target = event.getHitBlock().getLocation();
    spawnOrbitalStrike(target);
}
    private void spawnOrbitalStrike(Location center) {

    World world = center.getWorld();

    world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 3.0f, 0.5f);
    world.strikeLightningEffect(center);

    int rings = 3;
    int baseRadius = 6;
    int tntPerRing = 18;

    for (int r = 0; r < rings; r++) {

        int radius = baseRadius + (r * 5);

        for (int i = 0; i < tntPerRing; i++) {

            double angle = (2 * Math.PI / tntPerRing) * i;

            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location spawn = center.clone().add(x, 90, z);

            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(spawn, EntityType.TNT);

            // ✔ safer vector usage (no full package call)
            tnt.setVelocity(new Vector(0, -1.2, 0));

            tnt.setFuseTicks(100);
            tnt.setYield(5.5f);
        }
    }

    Bukkit.getScheduler().runTaskLater(this, () -> {
        world.createExplosion(center, 9f, false, false);
    }, 60L);
}
