package me.arcademadness.omnomz;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        Logger log = Bukkit.getLogger();

        this.getCommand("zombies").setExecutor(new HelpCommand());
        this.getCommand("zsummon").setExecutor(new SummonCommand());
        this.getCommand("zsniper").setExecutor(new SniperCommand());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ZombieTracking(), this);
        pm.registerEvents(new ArmorAndDamage(), this);
        pm.registerEvents(new DisableZombieFire(), this);
        pm.registerEvents(new SpawningHostiles(), this);
        pm.registerEvents(new MakeZombieJump(), this);
        //pm.registerEvents(new SubHardcore(), this);

        log.info("omnnomz loaded successfully :)");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}

