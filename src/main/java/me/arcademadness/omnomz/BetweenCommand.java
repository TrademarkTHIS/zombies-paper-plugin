package me.arcademadness.omnomz;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BetweenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        Player player = (Player) sender;

        Vector playerHead = player.getLocation().add(0, 1, 0).toVector();
        Vector targetHead = target.getLocation().add(0, 1, 0).toVector();

        Vector dir = targetHead.subtract(playerHead);
        BlockIterator iterator = new BlockIterator(player.getWorld(), playerHead, dir, 0, (int) dir.length());
        while (iterator.hasNext()) {
            Block next = iterator.next();
            if (next.getBlockData().isOccluding()) {
                player.sendMessage(next.getType().toString());
            }
        }
        return true;
    }
}
