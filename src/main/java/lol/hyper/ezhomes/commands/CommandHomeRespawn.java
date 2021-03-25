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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHomeRespawn implements TabExecutor {

    private final EzHomes ezHomes;

    public CommandHomeRespawn(EzHomes ezHomes) {
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
        switch(argsLength) {
            case 0:
                sender.sendMessage(ChatColor.RED + "You must specify what you want to do.");
                break;
            case 1:
                if (args[0].equalsIgnoreCase("remove")) {
                    ezHomes.homeManagement.removeRespawnLocation(player.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Respawn home has been removed.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid option. Valid options are: \"remove\" or \"set\" <home>.");
                }
                return true;
            case 2:
                if (args[0].equalsIgnoreCase("set")) {
                    String homeName = args[1];
                    if (playerHomes.contains(homeName)) {
                        ezHomes.homeManagement.setRespawnLocation(player.getUniqueId(), homeName);
                        sender.sendMessage(ChatColor.GREEN + "Respawn home has been set.");
                    } else {
                        player.sendMessage(ChatColor.RED + "That home does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid option. Valid options are: \"remove\" or \"set\" <home>.");
                    return true;
                }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("set", "remove");
        }

        if (args[0].equalsIgnoreCase("set")) {
            Player player = (Player) sender;
            return ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());
        }
        return null;
    }
}
