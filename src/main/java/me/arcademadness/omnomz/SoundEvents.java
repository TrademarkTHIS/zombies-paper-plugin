package me.arcademadness.omnomz;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.GenericGameEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class SoundEvents implements Listener {

    static HashMap<Player, SoundObject> alertSounds = new HashMap<>();

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};
    private static final GameEvent[] quietEvents = new GameEvent[]{
            GameEvent.STEP,
            GameEvent.SWIM,
            GameEvent.ELYTRA_GLIDE,
            GameEvent.HIT_GROUND,
            GameEvent.ITEM_INTERACT_START,
            GameEvent.ITEM_INTERACT_FINISH,
            GameEvent.ENTITY_MOUNT,
            GameEvent.ENTITY_DISMOUNT,
    };
    public void alert(Player p, Location l, double radius) {
        if (alertSounds.get(p) != null) {
            if (Duration.between(alertSounds.get(p).getAge(), Instant.now()).toMillis() < 5000) {
                return;
            }
        }
        SoundObject sound = new SoundObject(l, Instant.now(), radius);
        alertSounds.put(p, sound);
    }

    public static HashMap<Player, SoundObject> getAlertSounds() {
        return alertSounds;
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        this.alert(event.getPlayer(), event.getItemDrop().getLocation(), 32);
    }
    @EventHandler
    public void onGenericSound(GenericGameEvent event) {
        double radius = 64;
        if (Arrays.asList(quietEvents).contains(event.getEvent())) return;
        if (event.getEntity() == null) return;
        if (event.getEntity().getType() != EntityType.PLAYER) return;

        if (event.getEvent() == GameEvent.SPLASH || event.getEvent() == GameEvent.EQUIP) radius = 32;
        this.alert((Player) event.getEntity(), event.getLocation(), radius);
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player p = event.getPlayer();
        this.alert(p, event.getPlayer().getLocation(), 256);
    }

    @EventHandler
    public void onDamageAlert(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player p = (Player) event.getDamager();

        this.alert(p, event.getEntity().getLocation(), 256);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        for (Player p : event.getLocation().getNearbyPlayers(32)) {
            if (p != null) {
                this.alert(p, event.getLocation(), 256);
                return;
            }
        }
    }

    @EventHandler
    public void onNewZombie(EntityAddToWorldEvent event) {
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;
        Zombie z = (Zombie) event.getEntity();
        Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(256);
        FollowSoundGoal goal = new FollowSoundGoal(Main.getPlugin(), z);
        if (!Bukkit.getMobGoals().hasGoal(z, goal.getKey())) {
            Bukkit.getMobGoals().addGoal(z, 3, goal);
        }
    }

    @EventHandler
    public void onLoad(PluginEnableEvent event) {
        Logger log = Bukkit.getLogger();
        log.info("HELLO FRIENDS!");
        int zombiecount = 0;
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : Bukkit.getServer().getWorld(w.getKey()).getEntities()) {
                if (e == null) continue;
                if (!Arrays.asList(mobs).contains(e.getType())) continue;
                Zombie z = (Zombie) e;
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(256);
                FollowSoundGoal goal = new FollowSoundGoal(Main.getPlugin(), z);
                Bukkit.getMobGoals().removeGoal(z, goal);
                Bukkit.getMobGoals().addGoal(z, 3, goal);

                zombiecount++;
            }
        }
        if (zombiecount > 0) {
            log.info(zombiecount + " zombies have been given the follow_player_sound goal");
        }
    }
}
