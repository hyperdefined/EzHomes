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
import lol.hyper.ezhomes.tools.HomeManagement;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandWhere implements TabExecutor {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;
    private final BukkitAudiences audiences;
    private final MiniMessage miniMessage;

    public CommandWhere(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.homeManagement = ezHomes.homeManagement;
        this.audiences = ezHomes.getAdventure();
        this.miniMessage = ezHomes.miniMessage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("ezhomes.where")) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("no-perms", null));
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            audiences.sender(sender).sendMessage(ezHomes.getMessage("errors.must-be-player"));
            return true;
        }

        Player player = (Player) sender;
        List<String> playerHomes = homeManagement.getPlayerHomes(player.getUniqueId());
        if (playerHomes.isEmpty()) {
            audiences.player(player).sendMessage(ezHomes.getMessage("errors.no-homes"));
            return true;
        }

        int argsLength = args.length;
        switch (argsLength) {
            case 0: {
                audiences.player(player).sendMessage(ezHomes.getMessage("errors.specify-home-name"));
                return true;
            }
            case 1: {
                String homeName = args[0];
                if (playerHomes.contains(homeName)) {
                    Location home = homeManagement.getHomeLocation(player.getUniqueId(), homeName);
                    for (String line : ezHomes.getMessageList("commands.where.command")) {
                        if (line.contains("%home%")) {
                            line = line.replace("%home%", homeName);
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
                        Component component = miniMessage.deserialize(line);
                        audiences.player(player).sendMessage(component);
                    }
                } else {
                    audiences.player(player).sendMessage(ezHomes.getMessage("errors.home-does-not-exist"));
                }
                return true;
            }
            default: {
                audiences.player(player).sendMessage(ezHomes.getMessage("commands.where.invalid-syntax"));
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        return homeManagement.getPlayerHomes(player.getUniqueId());
    }
}
