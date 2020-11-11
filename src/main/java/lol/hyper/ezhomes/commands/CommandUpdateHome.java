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

import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandUpdateHome implements CommandExecutor {

    private final HomeManagement homeManagement;

    public CommandUpdateHome(HomeManagement homeManagement) {
        this.homeManagement = homeManagement;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player for this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must specify a home name!");
        } else {
            if (args.length == 1) {
                if (homeManagement.getPlayerHomes(player.getUniqueId()) != null) {
                    if (homeManagement.getPlayerHomes(player.getUniqueId()).contains(args[0])) {
                        homeManagement.updateHome(player.getUniqueId(), args[0]);
                        player.sendMessage(ChatColor.GREEN + "Updated home.");
                    } else {
                        player.sendMessage(ChatColor.RED + "That home does not exist.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any homes!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To update a home, simply do \"/updatehome <home name>\"");
            }
        }
        return true;
    }
}
