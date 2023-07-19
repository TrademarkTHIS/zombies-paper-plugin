package me.arcademadness.omnomz;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;

public class SubHardcore implements Listener {

    HashMap<Player, Location> deathLocation = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NORMAL) {
            Location loc = event.getPlayer().getLocation();
            deathLocation.put(event.getPlayer(), loc);
            if (event.getPlayer().getWorld().getFullTime() < 23500 && event.getPlayer().getWorld().getFullTime() > 13000) {
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    event.getPlayer().spigot().respawn();
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerInstall(PlayerRespawnEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (event.getPlayer().getWorld().getFullTime() < 23500 && event.getPlayer().getWorld().getFullTime() > 13000) {
                event.setRespawnLocation(deathLocation.get(event.getPlayer()));
            }
        }
    }
}
