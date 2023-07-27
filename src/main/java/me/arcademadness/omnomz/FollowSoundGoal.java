package me.arcademadness.omnomz;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private Location startSpot = null;
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
        SoundObject check = this.checkSounds();
        if (targetSound == null)
            if (check != null) {
                targetSound = check;
        }
        return (targetSound != null);
    }

    @Override
    public boolean shouldStayActive() {
        SoundObject check = this.checkSounds();

        if (targetSound != null) {
            if (check.getAge().isAfter(targetSound.getAge())) {
                if (check.getLocation().distance(mob.getLocation()) < targetSound.getLocation().distance((mob.getLocation()))) {
                    targetSound = check;
                    careTimer = 0;
                }
            }
        }
        return targetSound != null;
    }

    @Override
    public void start() {
        startSpot = mob.getLocation();
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
        if (lastLocation == null) lastLocation = mob.getLocation();
        if (mob.getLocation().distance(lastLocation) < 16) {
            careTimer++;
            if (careTimer > 240) {
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
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            SoundObject s = alertSounds.get(player);
            if (s != null) {
                if (s.getLocation().getWorld().getEnvironment() == mob.getLocation().getWorld().getEnvironment()) {
                    if (s.getAge().isAfter(startTime)) {
                        if (s.getLocation().distance(mob.getLocation()) < s.getRange()) {
                            if (!s.isSame(oldSound)) {
                                if (closestSound == null) {
                                    closestSound = s;
                                }
                                if (s.getLocation().distance(mob.getLocation()) < closestSound.getLocation().distance(mob.getLocation())) {
                                    if (oldSound != null) {
                                        if (s.getAge().isAfter(oldSound.getAge())) {
                                            closestSound = s;
                                        }
                                    } else {
                                        closestSound = s;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return closestSound;
    }
}
