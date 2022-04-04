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
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandHomes implements CommandExecutor {

    private final EzHomes ezHomes;

    public CommandHomes(EzHomes ezHomes) {
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
        if (ezHomes.config.getBoolean("use-homes-gui")) {
            GUIManager gui = new GUIManager(player, ezHomes.homeManagement, ezHomes.config.getBoolean("allow-respawn-homes"));
            gui.openGUI(0);
            return true;
        }

        for (String line : ezHomes.getMessageList("commands.homes.command")) {
            if (line.contains("%player%")) {
                line = line.replace("%player%", player.getName());
            }
            if (line.contains("%player%")) {
                line = line.replace("%player%", player.getName());
            }
            if (line.contains("%homes%")) {
                Component homesList = Component.empty();
                for (Component component : ezHomes.homeManagement.getHomesClickable(player.getUniqueId())) {
                    homesList = homesList.append(component);
                }
                ezHomes.getAdventure().player(player).sendMessage(homesList);
                continue;
            }
            if (line.contains("%respawnhome%")) {
                String respawnHome = ezHomes.homeManagement.getRespawnHomeName(player.getUniqueId());
                if (ezHomes.config.getBoolean("allow-respawn-at-home") && respawnHome != null) {
                    ezHomes.getAdventure().player(player).sendMessage(ezHomes.getMessage("commands.homes.respawn-home", respawnHome));
                }
                continue;
            }
            Component component = ezHomes.miniMessage.deserialize(line);
            ezHomes.getAdventure().player(player).sendMessage(component);
        }
        return true;
    }
}
