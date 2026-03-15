package dev.zyverasystems.lodestoneLink.menu

import dev.zyverasystems.lodestoneLink.SpecialCompass
import dev.zyverasystems.lodestoneLink.util.ConfigUtil
import dev.zyverasystems.lodestoneLink.util.ConfigUtil.getStringNn
import dev.zyverasystems.lodestoneLink.util.NamedLodestoneManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture
import com.cjcrafter.foliascheduler.FoliaCompatibility
import org.bukkit.plugin.java.JavaPlugin

object TeleportMenu {

    private val mm = MiniMessage.miniMessage()
    private lateinit var scheduler: FoliaCompatibility

    fun init(plugin: JavaPlugin) {
        this.scheduler = FoliaCompatibility(plugin)
    }

    fun open(player: Player, compassItem: ItemStack) {
        val locations = SpecialCompass.getLocationsFromItem(compassItem)

        val futures = locations.map { loc ->
            NamedLodestoneManager.getLodestoneName(loc).thenApply { name ->
                loc to name
            }
        }

        CompletableFuture.allOf(*futures.toTypedArray()).thenAccept {
            val holder = MenuHolder()
            val inv = Bukkit.createInventory(holder, ConfigUtil.config.getInt("menu.rows") * 9)

            futures.forEachIndexed { i, future ->
                val (loc, itemName) = future.join()

                val menuItem = ItemStack(
                    when (loc.world.name) {
                        "world" -> Material.GRASS_BLOCK
                        "world_nether" -> Material.NETHERRACK
                        "world_the_end" -> Material.END_STONE
                        else -> Material.CAKE
                    }
                )

                val meta = menuItem.itemMeta
                val displayName = itemName ?: mm.deserialize(ConfigUtil.config.getStringNn("menu.item.displayname"))
                meta.displayName(displayName)

                meta.lore(ConfigUtil.config.getStringList("menu.item.lore").map { line ->
                    mm.deserialize(
                        line.replace("{WORLD}", loc.world.name)
                            .replace("{X}", loc.blockX.toString())
                            .replace("{Y}", loc.blockY.toString())
                            .replace("{Z}", loc.blockZ.toString())
                    )
                })

                menuItem.itemMeta = meta

                holder.setLocation(i, loc)
                inv.setItem(i, menuItem)
            }

            scheduler.serverImplementation.entity(player).run(Runnable {
                player.openInventory(inv)
            })
        }
    }
}