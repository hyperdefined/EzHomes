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
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandWhere implements CommandExecutor {

    private final HomeManagement homeManagement;

    public CommandWhere(HomeManagement homeManagement) {
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
                        Location home = homeManagement.getHomeLocation(player.getUniqueId(), args[0]);
                        sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                        sender.sendMessage(ChatColor.GOLD + args[0] + "'s location:");
                        sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.GOLD + home.getWorld().getName());
                        sender.sendMessage(ChatColor.YELLOW + "X: " + ChatColor.GOLD + (int) home.getX());
                        sender.sendMessage(ChatColor.YELLOW + "Y: " + ChatColor.GOLD + (int) home.getY());
                        sender.sendMessage(ChatColor.YELLOW + "Y: " + ChatColor.GOLD + (int) home.getZ());
                        sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That home does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have any homes.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To see where a home is, simply do \"/where <home name>\"");
            }
        }
        return true;
    }
}
