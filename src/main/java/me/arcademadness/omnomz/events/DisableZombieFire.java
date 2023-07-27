package me.arcademadness.omnomz.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;


public class DisableZombieFire implements Listener {

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    public void onZombieCombust(EntitySpawnEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            Zombie z = (Zombie) event.getEntity();
            z.setShouldBurnInDay(false);
        }
    }
}