package me.arcademadness.omnomz;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Omnomz extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("zombies").setExecutor(new HelpCommand());
        this.getCommand("zsummon").setExecutor(new SummonCommand());


        Bukkit.getServer().getPluginManager().registerEvents(new zombieTracking(), this);
        System.out.println("omnnomz loaded successfully :)");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

