package me.arcademadness.omnomz;

import org.bukkit.Location;
import java.time.Instant;

public class SoundObject {

    final private Location location;
    final private Instant age;
    final private double range;

    public SoundObject(Location l, Instant a, double r) {
        this.location = l;
        this.age = a;
        this.range = r;
    }

    Location getLocation() {
        return this.location;
    }

    Instant getAge() {
        return this.age;
    }

    double getRange() {
        return this.range;
    }

    boolean isSame(SoundObject sound) {
        if (sound == null) return false;
        return (sound.getLocation() == this.location && sound.getAge() == this.age && sound.getRange() == this.range);
    }
}
