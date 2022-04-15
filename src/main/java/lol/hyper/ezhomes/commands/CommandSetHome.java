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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandSetHome implements CommandExecutor {

    private final EzHomes ezHomes;

    public CommandSetHome(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ezHomes.getAdventure().sender(sender).sendMessage(ezHomes.getMessage("errors.must-be-player", null));
            return true;
        }

        Player player = (Player) sender;

        // Doing this here because I am lazy LOL
        int homeSize;
        if (ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()) == null) {
            homeSize = 0;
        } else {
            homeSize =
                    ezHomes.homeManagement.getPlayerHomes(player.getUniqueId()).size();
        }

        int argsLength = args.length;
        switch (argsLength) {
            case 0:
                ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("errors.specify-home-name", null));
                return true;
            case 1:
                ArrayList<String> homes = ezHomes.homeManagement.getPlayerHomes(player.getUniqueId());
                if (homeSize != ezHomes.config.getInt("total-homes") || player.hasPermission("ezhomes.bypasslimit")) {
                    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
                    Matcher matcher = pattern.matcher(args[0]);
                    if (!matcher.matches()) {
                        ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("errors.invalid-characters", null));
                        return true;
                    }
                    // check for duplicates
                    if (homeSize != 0 && homes != null) {
                        if (homes.stream().anyMatch(x -> x.equalsIgnoreCase(args[0]))) {
                            ezHomes.getAdventure()
                                    .player(player)
                                    .sendMessage(
                                            ezHomes.getMessage("errors.home-already-exists", null));
                            return true;
                        }
                    }
                    ezHomes.homeManagement.createHome(player.getUniqueId(), args[0]);
                    ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("commands.sethome.new-home", player));
                } else {
                    ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("commands.sethome.home-limit", ezHomes.config.getInt("total-homes")));
                }
                return true;
            default:
                ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("commands.sethome.invalid-syntax", null));
                break;
        }
        return true;
    }
}
