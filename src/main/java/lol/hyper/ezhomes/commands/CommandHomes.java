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
import lol.hyper.ezhomes.gui.GUIManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandHomes implements CommandExecutor {

    private final EzHomes ezHomes;

    public CommandHomes(EzHomes ezHomes) {
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
            player.sendMessage(ChatColor.RED + "You don't have any homes set! Do /sethome <name> to set a home!");
            return true;
        }
        if (ezHomes.config.getBoolean("use-homes-gui")) {
            GUIManager gui = new GUIManager(player, ezHomes.homeManagement);
            gui.openGUI(0);
            return true;
        }
        sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
        player.sendMessage(ChatColor.GOLD + player.getDisplayName() + "' Homes");
        player.sendMessage(ChatColor.GOLD + "You will respawn at " + ChatColor.GREEN + ezHomes.homeManagement.getRespawnHomeName(player.getUniqueId()) + ChatColor.YELLOW + ".");
        player.spigot().sendMessage(ezHomes.homeManagement.getHomesClickable(player.getUniqueId()));
        sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
        return true;
    }
}
