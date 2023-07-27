package me.arcademadness.omnomz.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;

            p.sendMessage(ChatColor.GRAY + "-----Zombie Help Menu!-----" + "\n"
                    + ChatColor.GREEN + "/Zombies" + ChatColor.GRAY + ": Displays this menu. :)" + "\n"
                    + ChatColor.GREEN + "/ZSummon [<AMOUNT>]" + ChatColor.GRAY + ": Summons a specific number of zombies at your location." + "\n"
                    + ChatColor.GREEN + "/ZSniper" + ChatColor.GRAY + ": Gives you a trident with mystical undead powers." + "\n");
            return true;
        }
        return false;
    }// :)
}
