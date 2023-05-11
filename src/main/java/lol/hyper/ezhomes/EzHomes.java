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
import lol.hyper.ezhomes.events.*;
import lol.hyper.ezhomes.tools.HomeManagement;
import lol.hyper.githubreleaseapi.GitHubRelease;
import lol.hyper.githubreleaseapi.GitHubReleaseAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class EzHomes extends JavaPlugin {

    public final File configFile = new File(this.getDataFolder(), "config.yml");
    public final File messagesFile = new File(this.getDataFolder(), "messages.yml");
    public final int CONFIG_VERSION = 4;
    public final int MESSAGES_VERSION = 2;
    public FileConfiguration config = this.getConfig();
    public FileConfiguration messages;
    public final ArrayList<UUID> isTeleporting = new ArrayList<>();

    public final Logger logger = this.getLogger();

    public final MiniMessage miniMessage = MiniMessage.miniMessage();
    private BukkitAudiences adventure;

    public CommandEzHomes commandEzHomes;
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
    public PlayerMove playerMove;
    public PlayerRespawn playerRespawn;
    public PlayerTeleport playerTeleport;

    private Scoreboard sb = null;

    @Override
    public void onEnable() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            sb = manager.getMainScoreboard();
        }

        this.adventure = BukkitAudiences.create(this);
        homeManagement = new HomeManagement(this);
        playerMove = new PlayerMove(this);
        commandEzHomes = new CommandEzHomes(this);
        commandHome = new CommandHome(this);
        commandSetHome = new CommandSetHome(this);
        commandDeleteHome = new CommandDeleteHome(this);
        commandHomes = new CommandHomes(this);
        commandWhere = new CommandWhere(this);
        commandUpdateHome = new CommandUpdateHome(this);
        commandHomeRespawn = new CommandHomeRespawn(this);
        playerLeave = new PlayerLeave(this);
        playerRespawn = new PlayerRespawn(this);
        playerTeleport = new PlayerTeleport(this);
        inventoryClick = new InventoryClick(this);
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            logger.info("Copying default config!");
        }
        if (!messagesFile.exists()) {
            this.saveResource("messages.yml", true);
            logger.info("Copying default messages!");
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
        this.getCommand("ezhomes").setExecutor(commandEzHomes);
        this.getCommand("where").setExecutor(commandWhere);
        this.getCommand("respawnhome").setExecutor(commandHomeRespawn);

        Bukkit.getPluginManager().registerEvents(inventoryClick, this);
        Bukkit.getPluginManager().registerEvents(playerLeave, this);
        Bukkit.getPluginManager().registerEvents(playerMove, this);
        Bukkit.getPluginManager().registerEvents(playerRespawn, this);
        Bukkit.getPluginManager().registerEvents(playerTeleport, this);

        Bukkit.getScheduler()
                .runTaskLaterAsynchronously(this, () -> homeManagement.cleanEmptyHomeFiles(), 100);

        new Metrics(this, 9390);

        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        if (config.getInt("config-version") != CONFIG_VERSION) {
            logger.warning("Your config file is outdated! Please regenerate the config.");
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        if (messages.getInt("version") != MESSAGES_VERSION) {
            logger.warning("Your messages file is outdated! Please regenerate this file.");
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
            logger.warning(
                    "You are running a version that does not exist on GitHub. If you are in a dev environment, you can ignore this. Otherwise, this is a bug!");
            return;
        }
        int buildsBehind = api.getBuildsBehind(current);
        if (buildsBehind == 0) {
            logger.info("You are running the latest version.");
        } else {
            logger.warning(
                    "A new version is available ("
                            + latest.getTagVersion()
                            + ")! You are running version "
                            + current.getTagVersion()
                            + ". You are "
                            + buildsBehind
                            + " version(s) behind.");
        }
    }

    /**
     * Gets a message from messages.yml.
     *
     * @param path The path to the message.
     * @param replacement The replacement.
     * @return Component with formatting applied.
     */
    public Component getMessage(String path, Object replacement) {
        String message = messages.getString(path);
        if (message == null) {
            logger.warning(path + " is not a valid message!");
            return Component.text("Invalid path! " + path).color(NamedTextColor.RED);
        }
        if (message.contains("%seconds%")) {
            message = message.replace("%seconds%", replacement.toString());
        }
        if (message.contains("%homes%")) {
            message = message.replace("%homes%", replacement.toString());
        }
        if (message.contains("%home%")) {
            message = message.replace("%home%", replacement.toString());
        }
        if (message.contains("%world%")) {
            message = message.replace("%world%", replacement.toString());
        }
        if (message.contains("%player%") && replacement instanceof Player) {
            message = replaceTeamFormattedPlayerDisplayName(message, (Player) replacement);
        }
        if (message.contains("%x%")) {
            message = message.replace("%x%", replacement.toString());
        }
        if (message.contains("%y%")) {
            message = message.replace("%y%", replacement.toString());
        }
        if (message.contains("%z%")) {
            message = message.replace("%z%", replacement.toString());
        }
        return miniMessage.deserialize(message);
    }

    /**
     * Gets a message from messages.yml.
     *
     * @param path The path to the message.
     * @return Component with formatting applied.
     */
    public Component getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            logger.warning(path + " is not a valid message!");
            return Component.text("Invalid path! " + path).color(NamedTextColor.RED);
        }
        return miniMessage.deserialize(message);
    }

    /**
     * Gets a message list.
     *
     * @param path The path to the message.
     * @return A raw string list of messages.
     */
    public List<String> getMessageList(String path) {
        List<String> messageList = messages.getStringList(path);
        if (!messageList.isEmpty()) {
            return messageList;
        }
        logger.warning(path + " is not a valid message!");
        messageList.add("<red>Invalid path! " + path + "</red>");
        return messageList;
    }


    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException(
                    "Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    /**
     * Replace player's name with a formatted name.
     *
     * @param message The message with player placeholder.
     * @param player The player.
     * @return A formatted message with formatted player display name.
     */
    private String replaceTeamFormattedPlayerDisplayName(String message, final Player player) {
        if (config.getBoolean("use-team-formatting") && sb != null) {
            final Team team = sb.getEntryTeam(player.getName());
            if (team != null) {
                final Component mmsg = miniMessage.deserialize(message);
                final String lmsg = LegacyComponentSerializer.builder().build().serialize(mmsg);
                return miniMessage.serialize(
                        LegacyComponentSerializer.builder()
                                .build()
                                .deserialize(
                                        lmsg.replace(
                                                "%player%",
                                                team.getColor()
                                                        + team.getPrefix()
                                                        + player.getDisplayName()
                                                        + team.getSuffix()
                                                        + ChatColor.RESET)));
            }
        }
        return message.replace("%player%", player.getDisplayName());
    }
}
