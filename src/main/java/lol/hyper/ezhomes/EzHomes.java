/*
 * This file is part of EzHomes.
 *
 * EzHomes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EzHomes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EzHomes.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.ezhomes;

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.commands.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class EzHomes extends JavaPlugin {

    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final Path homesPath = Paths.get(this.getDataFolder() + File.separator + "data");
    public FileConfiguration config = this.getConfig();
    public final Logger logger = this.getLogger();
    public final int CONFIG_VERSION = 1;

    public CommandReload commandReload;
    public CommandHome commandHome;
    public CommandDeleteHome commandDeleteHome;
    public CommandSetHome commandSetHome;
    public CommandHomes commandHomes;
    public CommandWhere commandWhere;
    public CommandUpdateHome commandUpdateHome;
    public CommandHomeRespawn commandHomeRespawn;
    public HomeManagement homeManagement;
    public Events events;

    @Override
    public void onEnable() {
        homeManagement = new HomeManagement(this);
        commandReload = new CommandReload(this);
        commandHome = new CommandHome(this);
        commandSetHome = new CommandSetHome(this);
        commandDeleteHome = new CommandDeleteHome(this);
        commandHomes = new CommandHomes(this);
        commandWhere = new CommandWhere(this);
        commandUpdateHome = new CommandUpdateHome(this);
        commandHomeRespawn = new CommandHomeRespawn(this);
        events = new Events(this);
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            logger.info("Copying default config!");
        }
        loadConfig();
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

        this.getCommand("sethome").setExecutor(commandSetHome);
        this.getCommand("home").setExecutor(commandHome);
        this.getCommand("homes").setExecutor(commandHomes);
        this.getCommand("updatehome").setExecutor(commandUpdateHome);
        this.getCommand("delhome").setExecutor(commandDeleteHome);
        this.getCommand("homesreload").setExecutor(commandReload);
        this.getCommand("where").setExecutor(commandWhere);
        this.getCommand("respawnhome").setExecutor(commandHomeRespawn);

        Bukkit.getPluginManager().registerEvents(events, this);

        new UpdateChecker(this, 82663).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info("You are running the latest version.");
            } else {
                logger.info("There is a new version available! Please download at https://www.spigotmc.org/resources/ezhomes.82663/");
            }
        });

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> homeManagement.cleanEmptyHomeFiles(), 100);

        Metrics metrics = new Metrics(this, 9390);
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        if (config.getInt("config-version") != CONFIG_VERSION) {
            logger.warning("Your config file is outdated! Please regenerate the config.");
        }
    }
}
