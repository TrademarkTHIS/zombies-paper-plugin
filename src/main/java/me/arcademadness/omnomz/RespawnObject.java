package me.arcademadness.omnomz;

import java.time.Instant;

public class RespawnObject {
    final private Instant age;
    final private Integer cooldown;


    public RespawnObject(Instant instant, Integer seconds) {
        this.age = instant;
        this.cooldown = seconds;
    }

    public Instant getAge() { return this.age; }
    public Integer getCooldown() { return this.cooldown; }

}
