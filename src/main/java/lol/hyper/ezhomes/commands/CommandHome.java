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
import lol.hyper.ezhomes.tools.TeleportTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandHome implements TabExecutor {

    private final EzHomes ezHomes;

    public CommandHome(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ezHomes.getAdventure()
                    .sender(sender)
                    .sendMessage(ezHomes.getMessage("errors.must-be-player", null));
            return true;
        }

        Player player = (Player) sender;
        if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()) == null) {
            ezHomes.getAdventure()
                    .player(player)
                    .sendMessage(ezHomes.getMessage("errors.no-homes", null));
            return true;
        }

        ArrayList<String> playerHomes = ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());

        int argsLength = args.length;
        switch (argsLength) {
            case 0:
                ezHomes.getAdventure()
                        .player(player)
                        .sendMessage(ezHomes.getMessage("errors.specify-home-name", null));
                return true;
            case 1:
                if (ezHomes.homeManagement.canPlayerTeleport(player.getUniqueId())
                        || player.hasPermission("ezhomes.bypasscooldown")) {
                    if (playerHomes.contains(args[0])) {
                        BukkitTask teleportTask =
                                new TeleportTask(
                                                ezHomes,
                                                player,
                                                ezHomes.homeManagement.getHomeLocation(
                                                        player.getUniqueId(), args[0]))
                                        .runTaskTimer(ezHomes, 0, 20L);
                        ezHomes.playerMove.teleportTasks.put(player.getUniqueId(), teleportTask);
                    } else {
                        ezHomes.getAdventure()
                                .player(player)
                                .sendMessage(
                                        ezHomes.getMessage("errors.home-does-not-exist", null));
                    }
                } else {
                    long timeLeft =
                            TimeUnit.NANOSECONDS.toSeconds(
                                    System.nanoTime()
                                            - ezHomes.homeManagement.teleportCooldowns.get(
                                                    player.getUniqueId()));
                    long configTime = ezHomes.config.getInt("teleport-cooldown");
                    ezHomes.getAdventure()
                            .player(player)
                            .sendMessage(
                                    ezHomes.getMessage(
                                            "commands.home.teleport-cooldown",
                                            (configTime - timeLeft)));
                }
                return true;
            default:
                ezHomes.getAdventure()
                        .player(player)
                        .sendMessage(ezHomes.getMessage("commands.home.invalid-syntax", null));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            String[] args) {
        Player player = (Player) sender;
        return ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());
    }
}
