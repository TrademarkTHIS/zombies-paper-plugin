package me.arcademadness.omnomz;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;

public class FollowSoundGoal implements Goal<Zombie> {
    private final GoalKey<Zombie> key;
    private final Mob mob;

    private Instant startTime;
    private Location lastLocation = null;
    private SoundObject targetSound = null;
    private SoundObject oldSound = null;

    private int careTimer = 0;

    public FollowSoundGoal(Plugin plugin, Mob mob) {
        this.key = GoalKey.of(Zombie.class, new NamespacedKey(plugin, "follow_player_sound"));
        this.mob = mob;
        this.startTime = Instant.now();
    }

    @Override
    public boolean shouldActivate() {
        if (mob.getTarget() != null) return false;
        SoundObject check = this.checkSounds();
        if (targetSound == null)
            if (check != null) {
                targetSound = check;
        }
        return (targetSound != null);
    }

    @Override
    public boolean shouldStayActive() {
        if (mob.getTarget() != null) return false;
        SoundObject check = this.checkSounds();
        if (check != null) {
            if (targetSound != null) {
                if (targetSound.getLocation().distance(check.getLocation()) > 6) {
                    if (check.getAge().isAfter(targetSound.getAge())) {
                        if (check.getLocation().distance(mob.getLocation()) < targetSound.getLocation().distance((mob.getLocation()))) {
                            if (check.getLocation().distance(targetSound.getLocation()) > 6) {
                                targetSound = check;
                                careTimer = 0;
                            }
                        }
                    }
                }
            }
        }
        return targetSound != null;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        mob.getPathfinder().stopPathfinding();
    }

    @Override
    public void tick() {
        if (targetSound == null) return;
        if (targetSound.getLocation().distance(mob.getLocation()) > 6) {
            mob.getPathfinder().moveTo(targetSound.getLocation());
        } else {
            oldSound = targetSound;
            targetSound = null;
            startTime = Instant.now();
            this.stop();
        }
        if (mob.getPathfinder().getCurrentPath() != null) {
            if ((mob.getPathfinder().getCurrentPath().getNextPoint() != null && mob.getPathfinder().getCurrentPath().getNextPoint().getBlock().getType() == Material.WATER) || (mob.getPathfinder().getCurrentPath().getFinalPoint() != null && mob.getPathfinder().getCurrentPath().getFinalPoint().getBlock().getType() == Material.WATER)) {
                oldSound = targetSound;
                targetSound = null;
                startTime = Instant.now();
                this.stop();
            }
        }
        if (mob.getTarget() != null) {
            oldSound = targetSound;
            targetSound = null;
            startTime = Instant.now();
            this.stop();
        }
        if (lastLocation == null) lastLocation = mob.getLocation();
        if (mob.getLocation().distance(lastLocation) <= 4) {
            careTimer++;
            if (careTimer > 60) {
                careTimer = 0;
                lastLocation = null;
                oldSound = targetSound;
                targetSound = null;
                startTime = Instant.now();
                this.stop();
            }
        } else {
            lastLocation = mob.getLocation();
            careTimer = 0;
        }
    }


    @Override
    public @NotNull GoalKey<Zombie> getKey() {
        return key;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }

    private SoundObject checkSounds() {
        HashMap<Player, SoundObject> alertSounds = SoundEvents.getAlertSounds();
        SoundObject closestSound = null;
        if (targetSound != null) return null;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            SoundObject s = alertSounds.get(player);
            if (s == null) continue;

            if (s.getLocation().getWorld().getEnvironment() != mob.getLocation().getWorld().getEnvironment()) continue;
            if (s.getAge().isBefore(startTime)) continue;
            if (s.getLocation().distance(mob.getLocation()) > s.getRange()) continue;
            if (s.isSame(oldSound)) continue;

            if (oldSound != null) {
                if (s.getAge().isBefore(oldSound.getAge())) continue;
                if (s.getLocation().distance(oldSound.getLocation()) < 32) {
                    continue;
                }
            }

            if (closestSound == null) closestSound = s;
            if (s.getLocation().distance(mob.getLocation()) < closestSound.getLocation().distance(mob.getLocation())) closestSound = s;
        }
        return closestSound;
    }
}
