package me.arcademadness.omnomz;

import org.bukkit.GameEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.world.GenericGameEvent;

import java.util.List;

public class zombieTracking implements Listener {

    @EventHandler
    public void onSound(GenericGameEvent event) {
        if (event.getEntity() != null && event.getEntity().getType() != null) {

            double r = 256;

            if (event.getEntity().getType() == EntityType.PLAYER) {
                Player p = (Player) event.getEntity();

                //this first one is an example of how we can tweak when zombies chase us
                //the rest are events that don't make sense to trigger a zombie
                if (!p.isSprinting())
                    if (event.getEvent() == GameEvent.STEP)
                        return;
                if (event.getEvent() == GameEvent.ELYTRA_GLIDE)
                    return;
                if (event.getEvent() == GameEvent.HIT_GROUND)
                    return;
                if (event.getEvent() == GameEvent.SWIM)
                    return;
                if (event.getEvent() == GameEvent.ITEM_INTERACT_START)
                    return;
                if (event.getEvent() == GameEvent.ITEM_INTERACT_FINISH)
                    return;

                List<Entity> entities = p.getNearbyEntities(r, r, r);
                for (Entity e : entities) {
                    if (e.getType() == EntityType.ZOMBIE) {
                        if (e.getLocation().y() >= (p.getLocation().y() - 10) && e.getLocation().y() <= (p.getLocation().y() + 10)) {
                            Zombie z = (Zombie) e;

                            if (z.getTarget() != null) {
                                return;
                            } else {
                                z.setTarget(p);
                                p.sendMessage("A Zombie Targeted YOU!!!");

                            }
                        }
                    }
                }
            }
        }
    }
}
