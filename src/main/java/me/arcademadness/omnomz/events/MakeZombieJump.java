package me.arcademadness.omnomz.events;

import io.papermc.paper.event.entity.EntityMoveEvent;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class MakeZombieJump implements Listener {

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    private void onZombieMove(EntityMoveEvent event) {
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;
        Zombie z = (Zombie) event.getEntity();
        if (z.getTarget() == null) return;
        if (z.getTarget().getWorld().getEnvironment() != z.getWorld().getEnvironment()) return;

        Location targetLoc = new Location(z.getTarget().getWorld(), z.getTarget().getLocation().x(), 0 , z.getTarget().getLocation().y());
        Location zombieLoc = new Location(z.getWorld(), z.getLocation().x(), 0 , z.getLocation().y());
        if (targetLoc.distance(zombieLoc) > 32) {
            if (!z.hasLineOfSight(z.getTarget().getEyeLocation())) {
                z.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 4, false, false));
            }
        }
        if (z.getTarget().getLocation().distance(z.getLocation()) < 1.7) {
            z.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false));
        }
        if (!z.hasLineOfSight(z.getTarget())) {
            if (z.getTarget().getLocation().y() + 1 > z.getLocation().y()) {
                if (z.getTarget().getLocation().distance(z.getLocation()) <= 6) {
                    z.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 2, false, false));
                    z.setJumping(true);
                }
            }
        }
    }
}
