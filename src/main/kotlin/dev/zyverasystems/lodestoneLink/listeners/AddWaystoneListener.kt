package dev.zyverasystems.lodestoneLink.listeners

import dev.zyverasystems.lodestoneLink.SpecialCompass
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class AddWaystoneListener : Listener {
    @EventHandler
    fun onClickAdd(e: PlayerInteractEvent) {
        val item = e.item ?: return
        if (e.hand != EquipmentSlot.HAND) return
        if (!SpecialCompass.isItem(item)) return
        if (e.clickedBlock?.type != Material.LODESTONE) return
        e.isCancelled = true
        if (!e.action.isLeftClick) return

        val new = SpecialCompass.addLocationToItem(item, e.clickedBlock?.location) ?: return
        e.player.inventory.setItem(e.player.inventory.heldItemSlot, new)
    }
}