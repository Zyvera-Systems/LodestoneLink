package dev.zyverasystems.lodestoneLink

import dev.zyverasystems.lodestoneLink.util.ConfigUtil.getStringNn
import dev.zyverasystems.lodestoneLink.util.LocationUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

object SpecialCompass {
    private lateinit var key: NamespacedKey
    private val mm = MiniMessage.miniMessage()
    private lateinit var plugin: JavaPlugin

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        key = NamespacedKey(plugin, "lodestone-locations")
    }

    fun item(): ItemStack {
        val item = ItemStack(Material.COMPASS)
        val meta = item.itemMeta

        meta.displayName(mm.deserialize(plugin.config.getStringNn("item.displayname", "<red>MISSING CONFIG VALUE FOR: <yellow>item.displayname")))
        meta.lore(plugin.config.getStringList("item.lore").mapNotNull { mm.deserialize(it) })

        val data = meta.persistentDataContainer
        data.set(key, PersistentDataType.STRING, "")
        item.itemMeta = meta
        return item
    }

    fun isItem(item: ItemStack): Boolean {
        return item.itemMeta.persistentDataContainer.has(key)
    }

    fun addLocationToItem(itemFrom: ItemStack, loc: Location?): ItemStack? {
        loc ?: return null
        val item = itemFrom.clone()
        val meta = item.itemMeta ?: return null
        if (!meta.persistentDataContainer.has(key)) return null
        val locations = getLocationsFromItem(item).toMutableList()
        if (locations.size < 27) {
            locations.add(loc)
        }

        meta.persistentDataContainer.set(key, PersistentDataType.STRING,
            locations.toSet().joinToString(";") { location -> LocationUtil.serialize(location) })

        item.setItemMeta(meta)

        return item
    }

    fun getLocationsFromItem(item: ItemStack): List<Location> {
        val meta = item.itemMeta ?: return listOf()

        val rawData = meta.persistentDataContainer.get(key, PersistentDataType.STRING)

        if (rawData.isNullOrEmpty()) {
            return listOf()
        }

        return rawData.split(';').toSet().map { LocationUtil.deserialize(it) }
    }
}
