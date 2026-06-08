package me.anti.orbital;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player)) return true;

    Player player = (Player) sender;

    // OP CHECK
    if (!player.isOp()) {
        player.sendMessage("§cOnly server operators can use this command.");
        return true;
    }

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

    player.sendMessage("§aYou received an Orbital Trident!");

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

        // ⚡ effect start
        world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 3.0f, 0.6f);
        world.strikeLightningEffect(center);

        int rings = 4;        // MORE rings
        int density = 12;     // points per ring

        for (int r = 0; r < rings; r++) {

            double radius = 5 + (r * 4);

            for (int i = 0; i < density; i++) {

                double angle = (2 * Math.PI / density) * i;

                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;

                Location strike = center.clone().add(x, 0, z);

                // 💥 GUARANTEED EXPLOSION = NO TNT RELIABILITY ISSUES
                world.createExplosion(
                        strike,
                        4.5f,   // power per strike
                        true,   // block damage = CRATER ENABLED
                        true    // fire
                );
            }
        }

        // 💣 CORE CRATER (main impact)
        Bukkit.getScheduler().runTaskLater(this, () -> {

            world.createExplosion(
                    center,
                    12f,   // BIG crater
                    true,
                    true
            );

        }, 20L);
    }
}
