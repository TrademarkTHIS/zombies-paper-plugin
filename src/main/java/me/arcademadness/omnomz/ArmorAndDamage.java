package me.arcademadness.omnomz;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ArmorAndDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        LivingEntity victim = (LivingEntity) event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker.getType() == EntityType.ZOMBIE) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 24000, 2));
        }
    }

}
