package me.arcademadness.omnomz.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;

public class ArmorAndDamage implements Listener {

    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};
    HashMap<Player, Instant> lastHit = new HashMap<>();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        double hitChance = 20;

        LivingEntity victim = (LivingEntity) event.getEntity();
        Entity attacker = event.getDamager();

        if (Arrays.asList(mobs).contains(attacker.getType())) {
            if (victim.getType() == EntityType.PLAYER) {
                Player p = (Player) victim;

                //helmets
                if (p.getInventory().getHelmet() != null) {
                    if (p.getInventory().getHelmet().getType() == Material.NETHERITE_HELMET || p.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET)
                        hitChance -= 3;
                    if (p.getInventory().getHelmet().getType() == Material.GOLDEN_HELMET || p.getInventory().getHelmet().getType() == Material.CHAINMAIL_HELMET || p.getInventory().getHelmet().getType() == Material.IRON_HELMET || p.getInventory().getHelmet().getType() == Material.TURTLE_HELMET)
                        hitChance -= 2;
                    if (p.getInventory().getHelmet().getType() == Material.LEATHER_HELMET)
                        hitChance -= 1;
                }

                //chestplates
                if (p.getInventory().getChestplate() != null) {
                    if (p.getInventory().getChestplate().getType() == Material.NETHERITE_CHESTPLATE || p.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE)
                        hitChance -= 8;
                    if (p.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE)
                        hitChance -= 6;
                    if (p.getInventory().getChestplate().getType() == Material.GOLDEN_CHESTPLATE || p.getInventory().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE)
                        hitChance -= 5;
                    if (p.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE)
                        hitChance -= 4;
                }

                //leggings
                if (p.getInventory().getLeggings() != null) {
                    if (p.getInventory().getLeggings().getType() == Material.NETHERITE_LEGGINGS || p.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS)
                        hitChance -= 6;
                    if (p.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS)
                        hitChance -= 5;
                    if (p.getInventory().getLeggings().getType() == Material.CHAINMAIL_LEGGINGS)
                        hitChance -= 4;
                    if (p.getInventory().getLeggings().getType() == Material.GOLDEN_LEGGINGS)
                        hitChance -= 3;
                    if (p.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS)
                        hitChance -= 2;
                }

                //boots
                if (p.getInventory().getBoots() != null) {
                    if (p.getInventory().getBoots().getType() == Material.NETHERITE_BOOTS || p.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS)
                        hitChance -= 3;
                    if (p.getInventory().getBoots().getType() == Material.IRON_BOOTS)
                        hitChance -= 2;
                    if (p.getInventory().getBoots().getType() == Material.CHAINMAIL_BOOTS || p.getInventory().getBoots().getType() == Material.GOLDEN_BOOTS || p.getInventory().getBoots().getType() == Material.LEATHER_BOOTS)
                        hitChance -= 1;
                }

                double random = Math.random() * 100;

                if (hitChance==20)
                    hitChance=19.8;

                if (lastHit.get(p) != null) {
                    if (Duration.between(lastHit.get(p), Instant.now()).toMillis() < 700) hitChance=20;
                }

                if (random <= (hitChance * 5)) {
                    event.setDamage(2);

                    //Increases amplifier per bite and adds saturation on the first.
                    int amp = 0;
                    if (p.getPotionEffect(PotionEffectType.WITHER) !=null) {
                        amp = p.getPotionEffect(PotionEffectType.WITHER).getAmplifier() + 1;
                    } else {
                        if (p.getFoodLevel() >= 19)
                            p.setSaturation(20 + p.getSaturation());
                    }

                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 24000, amp));

                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1, 1);
                        players.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 1, 1);
                    }
                }
                else {
                    event.setDamage(0.5);
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1.5f);
                    }
                }
                lastHit.put(p, Instant.now());
            } else {
                if (victim.getType() == EntityType.IRON_GOLEM) return;
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 24000, 1));
            }
        }
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        event.getPlayer().removePotionEffect(PotionEffectType.WITHER);
    }
}
