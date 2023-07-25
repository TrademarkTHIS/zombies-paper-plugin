package me.arcademadness.omnomz;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.inventory.ItemStack;

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

    private void alert(Player p, double radius) {
        //putting these here (in order they're used) for easy adjustment.
        //this stuff will probably require further tweaking
        int alertDelay = 5000;
        int layerWiggleRoom = 12;
        double finalPointRadius = 12;

        if (lastAlert.get(p) != null) { if (Duration.between(lastAlert.get(p), Instant.now()).toMillis() < alertDelay) return; }
        lastAlert.put(p, Instant.now());

        List<Entity> entities = p.getNearbyEntities(radius, radius, radius);

        int index=0, delay=0;

        for (Entity e : entities) {
            if (Arrays.asList(mobs).contains(e.getType())) {
                Zombie z = (Zombie) e;
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    if ((e.getLocation().y() > 62 && p.getLocation().y() > 62) || (e.getLocation().y() <= 62 && p.getLocation().y() <= 62) || (e.getLocation().y() < p.getLocation().y()+layerWiggleRoom && e.getLocation().y() > p.getLocation().y()-layerWiggleRoom)) {
                        Pathfinder.PathResult zPath = z.getPathfinder().findPath(p.getLocation());
                        if (zPath != null) {
                            if (zPath.getFinalPoint().distance(p.getLocation()) < finalPointRadius) {
                                if (z.getTarget() == null) {
                                    z.setTarget(p);
                                } else {
                                    if (z.getTarget().getEntityId() != p.getEntityId()) {
                                        if (z.getLocation().distance(p.getLocation()) < z.getLocation().distance(z.getTarget().getLocation())) {
                                            z.setTarget(p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, delay);
                index++;
                if (index % 10 == 0) {
                    delay++;
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
            if (Arrays.asList(quietEvents).contains(event.getEvent())) return;

            if (event.getEvent() == GameEvent.SPLASH) radius /= 3;

            this.alert(p, radius);
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player p = event.getPlayer();
        this.alert(p, 256);
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

    @EventHandler
    public void onPickupZombie(EntityPickupItemEvent event) {
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;

        if (event.getItem().getItemStack().isSimilar(new ItemStack(Material.ROTTEN_FLESH))) { event.setCancelled(true); }
    }
}
