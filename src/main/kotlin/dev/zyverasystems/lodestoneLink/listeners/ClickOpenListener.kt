package dev.zyverasystems.lodestoneLink.listeners

import com.cjcrafter.foliascheduler.FoliaCompatibility
import dev.zyverasystems.lodestoneLink.SpecialCompass
import dev.zyverasystems.lodestoneLink.menu.TeleportMenu
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class ClickOpenListener(private val plugin: JavaPlugin) : Listener {
    val schedler = FoliaCompatibility(plugin).serverImplementation

    @EventHandler
    fun onClickOpen(e: PlayerInteractEvent) {
        if (!e.action.isRightClick) return
        val item = e.item ?: return
        if (!SpecialCompass.isItem(item)) return

        SpecialCompass.getLocationsFromItem(item).forEach { loc ->
            schedler.region(loc).run(Runnable {
                if (loc.block.type != Material.LODESTONE) {
                    val new = SpecialCompass.removeLocationFromItem(item, loc)
                    e.player.inventory.setItemInMainHand(new)
                }
            })
        }

        TeleportMenu.open(e.player, item)
    }
}