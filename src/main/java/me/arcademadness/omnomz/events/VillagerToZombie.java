package me.arcademadness.omnomz.events;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;


public class VillagerToZombie implements Listener {

    private static final EntityDamageEvent.DamageCause[] goodDamage = {
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
    };

    @EventHandler
    public void onEntityDeath(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Villager)) return;
        Villager v = (Villager) event.getEntity();
        if (v.getHealth() - event.getFinalDamage() <= 0) {
            if (Arrays.asList(goodDamage).contains(event.getCause())) {
                event.setCancelled(true);
                v.zombify();
                v.setRemoveWhenFarAway(false);
            }
        }
    }
}
