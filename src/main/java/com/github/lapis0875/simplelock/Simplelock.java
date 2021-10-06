package com.github.lapis0875.simplelock;

import com.github.lapis0875.simplelock.commands.LockCommandsExecutor;
import com.github.lapis0875.simplelock.listeners.BlockEventListener;
import com.github.lapis0875.simplelock.listeners.PlayerEventListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Simplelock extends JavaPlugin {

    public static Simplelock instance() {
        return Simplelock.getPlugin(Simplelock.class);
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        CommandExecutor commandExecutor = new LockCommandsExecutor();
        getCommand(Constants.CMD_LOCK).setExecutor(commandExecutor);
        getCommand(Constants.CMD_UNLOCK).setExecutor(commandExecutor);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockEventListener(), this);
        getSLF4JLogger().info("Loaded.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getSLF4JLogger().info("Unloaded.");
    }
}
