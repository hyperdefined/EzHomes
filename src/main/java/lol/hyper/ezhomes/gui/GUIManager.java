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

package lol.hyper.ezhomes.gui;

import com.google.common.collect.Lists;
import lol.hyper.ezhomes.HomeManagement;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {

    final ArrayList<Inventory> homePages = new ArrayList<>();
    int currentPage = 0;
    final Player owner;

    private final HomeManagement homeManagement;

    public GUIManager (Player player, HomeManagement homeManagement) {
        this.homeManagement = homeManagement;
        this.owner = player;
        homeManagement.guiManagers.put(owner, this);

        int pages = getPagesToMake();
        createGUI(pages);
    }

    /**
     * Create the individual pages and store them.
     * @param pagesToMake How many pages to make.
     */
    private void createGUI(int pagesToMake) {
        for (int i = 0; i < pagesToMake; i++) {
            Inventory inv = Bukkit.createInventory(new GUIHolder(), 54, owner.getDisplayName() + "'s Homes");

            // Create the next and previous page items
            ItemStack prevPage = new ItemStack(Material.PAPER);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName("Previous Page");
            prevPage.setItemMeta(prevPageMeta);

            ItemStack nextPage = new ItemStack(Material.PAPER);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName("Next Page");
            nextPage.setItemMeta(nextPageMeta);

            // first page, last page, then middle page
            // Setting the next and previous page items based on the page number
            if (i == 0) {
                if (pagesToMake != 1) {
                    inv.setItem(53, nextPage);
                }
            } else if (i == pagesToMake-1) {
                inv.setItem(45, prevPage);
            } else {
                inv.setItem(53, nextPage);
                inv.setItem(45, prevPage);
            }

            // Put the compass at the center of all pages
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta compassItemMeta = compass.getItemMeta();
            compassItemMeta.setDisplayName("Click to teleport to your homes!");
            compass.setItemMeta(compassItemMeta);
            inv.setItem(49, compass);

            // Break the homes list into chunks of 45
            List<List<String>> homes = Lists.partition(homeManagement.getPlayerHomes(owner.getUniqueId()), 45);

            // Get the player's respawn home name so we can use it later
            String respawnHome = homeManagement.getRespawnHomeName(owner.getUniqueId());

            // Go through the list of homes and put them into the inventory
            for (int x = 0; x < 45; x++) {
                String homeName;
                try {
                    homeName = homes.get(i).get(x);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                ItemStack bed;
                // Check if the home is their respawn home
                // If it is, then make the bed green so they can see it's the respawn home
                if (respawnHome.equals(homeName)) {
                    bed = new ItemStack(Material.GREEN_BED);
                } else {
                    bed = new ItemStack(Material.RED_BED);
                }
                ItemMeta bedMeta = bed.getItemMeta();
                // Make the name green if it's their respawn home
                if (respawnHome.equals(homeName)) {
                    bedMeta.setDisplayName(ChatColor.GREEN + homeName);
                } else {
                    bedMeta.setDisplayName(homeName);
                }
                Location loc = homeManagement.getHomeLocation(owner.getUniqueId(), homes.get(i).get(x));
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "X: " + ChatColor.GRAY + (int) loc.getX());
                lore.add(ChatColor.WHITE + "Y: " + ChatColor.GRAY + (int) loc.getY());
                lore.add(ChatColor.WHITE + "Z: " + ChatColor.GRAY + (int) loc.getZ());
                lore.add(ChatColor.WHITE + "World: " + ChatColor.GRAY + loc.getWorld().getName());
                // Add a new line if the home is their respawn home
                if (respawnHome.equals(homeName)) {
                    lore.add(ChatColor.GREEN + "You will respawn at this home.");
                }
                bedMeta.setLore(lore);
                bed.setItemMeta(bedMeta);
                inv.setItem(x, bed);
            }

            homePages.add(inv);
        }
    }

    /**
     * Open a certain page.
     * @param pageIndex The page index to open.
     */
    public void openGUI(int pageIndex) {
        owner.openInventory(homePages.get(pageIndex));
        currentPage = pageIndex + 1;
    }

    /**
     * Calculate how many pages we need to make for the GUI.
     * @return How many pages it's going to make.
     */
    private Integer getPagesToMake() {
        int totalHomes = homeManagement.getPlayerHomes(owner.getUniqueId()).size();
        int totalPages;

        // Force 1 page if the player has 45 homes since zero will be put on the 2nd page
        if (totalHomes == 45) {
            totalPages = 1;
        } else {
            totalPages = (totalHomes / 45) + 1;
        }

        return totalPages;
    }

    /**
     * Get what page someone is currently on.
     * @return The page they have open.
     */
    public int getCurrentPageIndex() {
        return currentPage - 1;
    }
}
