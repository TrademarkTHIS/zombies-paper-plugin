package me.arcademadness.omnomz;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger log = Bukkit.getLogger();

        this.getCommand("zombies").setExecutor(new HelpCommand());
        this.getCommand("zsummon").setExecutor(new SummonCommand());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new zombieTracking(), this);
        pm.registerEvents(new ArmorAndDamage(), this);

        log.info("omnnomz loaded successfully :)");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

