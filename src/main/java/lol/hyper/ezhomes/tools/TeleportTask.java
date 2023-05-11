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
import lol.hyper.ezhomes.events.PlayerMove;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private final EzHomes ezHomes;
    private final Player player;
    private final Location location;
    private final BukkitAudiences audiences;
    private final HomeManagement homeManagement;
    private final PlayerMove playerMove;
    int seconds;

    public TeleportTask(EzHomes ezHomes, Player player, Location location) {
        this.ezHomes = ezHomes;
        this.player = player;
        this.location = location;
        this.audiences = ezHomes.getAdventure();
        this.homeManagement = ezHomes.homeManagement;
        this.playerMove = ezHomes.playerMove;
        seconds = ezHomes.config.getInt("seconds-to-teleport");
    }

    @Override
    public void run() {
        if (seconds == 0) {
            ezHomes.isTeleporting.add(player.getUniqueId());
            PaperLib.teleportAsync(player, location);

            if(ezHomes.config.getBoolean("play-warp-sound")) {
                player.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }

            homeManagement.teleportCooldowns.put(player.getUniqueId(), System.nanoTime());
            audiences.player(player).sendMessage(ezHomes.getMessage("commands.home.on-teleport"));
            playerMove.teleportTasks.remove(player.getUniqueId());
            this.cancel();
        } else {
            audiences.player(player).sendMessage(ezHomes.getMessage("commands.home.teleporting-in", seconds));
            seconds--;
        }
    }
}
