package me.arcademadness.omnomz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SummonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            int i;
            for (i = 0; i < 10; i++) {
                p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
            }
            p.sendMessage("Summoned " + (i+1) + " zombies!");
            return true;
        }
        return false;
    }
}
