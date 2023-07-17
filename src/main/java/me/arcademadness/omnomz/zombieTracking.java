package me.arcademadness.omnomz;

import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import org.bukkit.entity.Entity;
import org.bukkit.event.world.GenericGameEvent;

import java.util.List;

public class zombieTracking implements Listener {

    @EventHandler
    public void onSound(GenericGameEvent event) {
        double r = 256;
        Location l = event.getLocation();
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player p = (Player) event.getEntity();

            if (p.isSprinting() == false)
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

                        if (z.getTarget().getType() == EntityType.PLAYER)
                        {
                            z.setTarget(p);
                            p.sendMessage("A Zombie Targeted YOU!!!");
                        }
                    }
                }
            }
        }
    }
}
