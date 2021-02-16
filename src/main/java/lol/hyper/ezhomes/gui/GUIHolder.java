package lol.hyper.ezhomes.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {

    /**
     * This class is to detect the custom inventory for the GUI.
     * We set the owner to this class, and check if the inventory is an instance of this.
     */

    @Override
    public Inventory getInventory() {
        return null;
    }
}
