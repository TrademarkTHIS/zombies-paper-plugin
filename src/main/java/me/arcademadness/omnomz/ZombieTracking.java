package me.arcademadness.omnomz;

import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.world.GenericGameEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ZombieTracking implements Listener {

    private static final EntityType mobs[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};
    private static final GameEvent quietEvents[] = {GameEvent.STEP, GameEvent.ELYTRA_GLIDE, GameEvent.HIT_GROUND, GameEvent.SWIM, GameEvent.ITEM_INTERACT_FINISH, GameEvent.ITEM_INTERACT_START};
    HashMap<Player, Instant> lastAlert = new HashMap<>();


    @EventHandler
    public void onAlert(GenericGameEvent event) {
        double r = 256;
        if (event.getEntity() != null) {

            //Not a player? get the fuck out of 'ere...
            if (event.getEntity().getType() != EntityType.PLAYER) return;
            Player p = (Player) event.getEntity();

            //Don't run if the player is in creative/spectator.
            if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) return;

            //Don't run if code was run for player less than 5 seconds ago.
            //Can be adjusted later if 5 seconds is not long enough.
            if (lastAlert.get(p) != null) {
                if (Duration.between(lastAlert.get(p), Instant.now()).toMillis() < 5000) return;
            }

            if (Arrays.asList(quietEvents).contains(event.getEvent())) return;

            lastAlert.put(p, Instant.now());
            List<Entity> entities = p.getNearbyEntities(r, r, r);
            for (Entity e : entities) {
                if (Arrays.asList(mobs).contains(e.getType())) {
                    Zombie z = (Zombie) e;
                    if (z.getTarget() == null) {
                        if (e.getLocation().y() >= (p.getLocation().y() - 10) && e.getLocation().y() <= (p.getLocation().y() + 10)) {
                            z.setTarget(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUntarget(EntityTargetLivingEntityEvent event) {
        if (event.getReason() == EntityTargetEvent.TargetReason.FORGOT_TARGET || event.getReason() == EntityTargetEvent.TargetReason.UNKNOWN) {
            if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
                Zombie z = (Zombie) event.getEntity();
                if (z.getTarget() != null) {
                    if (z.getTarget().getType() == EntityType.PLAYER) {
                        Player p = (Player) z.getTarget();
                        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
                    }
                }
                if (z.getTarget() != null) {
                    if (z.getTarget().getLocation().distance(event.getEntity().getLocation()) < 256) {
                        if (z.getTarget().getType().isAlive())
                            event.setCancelled(true);
                    }
                }
            }
        }
    }
}
