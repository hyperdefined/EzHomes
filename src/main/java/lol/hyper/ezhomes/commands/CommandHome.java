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

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.EzHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandHome implements TabExecutor {

    private final EzHomes ezHomes;

    public CommandHome(EzHomes ezHomes) {
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

        ArrayList < String > playerHomes = ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());

        int argsLength = args.length;
        switch (argsLength) {
            case 0:
                player.sendMessage(ChatColor.RED + "You must specify a home!");
                return true;
            case 1:
                if (ezHomes.homeManagement.canPlayerTeleport(player.getUniqueId()) || player.hasPermission("ezhomes.bypasscooldown")) {
                    if (playerHomes.contains(args[0])) {
                        PaperLib.teleportAsync(player, ezHomes.homeManagement.getHomeLocation(player.getUniqueId(), args[0]));
                        player.sendMessage(ChatColor.GREEN + "Whoosh!");
                        ezHomes.homeManagement.teleportCooldowns.put(player.getUniqueId(), System.nanoTime());
                    } else {
                        player.sendMessage(ChatColor.RED + "That home does not exist.");
                    }
                } else {
                    long timeLeft = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - ezHomes.homeManagement.teleportCooldowns.get(player.getUniqueId()));
                    long configTime = ezHomes.config.getInt("teleport-cooldown");
                    player.sendMessage(ChatColor.RED + "You must wait " + (configTime - timeLeft) + " seconds to teleport.");
                }
                return true;
            default:
                player.sendMessage(ChatColor.RED + "Invalid command usage. Usage: /home <home> to teleport to a home.");
                break;
        }
        return true;
    }

    @Override
    public List < String > onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;
        return ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());
    }
}