package me.arcademadness.omnomz;

import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SpawningHostiles implements Listener {

    private static final EntityType mobs[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.HUSK, EntityType.ZOMBIE_VILLAGER};
    private static final EntityType persistent[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.HUSK};

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            LivingEntity le = (LivingEntity) event.getEntity();
            List<Entity> totalSaved = new ArrayList<>();

            le.setHealth(le.getHealth() / 3);
            le.setMaxHealth(le.getMaxHealth() / 3);

            if (le.getLocation().y() > 62) {
                List<Entity> totalEntities = event.getLocation().getWorld().getEntities();
                for (Entity e : totalEntities) {
                    if (Arrays.asList(persistent).contains(e.getType())) {
                        LivingEntity livingTotal = (LivingEntity) e;
                        if (!livingTotal.getRemoveWhenFarAway()) {
                            if (livingTotal != null) totalSaved.add(livingTotal);
                        }
                    }
                }
                if (totalSaved.size() >= event.getLocation().getWorld().getPlayerCount() * 70) {
                    if (event.getLocation().getBlock().getLightFromSky() > 3) {
                        int randomZombieIndex = ThreadLocalRandom.current().nextInt(totalSaved.size()) % totalSaved.size();
                        LivingEntity livingBoy = (LivingEntity) totalSaved.get(randomZombieIndex);
                        livingBoy.setRemoveWhenFarAway(true);
                    }
                }
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
