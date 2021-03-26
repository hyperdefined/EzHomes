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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Doing this here because I am lazy LOL
        int homeSize;
        if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()) == null) {
            homeSize = 0;
        } else {
            homeSize = ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()).size();
        }

        int argsLength = args.length;
        switch (argsLength) {
            case 0:
                player.sendMessage(ChatColor.RED + "You must specify a home name!");
                return true;
            case 1:
                if (homeSize != ezHomes.config.getInt("total-homes") || player.hasPermission("ezhomes.bypasslimit")) {
                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
                    Matcher matcher = pattern.matcher(args[0]);
                    if (matcher.matches()) {
                        sender.sendMessage(ChatColor.RED + "Invalid character in home name.");
                        return true;
                    }
                    ezHomes.homeManagement.createHome(player.getUniqueId(), args[0]);
                    sender.sendMessage(ChatColor.GREEN + "New home set.");
                } else {
                    player.sendMessage(ChatColor.RED + "You can only have a maximum of " + ezHomes.config.getInt("total-homes") + " homes.");
                }
                return true;
            default:
                player.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /sethome <home> to set a new home.");
                break;
        }
        return true;
    }
}
