package me.arcademadness.omnomz;

import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.world.GenericGameEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ZombieTracking implements Listener {

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};
    private static final GameEvent[] quietEvents = new GameEvent[]{
            GameEvent.STEP,
            GameEvent.SWIM,
            GameEvent.ELYTRA_GLIDE,
            GameEvent.HIT_GROUND,
            GameEvent.ITEM_INTERACT_START,
            GameEvent.ITEM_INTERACT_FINISH
    };
    HashMap<Player, Instant> lastAlert = new HashMap<>();

    private void alert(Player p, double r) {
        lastAlert.put(p, Instant.now());
        List<Entity> entities = p.getNearbyEntities(r, r, r);
        for (Entity e : entities) {
            if (Arrays.asList(mobs).contains(e.getType())) {
                Zombie z = (Zombie) e;
                if ((e.getLocation().y() > 62 && p.getLocation().y() > 62) || (e.getLocation().y() <= 62 && p.getLocation().y() <= 62) || (e.getLocation().y() < p.getLocation().y()+12 && e.getLocation().y() > p.getLocation().y()-12)) {
                    if (z.getTarget() == null) {
                        z.setTarget(p);
                    } else {
                        if (z.getLocation().distance(p.getLocation()) < z.getLocation().distance(z.getTarget().getLocation())) {
                            z.setTarget(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAlert(GenericGameEvent event) {
        if (event.getEntity() != null) {
            double radius = 256;

            if (event.getEntity().getType() != EntityType.PLAYER) return;
            Player p = (Player) event.getEntity();
            if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) return;
            if (lastAlert.get(p) != null) { if (Duration.between(lastAlert.get(p), Instant.now()).toMillis() < 5000) return; }
            if (Arrays.asList(quietEvents).contains(event.getEvent())) return;

            if (event.getEvent() == GameEvent.SPLASH) radius /= 3;

            p.sendActionBar(String.valueOf(event.getEvent().getKey()));

            this.alert(p, radius);
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        double radius = 256;
        Player p = event.getPlayer();
        this.alert(p, radius);
    }

    @EventHandler
    public void onDamageAlert(EntityDamageByEntityEvent event) {
        double radius = 256;
        if (!(event.getDamager() instanceof Player)) return;
        Player p = (Player) event.getDamager();

        if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) return;

        if (lastAlert.get(p) != null) {
            if (Duration.between(lastAlert.get(p), Instant.now()).toMillis() < 5000) return;
        }

        this.alert(p, radius);
    }

    @EventHandler
    public void onUntarget(EntityTargetLivingEntityEvent event) {
        if (event.getReason() == EntityTargetEvent.TargetReason.FORGOT_TARGET || event.getReason() == EntityTargetEvent.TargetReason.UNKNOWN) {
            if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
                Zombie z = (Zombie) event.getEntity();
                if (z.getTarget() != null) {
                    if (z.getTarget().getWorld().getEnvironment() != z.getWorld().getEnvironment()) return;
                    if (z.getTarget().getType() == EntityType.PLAYER) {
                        Player p = (Player) z.getTarget();
                        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
                    }
                    if (z.getTarget().getLocation().distance(event.getEntity().getLocation()) < 256) {
                        if (z.getTarget().getType().isAlive())
                            event.setCancelled(true);
                    }
                }
            }
        }
    }
}
