package me.arcademadness.omnomz;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.List;


public class DisableZombieFire implements Listener {

    private static final EntityType mobs[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    public void onZombieCombust(EntityCombustEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) event.setCancelled(true);
    }



    @EventHandler
    public void onZombieDamage(EntityDamageEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                event.getEntity().setFireTicks(80);
            }
            if ( event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                event.getEntity().setFireTicks(300);
            }
            if ( event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                List<Entity> entities = event.getEntity().getNearbyEntities(0.95, 0.95, 0.95);

                Entity victim = null;
                for (Entity e : entities) {
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

    @EventHandler
    public void onZombieEntityDamage(EntityDamageByEntityEvent event) {
        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            if (event.getDamager().getType() == EntityType.PLAYER) {
                Player p = (Player) event.getDamager();
                if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.FIRE_ASPECT)) {
                    event.getEntity().setFireTicks(80 * p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT));
                }
                if (p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_FIRE)) {
                    event.getEntity().setFireTicks(120);
                }
            }
        }
    }
}
