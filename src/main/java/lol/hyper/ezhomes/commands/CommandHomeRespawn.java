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
import lol.hyper.ezhomes.tools.HomeManagement;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHomeRespawn implements TabExecutor {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;
    private final BukkitAudiences audiences;

    public CommandHomeRespawn(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.homeManagement = ezHomes.homeManagement;
        this.audiences = ezHomes.getAdventure();
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("errors.must-be-player", null));
            return true;
        }

        if (!ezHomes.config.getBoolean("allow-respawn-at-home")) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("commands.respawnhome.feature-not-enabled", null));
            return true;
        }

        Player player = (Player) sender;
        if (homeManagement.getPlayerHomes(player.getUniqueId()) == null) {
            audiences.player(player).sendMessage(ezHomes.getMessage("errors.no-homes", null));
            return true;
        }

        ArrayList<String> playerHomes = homeManagement.getPlayerHomes(player.getUniqueId());

        int argsLength = args.length;
        switch (argsLength) {
            case 0: {
                audiences.sender(sender).sendMessage(ezHomes.getMessage("errors.specify-action.", null));
                break;
            }
            case 1: {
                if (args[0].equalsIgnoreCase("remove")) {
                    homeManagement.removeRespawnLocation(player.getUniqueId());
                    audiences.sender(sender).sendMessage(ezHomes.getMessage("commands.respawnhome.respawnhome-removed", null));
                } else {
                    audiences.sender(sender).sendMessage(ezHomes.getMessage("commands.respawnhome.invalid-syntax", null));
                }
                return true;
            }
            case 2: {
                if (args[0].equalsIgnoreCase("set")) {
                    String homeName = args[1];
                    if (playerHomes.contains(homeName)) {
                        homeManagement.setRespawnLocation(player.getUniqueId(), homeName);
                        audiences.sender(sender).sendMessage(ezHomes.getMessage("commands.respawnhome.respawnhome-set", null));
                    } else {
                        audiences.sender(sender).sendMessage(ezHomes.getMessage("errors.home-does-not-exist.", null));
                    }
                } else {
                    audiences.sender(sender).sendMessage(ezHomes.getMessage("commands.respawnhome.invalid-syntax", null));
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            String[] args) {
        if (args.length == 0) {
            return Arrays.asList("set", "remove");
        }

        if (args[0].equalsIgnoreCase("set")) {
            Player player = (Player) sender;
            return homeManagement.getPlayerHomes(player.getUniqueId());
        }
        return null;
    }
}
