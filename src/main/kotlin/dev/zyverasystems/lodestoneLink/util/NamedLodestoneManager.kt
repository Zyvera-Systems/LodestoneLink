package dev.zyverasystems.lodestoneLink.util

import com.cjcrafter.foliascheduler.FoliaCompatibility
import dev.zyverasystems.lodestoneLink.LodestoneLink
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.CompletableFuture


class NamedLodestoneManager : Listener {
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

            pdc[getBlockKey(block), PersistentDataType.STRING] = serializedName
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
            val serializedName = pdc[key, PersistentDataType.STRING]
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
        private fun getBlockKey(block: Block): NamespacedKey {
            return NamespacedKey(LodestoneLink.instance, "lodestone_" + block.x + "_" + block.y + "_" + block.z)
        }

        fun getLodestoneDisplay(loc: Location): CompletableFuture<Pair<Component?, Material?>?> {
            val future = CompletableFuture<Pair<Component?, Material?>?>()

            FoliaCompatibility(LodestoneLink.instance).serverImplementation.region(loc).run(Runnable {
                val block = loc.block

                if (block.type != Material.LODESTONE) {
                    future.complete(null)
                    return@Runnable
                }

                if (loc.clone().add(0.0, 1.0, 0.0).block.type.isAir || loc.clone().add(0.0, 2.0, 0.0).block.type.isAir) {
                    future.complete(null)
                    return@Runnable
                }

                val chunk = loc.chunk
                val pdc = chunk.persistentDataContainer
                val key = getBlockKey(block)
                val itemFrame = try {
                    loc.clone().add(0.0, 1.0, 0.0).getNearbyEntities(1.0, 1.0, 1.0)
                        .first { it is ItemFrame } as? ItemFrame
                } catch (_: NoSuchElementException) {
                    null
                }
                val displayMaterial = if (itemFrame == null || itemFrame.item.type == Material.AIR) {
                    null
                } else {
                    itemFrame.item.type
                }

                if (pdc.has(key, PersistentDataType.STRING)) {
                    val serializedName = pdc[key, PersistentDataType.STRING]
                    future.complete(Pair(GsonComponentSerializer.gson().deserialize(serializedName!!), displayMaterial))
                } else {
                    future.complete(Pair(null, displayMaterial))
                }
            })

            return future
        }
    }
}