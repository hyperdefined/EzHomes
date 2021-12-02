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
import lol.hyper.ezhomes.events.InventoryClick;
import lol.hyper.ezhomes.events.PlayerLeave;
import lol.hyper.ezhomes.events.PlayerRespawn;
import lol.hyper.ezhomes.tools.HomeManagement;
import lol.hyper.githubreleaseapi.GitHubRelease;
import lol.hyper.githubreleaseapi.GitHubReleaseAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class EzHomes extends JavaPlugin {

    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final Logger logger = this.getLogger();
    public final int CONFIG_VERSION = 2;
    public FileConfiguration config = this.getConfig();
    public CommandReload commandReload;
    public CommandHome commandHome;
    public CommandDeleteHome commandDeleteHome;
    public CommandSetHome commandSetHome;
    public CommandHomes commandHomes;
    public CommandWhere commandWhere;
    public CommandUpdateHome commandUpdateHome;
    public CommandHomeRespawn commandHomeRespawn;
    public HomeManagement homeManagement;
    public InventoryClick inventoryClick;
    public PlayerLeave playerLeave;
    public PlayerRespawn playerRespawn;

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
        inventoryClick = new InventoryClick(this);
        playerLeave = new PlayerLeave(this);
        playerRespawn = new PlayerRespawn(this);
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            logger.info("Copying default config!");
        }
        loadConfig();

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

        Bukkit.getPluginManager().registerEvents(inventoryClick, this);
        Bukkit.getPluginManager().registerEvents(playerLeave, this);
        Bukkit.getPluginManager().registerEvents(playerRespawn, this);

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> homeManagement.cleanEmptyHomeFiles(), 100);

        new Metrics(this, 9390);

        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        if (config.getInt("config-version") != CONFIG_VERSION) {
            logger.warning("Your config file is outdated! Please regenerate the config.");
        }
    }

    public void checkForUpdates() {
        GitHubReleaseAPI api;
        try {
            api = new GitHubReleaseAPI("EzHomes", "hyperdefined");
        } catch (IOException e) {
            logger.warning("Unable to check updates!");
            e.printStackTrace();
            return;
        }
        GitHubRelease current = api.getReleaseByTag(this.getDescription().getVersion());
        GitHubRelease latest = api.getLatestVersion();
        if (current == null) {
            logger.warning("You are running a version that does not exist on GitHub. If you are in a dev environment, you can ignore this. Otherwise, this is a bug!");
            return;
        }
        int buildsBehind = api.getBuildsBehind(current);
        if (buildsBehind == 0) {
            logger.info("You are running the latest version.");
        } else {
            logger.warning("A new version is available (" + latest.getTagVersion() + ")! You are running version " + current.getTagVersion() + ". You are " + buildsBehind + " version(s) behind.");
        }
    }
}
