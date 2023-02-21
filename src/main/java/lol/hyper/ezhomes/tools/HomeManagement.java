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

package lol.hyper.ezhomes.tools;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.gui.GUIManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HomeManagement {

    public final HashMap<UUID, Long> teleportCooldowns = new HashMap<>();
    public final HashMap<UUID, GUIManager> guiManagers = new HashMap<>();

    public final Path homesPath;
    public final File respawnsFile;

    private final EzHomes ezHomes;

    public HomeManagement(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        homesPath = Paths.get(this.ezHomes.getDataFolder() + File.separator + "data");
        respawnsFile = new File(this.ezHomes.getDataFolder() + File.separator + "respawns.json");
        createFilesWeNeed();
    }

    /**
     * Get a player's home file.
     *
     * @param player Player to get file.
     * @return The player's home file.
     */
    private File getPlayerFile(UUID player) {
        return Paths.get(homesPath.toString(), player.toString() + ".json").toFile();
    }

    private void createFilesWeNeed() {

        if (!Files.exists(homesPath)) {
            try {
                Files.createDirectories(homesPath);
            } catch (IOException e) {
                ezHomes.logger.severe(
                        "Unable to create folder "
                                + homesPath
                                + "! Please make the folder manually or check folder permissions!");
                e.printStackTrace();
            }
        }

        if (!Files.exists(respawnsFile.toPath())) {
            try {
                Files.createFile(respawnsFile.toPath());
                JSONObject empty = new JSONObject();
                writeFile(
                        respawnsFile,
                        empty.toString()); // write an empty json because shit won't work otherwise
            } catch (IOException e) {
                ezHomes.logger.severe(
                        "Unable to create file "
                                + respawnsFile
                                + "! Please make the file manually or check file permissions!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Read data from JSON file.
     *
     * @param file File to read data from.
     * @return JSONObject with JSON data.
     */
    private JSONObject readFile(File file) {
        if (!file.exists()) {
            return null;
        }
        JSONObject object = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            object = new JSONObject(sb.toString());
            br.close();
        } catch (Exception e) {
            ezHomes.logger.severe("Unable to read file " + file.getAbsolutePath());
            ezHomes.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Write data to JSON file.
     *
     * @param file File to write data to.
     * @param jsonToWrite Data to write to file. This much be a JSON string.
     */
    private void writeFile(File file, String jsonToWrite) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonToWrite);
            writer.close();
        } catch (IOException e) {
            ezHomes.logger.severe("Unable to write file " + file.getAbsolutePath());
            ezHomes.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
    }

    /**
     * Delete a player's home file.
     *
     * @param player Player to delete file.
     */
    private void deletePlayerHomeFile(UUID player) {
        File homeFile = getPlayerFile(player);
        try {
            Files.delete(homeFile.toPath());
        } catch (IOException e) {
            ezHomes.logger.severe("Unable to delete file + " + homeFile.getAbsolutePath());
            ezHomes.logger.severe("This is bad, really bad.");
            e.printStackTrace();
        }
    }

    /**
     * Create a new home for a player.
     *
     * @param player Player that is creating a new home.
     * @param homeName Name of the home to create.
     */
    public void createHome(UUID player, String homeName) {
        Location homeLocation = Bukkit.getPlayer(player).getLocation();
        File homeFile = getPlayerFile(player);
        JSONObject homes = readFile(homeFile);

        // They don't have a file
        if (homes == null) {
            homes = new JSONObject();
        }
        Map<String, Object> m = new LinkedHashMap<>(5);
        m.put("x", homeLocation.getX());
        m.put("y", homeLocation.getY());
        m.put("z", homeLocation.getZ());
        m.put("pitch", homeLocation.getPitch());
        m.put("yaw", homeLocation.getYaw());
        m.put("world", homeLocation.getWorld().getName());
        homes.put(homeName, m);
        writeFile(homeFile, homes.toString());
    }

    /**
     * Get the location of a home.
     *
     * @param player Player to see home location.
     * @param homeName Home name to get location.
     * @return Location of the home.
     */
    public Location getHomeLocation(UUID player, String homeName) {
        File homeFile = getPlayerFile(player);
        if (readFile(homeFile) != null) {
            JSONObject homes = readFile(homeFile);
            JSONObject home = (JSONObject) homes.get(homeName);
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
    }

    /**
     * Returns a list of player homes.
     *
     * @param player Player to lookup homes for.
     * @return Returns null if the file doesn't exist. Returns 0 if there are no locations. Returns
     *     the number of locations if there are any.
     */
    public List<String> getPlayerHomes(UUID player) {
        File homeFile = getPlayerFile(player);
        if (readFile(homeFile) != null) {
            JSONObject currentHomeFileJSON = readFile(homeFile);
            List<String> playerHomes = new ArrayList<>(currentHomeFileJSON.keySet());
            Collections.sort(playerHomes);
            return playerHomes;
        } else {
            return Collections.emptyList();
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
            long timeLeft =
                    TimeUnit.NANOSECONDS.toSeconds(
                            (System.nanoTime() - teleportCooldowns.get(player))
                                    - (long) ezHomes.config.getInt("teleport-cooldown"));
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
        Location newLocation = Bukkit.getPlayer(player).getLocation();
        File homeFile = getPlayerFile(player);
        JSONObject homes = readFile(homeFile);
        homes.remove(homeName);
        Map<String, Object> m = new LinkedHashMap<>(5);
        m.put("x", newLocation.getX());
        m.put("y", newLocation.getY());
        m.put("z", newLocation.getZ());
        m.put("pitch", newLocation.getPitch());
        m.put("yaw", newLocation.getYaw());
        m.put("world", newLocation.getWorld().getName());
        homes.put(homeName, m);
        writeFile(homeFile, homes.toString());
    }

    /**
     * Delete a player's home.
     *
     * @param player Player to delete home from.
     * @param homeName Home to delete.
     */
    public void deleteHome(UUID player, String homeName) {
        File homeFile = getPlayerFile(player);
        JSONObject homes = readFile(homeFile);
        homes.remove(homeName);
        // check if the player has zero homes
        // if they do, delete the file off of the disk
        // otherwise write the new file
        if (homes.isEmpty()) {
            deletePlayerHomeFile(player);
            return;
        }
        writeFile(homeFile, homes.toString());

        // If the player deletes their respawn location, then remove
        // it from the respawn home lists
        if (homeName.equals(getRespawnHomeName(player))) {
            removeRespawnLocation(player);
        }
    }

    /**
     * Returns a nice and clickable list of player homes.
     *
     * @param player Player to get the homes of.
     * @return Returns TextComponent of homes that can be clicked.
     */
    public List<Component> getHomesClickable(UUID player) {
        List<Component> components = new ArrayList<>();
        if (getPlayerHomes(player).isEmpty()) {
            return Collections.emptyList();
        } else {
            for (String home : getPlayerHomes(player)) {
                TextComponent singleHome;
                int index = getPlayerHomes(player).indexOf(home);
                String finalHomeName;
                if (index != getPlayerHomes(player).size() - 1) {
                    finalHomeName = home + ", ";
                } else {
                    finalHomeName = home;
                }
                if (ezHomes.config.getBoolean("allow-respawn-homes") && home.equals(getRespawnHomeName(player))) {
                    singleHome =
                            Component.text(finalHomeName)
                                    .color(NamedTextColor.GREEN)
                                    .clickEvent(
                                            ClickEvent.clickEvent(
                                                    ClickEvent.Action.RUN_COMMAND, "/home " + home))
                                    .hoverEvent(
                                            Component.text("Click to teleport to " + home + "!")
                                                    .color(NamedTextColor.GREEN));
                } else {
                    singleHome =
                            Component.text(finalHomeName)
                                    .color(NamedTextColor.YELLOW)
                                    .clickEvent(
                                            ClickEvent.clickEvent(
                                                    ClickEvent.Action.RUN_COMMAND, "/home " + home))
                                    .hoverEvent(
                                            Component.text("Click to teleport to " + home + "!")
                                                    .color(NamedTextColor.GREEN));
                }
                components.add(singleHome);
            }
            return components;
        }
    }

    /**
     * This will delete any homes files that do not have homes in them. This is just for cleanup.
     */
    public void cleanEmptyHomeFiles() {
        ezHomes.logger.info("Looking for any empty homes files to clean up...");
        File homesFolder = homesPath.toFile();
        File[] homeFiles = homesFolder.listFiles();
        if (homeFiles == null) {
            return;
        }
        int fileCount = 0;
        for (File f : homeFiles) {
            JSONObject homeFileJSON = readFile(f);
            if (homeFileJSON.length() == 0) {
                try {
                    Files.delete(f.toPath());
                } catch (IOException e) {
                    ezHomes.logger.severe("Unable to delete empty home file " + f);
                    e.printStackTrace();
                }
                ezHomes.logger.info("Deleting empty home file " + f);
                fileCount++;
            }
        }
        ezHomes.logger.info(fileCount + " file(s) were cleaned.");
    }

    /**
     * Set a player's respawn location home.
     *
     * @param player Player to set spawn location for.
     * @param homeName Home to set as spawn.
     */
    public void setRespawnLocation(UUID player, String homeName) {
        JSONObject respawns = readFile(respawnsFile);

        if (respawns == null) {
            respawns = new JSONObject();
        }
        respawns.put(player.toString(), homeName);
        writeFile(respawnsFile, respawns.toString());
    }

    /**
     * Remove a player's respawn location home.
     *
     * @param player Player to remove spawn location for.
     */
    public void removeRespawnLocation(UUID player) {
        JSONObject respawns = readFile(respawnsFile);

        if (respawns == null) {
            respawns = new JSONObject();
        }
        respawns.remove(player.toString());
        writeFile(respawnsFile, respawns.toString());
    }

    /**
     * Get a player's respawn location home.
     *
     * @param player Player to get their respawn location.
     */
    public Location getRespawnLocation(UUID player) {
        JSONObject respawns = readFile(respawnsFile);
        if (respawns.has(player.toString())) {
            return getHomeLocation(player, respawns.get(player.toString()).toString());
        } else {
            return null;
        }
    }

    /**
     * Get a player's respawn home name.
     *
     * @param player Player to get the home name.
     * @return Player's respawn home name.
     */
    public String getRespawnHomeName(UUID player) {
        JSONObject respawns = readFile(respawnsFile);
        if (respawns.has(player.toString())) {
            return respawns.get(player.toString()).toString();
        } else {
            return null;
        }
    }
}
