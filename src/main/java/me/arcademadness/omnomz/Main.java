package me.arcademadness.omnomz;

import me.arcademadness.omnomz.commands.BetweenCommand;
import me.arcademadness.omnomz.commands.HelpCommand;
import me.arcademadness.omnomz.commands.SniperCommand;
import me.arcademadness.omnomz.commands.SummonCommand;
import me.arcademadness.omnomz.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    private static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        Logger log = Bukkit.getLogger();

        Objects.requireNonNull(this.getCommand("zombies")).setExecutor(new HelpCommand());
        Objects.requireNonNull(this.getCommand("zsummon")).setExecutor(new SummonCommand());
        Objects.requireNonNull(this.getCommand("zsniper")).setExecutor(new SniperCommand());
        Objects.requireNonNull(this.getCommand("zbetween")).setExecutor(new BetweenCommand());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SoundEvents(), this);
        pm.registerEvents(new ArmorAndDamage(), this);
        pm.registerEvents(new EntityFire(), this);
        pm.registerEvents(new SpawningHostiles(), this);
        pm.registerEvents(new MakeZombieJump(), this);
        pm.registerEvents(new VillagerToZombie(), this);

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

