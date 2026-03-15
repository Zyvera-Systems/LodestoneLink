package dev.zyverasystems.lodestoneLink.menu

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class MenuClickListener : Listener {
    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        val holder = e.clickedInventory?.holder as? MenuHolder ?: return
        e.isCancelled = true
        val loc = holder.getLocation(e.slot) ?: return
        e.whoClicked.teleportAsync(loc.add(0.5, 1.0, 0.5))
    }
}