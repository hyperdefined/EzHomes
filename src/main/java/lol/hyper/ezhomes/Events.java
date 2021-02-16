package lol.hyper.ezhomes;

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.gui.GUIHolder;
import lol.hyper.ezhomes.gui.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.TimeUnit;

public class Events implements Listener {

    private final EzHomes ezHomes;

    public Events(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getHolder() instanceof GUIHolder) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item.getType() == Material.RED_BED && item.getType() != Material.AIR) {
                if (ezHomes.homeManagement.canPlayerTeleport(player.getUniqueId())) {
                    ItemMeta meta = item.getItemMeta();
                    Location loc = ezHomes.homeManagement.getHomeLocation(player.getUniqueId(), meta.getDisplayName());
                    PaperLib.teleportAsync(player, loc);
                    ezHomes.homeManagement.teleportCooldowns.put(player.getUniqueId(), System.nanoTime());
                    player.sendMessage(ChatColor.GREEN + "Whoosh!");
                } else {
                    long timeLeft = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - ezHomes.homeManagement.teleportCooldowns.get(player.getUniqueId()));
                    long configTime = ezHomes.config.getInt("teleport-cooldown");
                    player.sendMessage(ChatColor.RED + "You must wait " + (configTime - timeLeft) + " seconds to teleport.");
                }
            }

            if (item.getType() == Material.PAPER && item.getType() != Material.AIR) {
                int currentPage = ezHomes.homeManagement.guiManagers.get(player).getCurrentPageIndex();
                GUIManager guiManager = ezHomes.homeManagement.guiManagers.get(player);
                ItemStack paper = event.getCurrentItem();
                if (paper.getItemMeta().getDisplayName().contains("Next")) {
                    player.getOpenInventory().close();
                    guiManager.openGUI(currentPage + 1);
                }
                if (paper.getItemMeta().getDisplayName().contains("Previous")) {
                    player.getOpenInventory().close();
                    guiManager.openGUI(currentPage - 1);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ezHomes.homeManagement.guiManagers.remove(event.getPlayer());
    }
}
