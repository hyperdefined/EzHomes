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
import org.bukkit.event.player.PlayerRespawnEvent;
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
                if (ezHomes.homeManagement.canPlayerTeleport(player.getUniqueId()) || player.hasPermission("ezhomes.bypasscooldown")) {
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

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!ezHomes.config.getBoolean("allow-respawn-at-home")) {
            return;
        }

        Player player = event.getPlayer();
        Location respawnLocation = ezHomes.homeManagement.getRespawnLocation(player.getUniqueId());

        if (respawnLocation != null) {
            event.setRespawnLocation(respawnLocation);
        }
    }
}
