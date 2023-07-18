package me.arcademadness.omnomz;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;
import java.util.List;

public class ReplaceHostiles implements Listener {

    private static final EntityType mobs[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            LivingEntity le = (LivingEntity) event.getEntity();
            if (le.getLocation().y() > 62) {
                le.setRemoveWhenFarAway(false);
                List<Entity> entities = event.getEntity().getNearbyEntities(15, 15, 15);
                for (Entity e : entities) {
                    if (Arrays.asList(mobs).contains(e.getType())) {
                        Zombie z = (Zombie) e;
                        Zombie eventZombie = (Zombie) event.getEntity();
                        if (z.getTarget() != null){
                            eventZombie.setTarget(z.getTarget());
                        }
                    }
                }
            }
            return;
        }

        if (event.getEntity() instanceof Monster) {
            if (event.getEntity().getLocation().y() > 62) {
                if (event.getEntity().getWorld().getEnvironment() == World.Environment.NORMAL) {
                    event.setCancelled(true);
                    //event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.ZOMBIE);
                }
            }
        }
    }
}
