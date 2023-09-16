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

package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.events.PlayerMove;
import lol.hyper.ezhomes.tools.HomeManagement;
import lol.hyper.ezhomes.tools.TeleportTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandHome implements TabExecutor {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;
    private final BukkitAudiences audiences;
    private final PlayerMove playerMove;

    public CommandHome(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.homeManagement = ezHomes.homeManagement;
        this.audiences = ezHomes.getAdventure();
        this.playerMove = ezHomes.playerMove;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("ezhomes.home")) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("no-perms"));
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("errors.must-be-player"));
            return true;
        }

        Player player = (Player) sender;
        List<String> playerHomes = homeManagement.getPlayerHomes(player.getUniqueId());
        if (playerHomes.isEmpty()) {
            audiences.player(player).sendMessage(ezHomes.getMessage("errors.no-homes"));
            return true;
        }

        int argsLength = args.length;
        switch (argsLength) {
            case 0: {
                audiences.player(player).sendMessage(ezHomes.getMessage("errors.specify-home-name"));
                return true;
            }
            case 1: {
                String homeName = args[0];
                if (homeManagement.canPlayerTeleport(player.getUniqueId()) || player.hasPermission("ezhomes.bypasscooldown")) {
                    if (playerHomes.contains(homeName)) {
                        BukkitTask currentTask = playerMove.teleportTasks.get(player.getUniqueId());
                        if (currentTask != null) {
                            currentTask.cancel();
                            audiences.player(player).sendMessage(ezHomes.getMessage("errors.teleport-canceled"));
                        }
                        BukkitTask teleportTask = new TeleportTask(ezHomes, player, homeManagement.getHomeLocation(player.getUniqueId(), homeName)).runTaskTimer(ezHomes, 0, 20L);
                        playerMove.teleportTasks.put(player.getUniqueId(), teleportTask);
                    } else {
                        audiences.player(player).sendMessage(ezHomes.getMessage("errors.home-does-not-exist"));
                    }
                } else {
                    long timeLeft = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - homeManagement.teleportCooldowns.get(player.getUniqueId()));
                    long configTime = ezHomes.config.getInt("teleport-cooldown");
                    audiences.player(player).sendMessage(ezHomes.getMessage("commands.home.teleport-cooldown", (configTime - timeLeft)));
                }
                return true;
            }
            default:
                audiences.player(player).sendMessage(ezHomes.getMessage("commands.home.invalid-syntax"));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        return homeManagement.getPlayerHomes(player.getUniqueId());
    }
}
