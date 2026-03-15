package dev.zyverasystems.lodestoneLink.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import com.cjcrafter.foliascheduler.FoliaCompatibility
import java.util.concurrent.CompletableFuture


class NamedLodestoneManager(plugin: JavaPlugin) : Listener {
    init {
        init(plugin)
    }

    @EventHandler
    fun onLodestonePlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced

        if (block.type != Material.LODESTONE) return

        val item = event.getItemInHand()
        val meta = item.itemMeta

        if (meta != null && meta.hasDisplayName()) {
            val nameComponent = meta.displayName()

            val serializedName = GsonComponentSerializer.gson().serialize(nameComponent!!)

            val chunk = block.chunk
            val pdc = chunk.persistentDataContainer

            pdc.set(getBlockKey(block), PersistentDataType.STRING, serializedName)
        }
    }

    @EventHandler
    fun onLodestoneBreak(event: BlockBreakEvent) {
        val block = event.getBlock()

        if (block.type != Material.LODESTONE) return

        val chunk = block.chunk
        val pdc = chunk.persistentDataContainer
        val key = getBlockKey(block)

        if (pdc.has(key, PersistentDataType.STRING)) {
            val serializedName = pdc.get(key, PersistentDataType.STRING)
            val nameComponent = GsonComponentSerializer.gson().deserialize(serializedName!!)


            event.isDropItems = false
            val dropItem = ItemStack(Material.LODESTONE)
            val dropMeta = dropItem.itemMeta
            dropMeta.displayName(nameComponent)
            dropItem.setItemMeta(dropMeta)
            block.world.dropItemNaturally(block.location, dropItem)

            pdc.remove(key)
        }
    }

    companion object {
        private lateinit var plugin: JavaPlugin

        fun init(plugin: JavaPlugin) {
            this.plugin = plugin
        }

        private fun getBlockKey(block: Block): NamespacedKey {
            return NamespacedKey(plugin, "lodestone_" + block.x + "_" + block.y + "_" + block.z)
        }

        fun getLodestoneName(loc: Location): CompletableFuture<Component?> {
            val future = CompletableFuture<Component?>()

            FoliaCompatibility(plugin).serverImplementation.region(loc).run (Runnable {
                val block = loc.block

                if (block.type != Material.LODESTONE) {
                    future.complete(null)
                    return@Runnable
                }

                val chunk = loc.chunk
                val pdc = chunk.persistentDataContainer
                val key = getBlockKey(block)

                if (pdc.has(key, PersistentDataType.STRING)) {
                    val serializedName = pdc.get(key, PersistentDataType.STRING)
                    future.complete(GsonComponentSerializer.gson().deserialize(serializedName!!))
                } else {
                    future.complete(null)
                }
            })

            return future
        }
    }
}