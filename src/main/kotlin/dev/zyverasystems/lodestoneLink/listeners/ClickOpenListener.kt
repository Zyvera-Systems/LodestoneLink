package dev.zyverasystems.lodestoneLink.listeners

import dev.zyverasystems.lodestoneLink.SpecialCompass
import dev.zyverasystems.lodestoneLink.menu.TeleportMenu
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class ClickOpenListener : Listener {
    @EventHandler
    fun onClickOpen(e: PlayerInteractEvent) {
        if (!e.action.isRightClick) return
        val item = e.item ?: return
        if (!SpecialCompass.isItem(item)) return
        TeleportMenu.open(e.player, item)
    }
}