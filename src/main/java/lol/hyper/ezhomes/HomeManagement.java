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

import lol.hyper.ezhomes.gui.GUIManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HomeManagement {
    private static FileWriter writer;
    private static FileReader reader;

    public final HashMap<UUID, Long> teleportCooldowns = new HashMap<>();
    public final HashMap<Player, GUIManager> guiManagers = new HashMap<>();

    private final EzHomes ezHomes;

    public HomeManagement(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    /**
     * Create a new home for a player.
     *
     * @param player Player that is creating a new home.
     * @param homeName Name of the home to create.
     */
    public void createHome(UUID player, String homeName) {
        File homeFile = new File(ezHomes.homesPath.toFile(), player + ".json");
        // Checks if the player has a home file already.
        // If they do, then read current file then add new JSONObject to it.
        // If they don't, then just put a new JSONObject there.
        // There is probably a better way of doing this, but I have done this method in the past.
        try {
            Location homeLocation = Bukkit.getPlayer(player).getLocation();
            if (homeFile.exists()) {
                JSONParser parser = new JSONParser();
                reader = new FileReader(homeFile);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject currentHomeFileJSON = (JSONObject) obj;
                Map m = new LinkedHashMap(5);
                m.put("x", homeLocation.getX());
                m.put("y", homeLocation.getY());
                m.put("z", homeLocation.getZ());
                m.put("pitch", homeLocation.getPitch());
                m.put("yaw", homeLocation.getYaw());
                m.put("world", homeLocation.getWorld().getName());
                currentHomeFileJSON.put(homeName, m);
                writer = new FileWriter(homeFile);
                writer.write(currentHomeFileJSON.toJSONString());
            } else {
                JSONObject homeObject = new JSONObject();
                Map m = new LinkedHashMap(5);
                m.put("x", homeLocation.getX());
                m.put("y", homeLocation.getY());
                m.put("z", homeLocation.getZ());
                m.put("pitch", homeLocation.getPitch());
                m.put("yaw", homeLocation.getYaw());
                m.put("world", homeLocation.getWorld().getName());
                homeObject.put(homeName, m);
                writer = new FileWriter(homeFile);
                writer.write(homeObject.toJSONString());
            }
            writer.close();
        } catch (ParseException | IOException e) {
            ezHomes.logger.severe("There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
        }
    }

    /**
     * Get the location of a home.
     *
     * @param player Player to see home location.
     * @param homeName Home name to get location.
     * @return Location of the home.
     */
    public Location getHomeLocation(UUID player, String homeName) {
        File homeFile = new File(ezHomes.homesPath.toFile(), player + ".json");
        try {
            if (homeFile.exists()) {
                JSONParser parser = new JSONParser();
                reader = new FileReader(homeFile);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject homeFileJSON = (JSONObject) obj;
                JSONObject home = (JSONObject) homeFileJSON.get(homeName);
                double x = Double.parseDouble(home.get("x").toString());
                double y = Double.parseDouble(home.get("y").toString());
                double z = Double.parseDouble(home.get("z").toString());
                float pitch = Float.parseFloat(home.get("pitch").toString());
                float yaw = Float.parseFloat(home.get("yaw").toString());
                World w = Bukkit.getWorld(home.get("world").toString());
                return new Location(w, x, y, z, yaw, pitch);
            } else {
                return null;
            }
        } catch (ParseException | IOException e) {
            ezHomes.logger.severe("There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of player homes.
     *
     * @param player Player to lookup homes for.
     * @return Returns null if the file doesn't exist. Returns 0 if there are no locations. Returns the number of locations if there are any.
     */
    public ArrayList<String> getPlayerHomes(UUID player) {
        File homeFile = new File(ezHomes.homesPath.toFile(), player + ".json");
        try {
            if (homeFile.exists()) {
                ArrayList<String> playerHomes = new ArrayList<>();
                JSONParser parser = new JSONParser();
                reader = new FileReader(homeFile);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject currentHomeFileJSON = (JSONObject) obj;
                for (Object o : currentHomeFileJSON.keySet()) {
                    playerHomes.add((String) o);
                }
                Collections.sort(playerHomes);
                return playerHomes;
            } else {
                return null;
            }
        } catch (ParseException | IOException e) {
            ezHomes.logger.severe("There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks the cooldown between teleports.
     *
     * @param player Player to check cooldown for.
     * @return Returns true if the player can teleport, false if they cannot.
     */
    public boolean canPlayerTeleport(UUID player) {
        if (teleportCooldowns.containsKey(player)) {
            long timeLeft = TimeUnit.NANOSECONDS.toSeconds((System.nanoTime() - teleportCooldowns.get(player)) - (long) ezHomes.config.getInt("teleport-cooldown"));
            return timeLeft >= (long) ezHomes.config.getInt("teleport-cooldown");
        } else {
            return true;
        }
    }

    /**
     * Update a home's location.
     *
     * @param player Player to update home.
     * @param homeName Home to update location for.
     */
    public void updateHome(UUID player, String homeName) {
        File homeFile = new File(ezHomes.homesPath.toFile(), player + ".json");
        try {
            Location newLocation = Bukkit.getPlayer(player).getLocation();
            JSONParser parser = new JSONParser();
            reader = new FileReader(homeFile);
            Object obj = parser.parse(reader);
            reader.close();
            JSONObject homeFileJSON = (JSONObject) obj;
            homeFileJSON.remove(homeName);
            Map m = new LinkedHashMap(5);
            m.put("x", newLocation.getX());
            m.put("y", newLocation.getY());
            m.put("z", newLocation.getZ());
            m.put("pitch", newLocation.getPitch());
            m.put("yaw", newLocation.getYaw());
            m.put("world", newLocation.getWorld().getName());
            homeFileJSON.put(homeName, m);
            writer = new FileWriter(homeFile);
            writer.write(homeFileJSON.toJSONString());
            writer.close();
        } catch (ParseException | IOException e) {
            ezHomes.logger.severe("There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
        }
    }

    /**
     * Delete a player's home.
     *
     * @param player Player to delete home from.
     * @param homeName Home to delete.
     */
    public void deleteHome(UUID player, String homeName) {
        File homeFile = new File(ezHomes.homesPath.toFile(), player + ".json");
        try {
            JSONParser parser = new JSONParser();
            reader = new FileReader(homeFile);
            Object obj = parser.parse(reader);
            reader.close();
            JSONObject homeFileJSON = (JSONObject) obj;
            homeFileJSON.remove(homeName);
            writer = new FileWriter(homeFile);
            writer.write(homeFileJSON.toJSONString());
            writer.close();
            if (homeFileJSON.size() == 0) {
                Files.delete(homeFile.toPath());
            }
        } catch (ParseException | IOException e) {
            ezHomes.logger.severe("There was an issue reading file " + homeFile + "!");
            e.printStackTrace();
        }
    }

    /**
     * Returns a nice and clickable list of player homes.
     *
     * @param player Player to get the homes of.
     * @return Returns TextComponent of homes that can be clicked.
     */
    public TextComponent getHomesClickable(UUID player) {
        if (getPlayerHomes(player) == null) {
            return null;
        } else {
            TextComponent homesList = new TextComponent("");
            for (String home : getPlayerHomes(player)) {
                int index = getPlayerHomes(player).indexOf(home);
                TextComponent singleHome;
                if (index != getPlayerHomes(player).size() - 1) {
                    singleHome = new TextComponent(home + ", ");
                } else {
                    singleHome = new TextComponent(home);
                }
                singleHome.setColor(ChatColor.YELLOW);
                singleHome.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home));
                singleHome.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Click to teleport to " + home + "!")));
                homesList.addExtra(singleHome);
            }
            return homesList;
        }
    }

    /**
     * This will delete any homes files that do not have homes in them.
     * This is just for cleanup.
     */
    public void cleanEmptyHomeFiles() {
        ezHomes.logger.info("Looking for any empty homes files to clean up...");
        File homesFolder = ezHomes.homesPath.toFile();
        File[] homeFiles = homesFolder.listFiles();
        if (homeFiles == null) {
            return;
        }
        int fileCount = 0;
        for (File f : homeFiles) {
            JSONParser parser = new JSONParser();
            try {
                reader = new FileReader(f);
                Object obj = parser.parse(reader);
                reader.close();
                JSONObject homeFileJSON = (JSONObject) obj;
                if (homeFileJSON.size() == 0) {
                    Files.delete(f.toPath());
                    ezHomes.logger.info("Deleting empty home file " + f);
                    fileCount++;
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
        ezHomes.logger.info(fileCount + " file(s) were cleaned.");
    }
}
