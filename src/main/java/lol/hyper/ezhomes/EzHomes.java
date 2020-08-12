package lol.hyper.ezhomes;

import lol.hyper.ezhomes.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public final class EzHomes extends JavaPlugin {

    private static EzHomes instance;
    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final Path homesPath = Paths.get(this.getDataFolder() + File.separator + "data");
    public FileConfiguration config = this.getConfig();
    public final HashMap<Player, Long> teleportCooldowns = new HashMap<>();

    public static EzHomes getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            Bukkit.getLogger().info("[EzHomes] Copying default config!");
        }
        if (!Files.exists(homesPath)) {
            try {
                Files.createDirectory(homesPath);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Unable to create folder " + homesPath.toString() + "! Please make the folder manually or check folder permissions!");
                e.printStackTrace();
            }
        }
        this.getCommand("sethome").setExecutor(new CommandSetHome());
        this.getCommand("home").setExecutor(new CommandHome());
        this.getCommand("homes").setExecutor(new CommandHomes());
        this.getCommand("updatehome").setExecutor(new CommandUpdateHome());
        this.getCommand("delhome").setExecutor(new CommandDeleteHome());
        this.getCommand("homesreload").setExecutor(new CommandReload());
        this.getCommand("where").setExecutor(new CommandWhere());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
