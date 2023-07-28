package me.arcademadness.omnomz;

import me.arcademadness.omnomz.commands.BetweenCommand;
import me.arcademadness.omnomz.commands.HelpCommand;
import me.arcademadness.omnomz.commands.SniperCommand;
import me.arcademadness.omnomz.commands.SummonCommand;
import me.arcademadness.omnomz.events.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    private static Plugin plugin;
    private static final EntityType[] mobs = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER};

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
        pm.registerEvents(new DisableZombieFire(), this);
        pm.registerEvents(new SpawningHostiles(), this);
        pm.registerEvents(new MakeZombieJump(), this);
        pm.registerEvents(new VillagerToZombie(), this);

        int zombiecount = 0;

        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : Objects.requireNonNull(Bukkit.getServer().getWorld(w.getUID())).getEntities()) {
                if (!Arrays.asList(mobs).contains(e.getType())) continue;
                Zombie z = (Zombie) e;
                Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(256);
                FollowSoundGoal goal = new FollowSoundGoal(getPlugin(), z);
                if (!Bukkit.getMobGoals().hasGoal(z, goal.getKey())) {
                    Bukkit.getMobGoals().addGoal(z, 3, goal);
                    zombiecount++;
                }
            }
        }
        if (zombiecount < 0) {
            log.info(zombiecount + " zombies have been given the follow_player_sound goal");
        }

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

