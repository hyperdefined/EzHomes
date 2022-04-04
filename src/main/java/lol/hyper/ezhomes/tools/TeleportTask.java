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

package lol.hyper.ezhomes.tools;

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.EzHomes;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private final EzHomes ezHomes;
    private final Player player;
    private final Location location;
    int seconds;

    public TeleportTask(EzHomes ezHomes, Player player, Location location) {
        this.ezHomes = ezHomes;
        this.player = player;
        this.location = location;
        seconds = ezHomes.config.getInt("seconds-to-teleport");
    }

    @Override
    public void run() {
        if (seconds == 0) {
            PaperLib.teleportAsync(player, location);
            ezHomes.homeManagement.teleportCooldowns.put(player.getUniqueId(), System.nanoTime());
            ezHomes.adventure().player(player).sendMessage(ezHomes.getMessage("commands.home.on-teleport", null));
            ezHomes.playerMove.teleportTasks.remove(player.getUniqueId());
            this.cancel();
        } else {
            ezHomes.adventure().player(player).sendMessage(ezHomes.getMessage("commands.home.teleporting-in", seconds));
            seconds--;
        }
    }
}
