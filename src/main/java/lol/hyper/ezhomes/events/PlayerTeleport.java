package lol.hyper.ezhomes.events;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.tools.HomeManagement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class PlayerTeleport implements Listener {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;

    public PlayerTeleport(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.homeManagement = ezHomes.homeManagement;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (ezHomes.isTeleporting.contains(uuid)) {
            ezHomes.isTeleporting.remove(uuid);
            homeManagement.guiManagers.remove(uuid);
        }
    }
}
