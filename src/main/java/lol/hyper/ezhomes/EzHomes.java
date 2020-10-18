package lol.hyper.ezhomes;

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.commands.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Logger;

public final class EzHomes extends JavaPlugin {

    private static EzHomes instance;
    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final Path homesPath = Paths.get(this.getDataFolder() + File.separator + "data");
    public FileConfiguration config = this.getConfig();
    public final HashMap<Player, Long> teleportCooldowns = new HashMap<>();
    public Logger logger = this.getLogger();

    public static EzHomes getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            logger.info("Copying default config!");
        }
        if (!Files.exists(homesPath)) {
            try {
                Files.createDirectory(homesPath);
            } catch (IOException e) {
                logger.severe("Unable to create folder " + homesPath.toString() + "! Please make the folder manually or check folder permissions!");
                e.printStackTrace();
            }
        }

        if (!PaperLib.isPaper()) {
            PaperLib.suggestPaper(this);
        } else {
            logger.info("Yay! You are using Paper! We will make teleports async!");
        }

        this.getCommand("sethome").setExecutor(new CommandSetHome());
        this.getCommand("home").setExecutor(new CommandHome());
        this.getCommand("homes").setExecutor(new CommandHomes());
        this.getCommand("updatehome").setExecutor(new CommandUpdateHome());
        this.getCommand("delhome").setExecutor(new CommandDeleteHome());
        this.getCommand("homesreload").setExecutor(new CommandReload());
        this.getCommand("where").setExecutor(new CommandWhere());

        new UpdateChecker(this, 82663).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("You are running the latest version.");
            } else {
                logger.info("There is a new version available! Please download at https://www.spigotmc.org/resources/ezhomes.82663/");
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
