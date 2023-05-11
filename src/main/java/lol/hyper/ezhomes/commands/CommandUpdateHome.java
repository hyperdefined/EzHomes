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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUpdateHome implements TabExecutor {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;
    private final BukkitAudiences audiences;

    public CommandUpdateHome(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.homeManagement = ezHomes.homeManagement;
        this.audiences = ezHomes.getAdventure();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("ezhomes.updatehome")) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("no-perms", null));
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("errors.must-be-player", null));
            return true;
        }

        Player player = (Player) sender;
        if (homeManagement.getPlayerHomes(player.getUniqueId()).isEmpty()) {
            audiences.player(player).sendMessage(ezHomes.getMessage("errors.no-homes", null));
            return true;
        }
        List<String> playerHomes = homeManagement.getPlayerHomes(player.getUniqueId());

        int argsLength = args.length;
        switch (argsLength) {
            case 0: {
                audiences.player(player).sendMessage(ezHomes.getMessage("errors.specify-home-name", null));
                return true;
            }
            case 1: {
                if (playerHomes.contains(args[0])) {
                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
                    Matcher matcher = pattern.matcher(args[0]);
                    if (!matcher.matches()) {
                        audiences.player(player).sendMessage(ezHomes.getMessage("errors.invalid-character", null));
                        return true;
                    }
                    homeManagement.updateHome(player.getUniqueId(), args[0]);
                    audiences.player(player).sendMessage(ezHomes.getMessage("commands.updatehome.updated-home", null));
                } else {
                    audiences.player(player).sendMessage(ezHomes.getMessage("errors.home-does-not-exist", null));
                }
                return true;
            }
            default:
                audiences.player(player).sendMessage(ezHomes.getMessage("commands.updatehome.invalid-syntax", null));
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
        return homeManagement.getPlayerHomes(player.getUniqueId());
    }
}
