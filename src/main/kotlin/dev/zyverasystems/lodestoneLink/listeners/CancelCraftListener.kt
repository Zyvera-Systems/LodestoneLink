package dev.zyverasystems.lodestoneLink.listeners

import dev.zyverasystems.lodestoneLink.LodestoneLink
import dev.zyverasystems.lodestoneLink.SpecialCompass
import net.kyori.adventure.key.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent

class CancelCraftListener : Listener {
    @EventHandler
    fun cancelOther(e: PrepareItemCraftEvent) {
        if (e.inventory.matrix.any { item -> item != null && SpecialCompass.isItem(item) }) {
            e.inventory.result = null
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun cancelItemCraft(event: PrepareItemCraftEvent) {

        val recipe = event.recipe as? Keyed ?: return
        val player = event.view.player as? Player ?: return

        if (recipe.key() != NamespacedKey(LodestoneLink.instance, "custom_compass")) {
            return
        }

        if (!player.hasPermission("lodestonelink.craft")) {
            event.inventory.result = null
        }
    }
}