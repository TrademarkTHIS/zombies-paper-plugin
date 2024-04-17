package me.arcademadness.omnomz;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
    static HashMap<Player, RespawnObject> respawns = new HashMap<>();

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
        if (hasRespawnProtection(p)) return; //if zombies are bugged, this is why

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

        if (Arrays.asList(mobs).contains(event.getEntity().getType())) {
            if (hasRespawnProtection(p)) {
                respawns.remove(p);
            }
        }
        this.alert(p, event.getEntity().getLocation(), 256);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        for (Player p : event.getLocation().getNearbyPlayers(64)) {
            if (p != null) {
                this.alert(p, event.getLocation(), 256);
                return;
            }
        }
    }

    @EventHandler
    public void onAnvil(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.ANVIL || event.getBlock().getType() == Material.CHIPPED_ANVIL || event.getBlock().getType() == Material.DAMAGED_ANVIL) {
            for (Player p : event.getBlock().getLocation().getNearbyPlayers(32)) {
                if (p != null) {
                    this.alert(p, event.getBlock().getLocation(), 256);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onNewZombie(EntityAddToWorldEvent event) {
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;
        if (event.getEntity().getType() != EntityType.DROWNED) {
            Zombie z = (Zombie) event.getEntity();
            FollowSoundGoal goal = new FollowSoundGoal(Main.getPlugin(), z);
            if (!Bukkit.getMobGoals().hasGoal(z, goal.getKey())) {
                Bukkit.getMobGoals().addGoal(z, 3, goal);
            }
        }
    }

    @EventHandler
    public void onLoad(PluginEnableEvent event) {
        Logger log = Bukkit.getLogger();
        int zombiecount = 0;
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : Objects.requireNonNull(Bukkit.getServer().getWorld(w.getKey())).getEntities()) {
                if (!Arrays.asList(mobs).contains(e.getType())) continue;
                if (e.getType() != EntityType.DROWNED) {
                    Zombie z = (Zombie) e;
                    FollowSoundGoal goal = new FollowSoundGoal(Main.getPlugin(), z);
                    if (!Bukkit.getMobGoals().hasGoal(z, goal.getKey())) {
                        Bukkit.getMobGoals().addGoal(z, 3, goal);
                    }
                }
                zombiecount++;
            }
        }
        if (zombiecount > 0) {
            log.info(zombiecount + " zombies have been given the follow_player_sound goal");
        }
    }

    //The following is to handle the respawn cooldown stuff


    final int bedTime = 30;
    final int spawnTime = 120;

    public boolean hasRespawnProtection(Player player) {
        if (respawns.get(player) == null) return false;

        /*
        * This is the most iffy part because I'm not sure what the duration is going to return.
        * Documentation states that if the duration is OVER 0, that represents going over the allotted time
        * If it's under 0, that means the time hasn't fully elapsed yet.
        */

        int duration = Duration.between(respawns.get(player).getAge(), Instant.now()).compareTo(Duration.ofSeconds(respawns.get(player).getCooldown()));
        if (duration < 0) { return true; }

        respawns.remove(player);
        return false;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getBedSpawnLocation() == null) {
            respawns.put(event.getPlayer(), new RespawnObject(Instant.now(), spawnTime));
        } else {
            respawns.put(event.getPlayer(), new RespawnObject(Instant.now(), bedTime));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        respawns.put(event.getPlayer(), new RespawnObject(Instant.now(), spawnTime));
    }

    @EventHandler
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player)) return;
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;

        Player player = (Player) event.getTarget();
        if (hasRespawnProtection(player)) {
            event.setCancelled(true);
        }
    }





}
