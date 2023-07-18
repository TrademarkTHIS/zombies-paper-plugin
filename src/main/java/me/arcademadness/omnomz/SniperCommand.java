package me.arcademadness.omnomz;

import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SniperCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

            ItemStack sniper = new ItemStack(Material.TRIDENT, 1);
            ItemMeta smeta = sniper.getItemMeta();

            smeta.setDisplayName("Zombie Sniper");
            sniper.setItemMeta(smeta);

            sniper.addEnchantment(Enchantment.LOYALTY, 3);
            sniper.addEnchantment(Enchantment.CHANNELING, 1);
            sniper.addEnchantment(Enchantment.VANISHING_CURSE, 1);

            p.getInventory().addItem(sniper);
            p.sendMessage("Zombie Sniper Summoned!");
            return true;
        }
        return false;
    }
}
