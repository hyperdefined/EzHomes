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
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandUpdateHome implements TabExecutor {

    private final EzHomes ezHomes;

    public CommandUpdateHome(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player for this command!");
            return true;
        }

        Player player = (Player) sender;
        if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()) == null) {
            sender.sendMessage(ChatColor.RED + "You do not have any homes.");
            return true;
        }
        ArrayList<String> playerHomes = ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());

        int argsLength = args.length;
        switch (argsLength) {
            case 0:
                player.sendMessage(ChatColor.RED + "You must specify a home name!");
                return true;
            case 1:
                if (playerHomes.contains(args[0])) {
                    ezHomes.homeManagement.updateHome(player.getUniqueId(), args[0]);
                    player.sendMessage(ChatColor.GREEN + "Updated home.");
                } else {
                    player.sendMessage(ChatColor.RED + "That home does not exist.");
                }
                return true;
            default:
                player.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /updatehome <home> to update a home's location.");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;
        return ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());
    }
}
