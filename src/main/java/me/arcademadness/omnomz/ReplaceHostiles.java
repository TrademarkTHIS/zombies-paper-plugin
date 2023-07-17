package me.arcademadness.omnomz;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;

public class ReplaceHostiles implements Listener {

    private static final EntityType mobs[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) return;

        if (event.getEntity() instanceof Monster) {
            if (event.getEntity().getLocation().y() > 62) {
                event.setCancelled(true);
                event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.ZOMBIE);
            }
        }
    }
}
