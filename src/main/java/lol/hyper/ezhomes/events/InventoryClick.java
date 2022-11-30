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

        if (!homeManagement.guiManagers.containsKey(player.getUniqueId())) {
            return;
        }
        GUIManager guiManager = homeManagement.guiManagers.get(player.getUniqueId());
        if (guiManager == null) {
            return;
        }

        if (guiManager.getHomePages().contains(inventory)) {
            // player clicked our custom inventory
            event.setCancelled(true);
            if ((item.getType() == Material.RED_BED || item.getType() == Material.GREEN_BED)
                    && item.getType() != Material.AIR) {
                if (homeManagement.canPlayerTeleport(player.getUniqueId())
                        || player.hasPermission("ezhomes.bypasscooldown")) {
                    ItemMeta meta = item.getItemMeta();
                    Location loc =
                            homeManagement.getHomeLocation(
                                    player.getUniqueId(),
                                    ChatColor.stripColor(meta.getDisplayName()));
                    BukkitTask currentTask = playerMove.teleportTasks.get(player.getUniqueId());
                    if (currentTask != null) {
                        currentTask.cancel();
                        audiences
                                .player(player)
                                .sendMessage(ezHomes.getMessage("errors.teleport-canceled", null));
                    }
                    BukkitTask teleportTask =
                            new TeleportTask(ezHomes, player, loc).runTaskTimer(ezHomes, 0, 20L);
                    playerMove.teleportTasks.put(player.getUniqueId(), teleportTask);
                    Bukkit.getScheduler()
                            .runTaskLater(ezHomes, () -> player.getOpenInventory().close(), 1);
                } else {
                    long timeLeft =
                            TimeUnit.NANOSECONDS.toSeconds(
                                    System.nanoTime()
                                            - homeManagement.teleportCooldowns.get(
                                                    player.getUniqueId()));
                    long configTime = ezHomes.config.getInt("teleport-cooldown");
                    audiences
                            .player(player)
                            .sendMessage(
                                    ezHomes.getMessage(
                                            "commands.home.teleport-cooldown",
                                            (configTime - timeLeft)));
                }
            }
            if (item.getType() == Material.PAPER && item.getType() != Material.AIR) {
                int currentPage =
                        homeManagement.guiManagers.get(player.getUniqueId()).getCurrentPageIndex();
                ItemStack paper = event.getCurrentItem();
                if (paper.getItemMeta().getDisplayName().contains("Next")) {
                    Bukkit.getScheduler()
                            .runTaskLater(ezHomes, () -> player.getOpenInventory().close(), 1);
                    guiManager.openGUI(currentPage + 1);
                }
                if (paper.getItemMeta().getDisplayName().contains("Previous")) {
                    Bukkit.getScheduler()
                            .runTaskLater(ezHomes, () -> player.getOpenInventory().close(), 1);
                    guiManager.openGUI(currentPage - 1);
                }
            }
        } else {
            event.setCancelled(true);
        }
    }
}
