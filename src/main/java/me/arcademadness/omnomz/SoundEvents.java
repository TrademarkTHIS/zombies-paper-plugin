package me.arcademadness.omnomz;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;

public class SoundEvents implements Listener {

    static HashMap<Player, SoundObject> alertSounds = new HashMap<>();

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};
    private static final GameEvent[] quietEvents = new GameEvent[]{
            GameEvent.STEP,
            GameEvent.SWIM,
            GameEvent.ELYTRA_GLIDE,
            GameEvent.HIT_GROUND,
            GameEvent.ITEM_INTERACT_START,
            GameEvent.ITEM_INTERACT_FINISH
    };
    public void alert(Player p, Location l, double radius) {
        SoundObject sound = new SoundObject(l, Instant.now(), radius);
        alertSounds.put(p, sound);
    }

    public static HashMap<Player, SoundObject> getAlertSounds() {
        return alertSounds;
    }

    @EventHandler
    public void onGenericSound(GenericGameEvent event) {
        if (Arrays.asList(quietEvents).contains(event.getEvent())) return;
        if (event.getEntity() == null) return;
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        if (event.getEntity() != null) {
            this.alert((Player) event.getEntity(), event.getLocation(), 2000);
        }
    }

    @EventHandler
    public void onNewZombie(EntityAddToWorldEvent event) {
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;
        Zombie z = (Zombie) event.getEntity();
        FollowSoundGoal goal = new FollowSoundGoal(Main.getPlugin(), z);
        if (!Bukkit.getMobGoals().hasGoal(z, goal.getKey())) {
            Bukkit.getMobGoals().addGoal(z, 3, goal);
        }
    }
}
