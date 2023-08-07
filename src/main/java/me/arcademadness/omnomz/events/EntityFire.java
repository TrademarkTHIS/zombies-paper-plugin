package me.arcademadness.omnomz.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;
import java.util.List;


public class EntityFire implements Listener {

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    public void onZombieCombust(EntitySpawnEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            Zombie z = (Zombie) event.getEntity();
            z.setShouldBurnInDay(false);
        }
    }

    @EventHandler
    public void onZombieFire(EntityDamageEvent event) {
        if ( event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (event.getEntity().getType() == EntityType.DROPPED_ITEM
                    || event.getEntity().getType() == EntityType.ITEM_FRAME
                    || event.getEntity().getType() == EntityType.ITEM_DISPLAY
                    || event.getEntity().getType() == EntityType.GLOW_ITEM_FRAME) return;
            List<Entity> entities = event.getEntity().getNearbyEntities(0.95, 0.95, 0.95);
            Entity victim = null;
            for (Entity e : entities) {
                if (e.getType() == EntityType.DROPPED_ITEM
                        || e.getType() == EntityType.ITEM_FRAME
                        || e.getType() == EntityType.ITEM_DISPLAY
                        || e.getType() == EntityType.GLOW_ITEM_FRAME) continue;
                if (entities.size() > 2) {
                    if (victim == null)
                        victim = e;
                    if (event.getEntity().getLocation().distance(e.getLocation()) < event.getEntity().getLocation().distance(victim.getLocation())) {
                        if (e.getFireTicks() < 10) {
                            victim = e;
                        }
                    }
                } else {
                    victim = e;
                }
            }
            if (victim != null)
                victim.setFireTicks(80 + victim.getFireTicks());
        }
    }
}