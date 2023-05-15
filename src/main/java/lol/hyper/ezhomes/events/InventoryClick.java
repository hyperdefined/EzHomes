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

package lol.hyper.ezhomes.events;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.gui.GUIManager;
import lol.hyper.ezhomes.tools.HomeManagement;
import lol.hyper.ezhomes.tools.TeleportTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class InventoryClick implements Listener {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;
    private final BukkitAudiences audiences;
    private final PlayerMove playerMove;

    public InventoryClick(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.homeManagement = ezHomes.homeManagement;
        this.audiences = ezHomes.getAdventure();
        this.playerMove = ezHomes.playerMove;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();
        if (inventory == null) {
            return;
        }

        // we don't have a gui saved for them
        GUIManager guiManager = homeManagement.guiManagers.get(player.getUniqueId());
        if (guiManager == null) {
            return;
        }

        // player has gui saved, but they didn't click the inventory
        if (!guiManager.getHomePages().contains(inventory)) {
            return;
        }

        // player clicked our custom inventory
        // cancel them clicking anything in the gui, but react on it
        event.setCancelled(true);
        Material clickedType = item.getType();
        switch (clickedType) {
            // beds are homes
            case RED_BED:
            case GREEN_BED: {
                // player has a cooldown still
                if (!homeManagement.canPlayerTeleport(player.getUniqueId()) || !player.hasPermission("ezhomes.bypasscooldown")) {
                    long timeLeft = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - homeManagement.teleportCooldowns.get(player.getUniqueId()));
                    long configTime = ezHomes.config.getInt("teleport-cooldown");
                    audiences.player(player).sendMessage(ezHomes.getMessage("commands.home.teleport-cooldown", (configTime - timeLeft)));
                    return;
                }

                // get home name from item
                ItemMeta meta = item.getItemMeta();
                Location loc = homeManagement.getHomeLocation(player.getUniqueId(), ChatColor.stripColor(meta.getDisplayName()));

                // see if the player is already teleporting
                // if they are, cancel it
                BukkitTask currentTask = playerMove.teleportTasks.get(player.getUniqueId());
                if (currentTask != null) {
                    currentTask.cancel();
                    audiences.player(player).sendMessage(ezHomes.getMessage("errors.teleport-canceled"));
                }

                // start the teleport countdown task
                BukkitTask teleportTask =
                        new TeleportTask(ezHomes, player, loc).runTaskTimer(ezHomes, 0, 20L);
                playerMove.teleportTasks.put(player.getUniqueId(), teleportTask);
                // make sure to close the inventory afterwards!!
                Bukkit.getScheduler().runTaskLater(ezHomes, () -> player.getOpenInventory().close(), 1);
            }
            // paper is next/prev page
            case PAPER: {
                // find the current page the player has opened
                int currentPage = homeManagement.guiManagers.get(player.getUniqueId()).getCurrentPageIndex();
                ItemStack paper = event.getCurrentItem();
                // move to next page
                if (paper.getItemMeta().getDisplayName().contains("Next")) {
                    Bukkit.getScheduler().runTaskLater(ezHomes, () -> player.getOpenInventory().close(), 1);
                    guiManager.openGUI(currentPage + 1);
                }
                // move to previous page
                if (paper.getItemMeta().getDisplayName().contains("Previous")) {
                    Bukkit.getScheduler().runTaskLater(ezHomes, () -> player.getOpenInventory().close(), 1);
                    guiManager.openGUI(currentPage - 1);
                }
            }
        }
    }
}
