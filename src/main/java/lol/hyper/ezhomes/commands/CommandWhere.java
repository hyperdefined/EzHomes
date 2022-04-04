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
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandWhere implements TabExecutor {

    private final EzHomes ezHomes;

    public CommandWhere(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ezHomes.getAdventure().sender(sender).sendMessage(ezHomes.getMessage("errors.must-be-player", null));
            return true;
        }

        Player player = (Player) sender;
        if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()) == null) {
            ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("errors.no-homes", null));
            return true;
        }
        ArrayList<String> playerHomes = ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());

        int argsLength = args.length;
        switch (argsLength) {
            case 0:
                ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("errors.specify-home-name", null));
                return true;
            case 1:
                if (playerHomes.contains(args[0])) {
                    Location home = ezHomes.homeManagement.getHomeLocation(player.getUniqueId(), args[0]);
                    for (String line : ezHomes.getMessageList("commands.where.command")) {
                        if (line.contains("%home%")) {
                            line = line.replace("%home%", args[0]);
                        }
                        if (line.contains("%world%")) {
                            line = line.replace("%world%", home.getWorld().getName());
                        }
                        if (line.contains("%x%")) {
                            line = line.replace("%x%", String.valueOf((int) home.getX()));
                        }
                        if (line.contains("%y%")) {
                            line = line.replace("%y%", String.valueOf((int) home.getY()));
                        }
                        if (line.contains("%z%")) {
                            line = line.replace("%z%", String.valueOf((int) home.getZ()));
                        }
                        Component component = ezHomes.miniMessage.deserialize(line);
                        ezHomes.getAdventure().player(player).sendMessage(component);
                    }
                } else {
                    ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("errors.home-does-not-exist", null));
                }
                return true;
            default:
                ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("commands.where.invalid-syntax", null));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        return ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());
    }
}
