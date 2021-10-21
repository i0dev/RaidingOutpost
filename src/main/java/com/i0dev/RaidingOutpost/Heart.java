package com.i0dev.RaidingOutpost;

import com.i0dev.RaidingOutpost.commands.CmdRaidingOutpost;
import com.i0dev.RaidingOutpost.config.GeneralConfig;
import com.i0dev.RaidingOutpost.config.MessageConfig;
import com.i0dev.RaidingOutpost.config.StorageConfig;
import com.i0dev.RaidingOutpost.handlers.RpostHandler;
import com.i0dev.RaidingOutpost.hooks.PlaceHolderAPIExpansion;
import com.i0dev.RaidingOutpost.managers.MessageManager;
import com.i0dev.RaidingOutpost.managers.RpostManager;
import com.i0dev.RaidingOutpost.templates.AbstractCommand;
import com.i0dev.RaidingOutpost.templates.AbstractConfiguration;
import com.i0dev.RaidingOutpost.templates.AbstractListener;
import com.i0dev.RaidingOutpost.templates.AbstractManager;
import com.i0dev.RaidingOutpost.utility.ConfigUtil;
import com.massivecraft.factions.entity.BoardColl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Heart extends JavaPlugin {

    List<AbstractManager> managers = new ArrayList<>();
    List<AbstractConfiguration> configs = new ArrayList<>();


    boolean usingPapi;
    boolean usingMCoreFactions;

    @Override
    public void onEnable() {

        usingPapi = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        Plugin factions = getServer().getPluginManager().getPlugin("Factions");
        usingMCoreFactions = factions != null && factions.getDescription().getAPIVersion() != null && factions.getDescription().getAPIVersion().startsWith("1.15");

        managers.addAll(Arrays.asList(
                new MessageManager(this),
                new RpostManager(this),
                new RpostHandler(this),
                new CmdRaidingOutpost(this, "RaidingOutpost")
        ));

        configs.addAll(Arrays.asList(
                new GeneralConfig(this, getDataFolder() + "/General.json"),
                new StorageConfig(this, getDataFolder() + "/Storage.json"),
                new MessageConfig(this, getDataFolder() + "/Messages.json")
        ));

        if (usingPapi) {
            new PlaceHolderAPIExpansion(this).register();
        }

        reload();
        registerManagers();

        System.out.println("\u001B[32m" + this.getDescription().getName() + " by: " + this.getDescription().getAuthors().get(0) + " has been enabled.");
    }

    public void reload() {
        // old ~ new
        ArrayList<MessageManager.Pair<AbstractConfiguration, AbstractConfiguration>> toReplace = new ArrayList<>();
        configs.forEach(abstractConfiguration -> toReplace.add(new MessageManager.Pair<>(abstractConfiguration, ConfigUtil.load(abstractConfiguration, this))));
        toReplace.forEach(pairs -> {
            configs.remove(pairs.getKey());
            configs.add(pairs.getValue());
        });
    }


    @Override
    public void onDisable() {
        configs.clear();
        managers.forEach(AbstractManager::deinitialize);
        HandlerList.unregisterAll(this);
        managers.clear();
        Bukkit.getScheduler().cancelTasks(this);
        System.out.println("\u001B[31m" + this.getDescription().getName() + " by: " + this.getDescription().getAuthors().get(0) + " has been disabled.");
    }

    public <T> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T> T getConfig(Class<T> clazz) {
        return (T) configs.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public void registerManagers() {
        managers.forEach(abstractManager -> {
            if (abstractManager.isLoaded()) abstractManager.deinitialize();
            if (abstractManager instanceof AbstractListener)
                getServer().getPluginManager().registerEvents((AbstractListener) abstractManager, this);
            else if (abstractManager instanceof AbstractCommand) {
                getCommand(((AbstractCommand) abstractManager).getCommand()).setExecutor(((AbstractCommand) abstractManager));
                getCommand(((AbstractCommand) abstractManager).getCommand()).setTabCompleter(((AbstractCommand) abstractManager));
            }
            abstractManager.initialize();
            abstractManager.setLoaded(true);
        });
    }

    public GeneralConfig cnf() {
        return getConfig(GeneralConfig.class);
    }

    public MessageConfig msg() {
        return getConfig(MessageConfig.class);
    }

    public MessageManager msger() {
        return getManager(MessageManager.class);
    }

    public StorageConfig storage() {
        return getConfig(StorageConfig.class);
    }


}
