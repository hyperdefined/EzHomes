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

package lol.hyper.ezhomes.events;

import lol.hyper.ezhomes.EzHomes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    private final EzHomes ezHomes;

    public PlayerLeave(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ezHomes.homeManagement.guiManagers.remove(event.getPlayer());
        if (ezHomes.playerMove.teleportTasks.containsKey(event.getPlayer().getUniqueId())) {
            ezHomes.playerMove.teleportTasks.get(event.getPlayer().getUniqueId()).cancel();
            ezHomes.playerMove.teleportTasks.remove(event.getPlayer().getUniqueId());
        }
    }
}
