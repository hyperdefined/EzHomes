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

    ArrayList<Inventory> homePages = new ArrayList<>();
    int currentPage = 0;
    Player owner;

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

            // Go through the list of homes and put them into the inventory
            for (int x = 0; x < 45; x++) {
                String homeName;
                try {
                    homeName = homes.get(i).get(x);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                ItemStack bed = new ItemStack(Material.RED_BED);
                ItemMeta bedMeta = bed.getItemMeta();
                bedMeta.setDisplayName(homeName);
                Location loc = homeManagement.getHomeLocation(owner.getUniqueId(), homes.get(i).get(x));
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "X: " + ChatColor.GRAY + (int) loc.getX());
                lore.add(ChatColor.WHITE + "Y: " + ChatColor.GRAY + (int) loc.getY());
                lore.add(ChatColor.WHITE + "Z: " + ChatColor.GRAY + (int) loc.getZ());
                lore.add(ChatColor.WHITE + "World: " + ChatColor.GRAY + loc.getWorld().getName());
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

    public int getCurrentPageIndex() {
        return currentPage - 1;
    }
}
