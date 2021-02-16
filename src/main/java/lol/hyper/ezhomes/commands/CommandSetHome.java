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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandSetHome implements CommandExecutor {

    private final EzHomes ezHomes;
    public CommandSetHome(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
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
                if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()) != null) {
                    if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()).size() != ezHomes.config.getInt("total-homes") || player.hasPermission("ezhomes.bypasslimit")) {
                        ezHomes.homeManagement.createHome(player.getUniqueId(), args[0]);
                        sender.sendMessage(ChatColor.GREEN + "Home set.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only have a maximum of " + ezHomes.config.getInt("total-homes") + " homes.");
                    }
                } else {
                    ezHomes.homeManagement.createHome(player.getUniqueId(), args[0]);
                    sender.sendMessage(ChatColor.GREEN + "Home set.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To set a home, simply do \"/sethome <home name>\"");
            }
        }
        return true;
    }
}
