package dev.zyverasystems.lodestoneLink.menu

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class MenuHolder : InventoryHolder {
    private val locations = mutableMapOf<Int, Location>()

    override fun getInventory(): Inventory {
        return Bukkit.createInventory(this, 27)
    }

    fun getLocation(slot: Int): Location? {
        return locations[slot]
    }

    fun setLocation(slot: Int, loc: Location) {
        locations[slot] = loc
    }
}