package me.arcademadness.omnomz.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class MilkEvent implements Listener {

    @EventHandler
    public void onMilk(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.COW) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BUCKET) return;
        event.setCancelled(true);
        ItemStack milk = new ItemStack(Material.MILK_BUCKET);
        ItemMeta milkMeta = milk.getItemMeta();
        milkMeta.setCustomModelData(3);
        List<Component> milkLore = new ArrayList<>();
        milkLore.add(Component.text(Math.round(milkMeta.getCustomModelData()*333.333333333) + "mB",TextColor.fromHexString("#AAAAAA")).decoration(TextDecoration.ITALIC, false));
        milkMeta.lore(milkLore);
        milk.setItemMeta(milkMeta);


        if (event.getPlayer().getInventory().getItem(event.getHand()).getAmount()>1) {
            event.getPlayer().getInventory().getItem(event.getHand()).setAmount(event.getPlayer().getInventory().getItem(event.getHand()).getAmount()-1);
        } else {
            event.getPlayer().getInventory().setItem(event.getHand(), milk);
            return;
        }

        if (event.getPlayer().getInventory().firstEmpty() != -1) {
            event.getPlayer().getInventory().addItem(milk);
            return;
        }

        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), milk);
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.MILK_BUCKET) {
            if (item.getItemMeta().hasCustomModelData()) {
                if (item.getItemMeta().getCustomModelData() > 1) {
                    event.setCancelled(true);
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(item.getItemMeta().getCustomModelData()-1);
                    List<Component> milkLore = new ArrayList<>();
                    milkLore.add(Component.text(Math.round(meta.getCustomModelData()*333.333333333) + "mB",TextColor.fromHexString("#AAAAAA")).decoration(TextDecoration.ITALIC, false));
                    meta.lore(milkLore);
                    event.getPlayer().getInventory().getItem(event.getHand()).setItemMeta(meta);
                    for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
                        event.getPlayer().removePotionEffect(effect.getType());
                    }
                }
            }
        }
    }
}
