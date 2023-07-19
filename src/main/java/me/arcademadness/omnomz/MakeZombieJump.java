package me.arcademadness.omnomz;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.function.Supplier;

public class MakeZombieJump implements Listener {

    private static final EntityType mobs[] = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

    @EventHandler
    private void onZombieMove(EntityMoveEvent event) {
        if (!Arrays.asList(mobs).contains(event.getEntity().getType())) return;
        Zombie z = (Zombie) event.getEntity();
        if (z.getTarget() != null)
            if (!z.hasLineOfSight(z.getTarget()))
                if (z.getTarget().getLocation().y() > z.getLocation().y())
                    z.setJumping(true);
    }
}
