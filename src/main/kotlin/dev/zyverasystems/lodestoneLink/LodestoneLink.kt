package dev.zyverasystems.lodestoneLink

import dev.zyverasystems.lodestoneLink.listeners.AddWaystoneListener
import dev.zyverasystems.lodestoneLink.listeners.ClickOpenListener
import dev.zyverasystems.lodestoneLink.menu.MenuClickListener
import dev.zyverasystems.lodestoneLink.menu.TeleportMenu
import dev.zyverasystems.lodestoneLink.util.ConfigUtil
import dev.zyverasystems.lodestoneLink.util.ConfigUtil.getStringNn
import dev.zyverasystems.lodestoneLink.util.NamedLodestoneManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class LodestoneLink : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()

        ConfigUtil.init(this)
        SpecialCompass.init(this)
        TeleportMenu.init(this)
        registerCrafting()

        server.pluginManager.registerEvents(AddWaystoneListener(), this)
        server.pluginManager.registerEvents(ClickOpenListener(), this)
        server.pluginManager.registerEvents(NamedLodestoneManager(this), this)
        server.pluginManager.registerEvents(MenuClickListener(), this)
    }

    fun registerCrafting() {
        val specialRounding = Material.valueOf(config.getStringNn("crafting.surround-item", "DIAMOND"))
        Bukkit.addRecipe(
            ShapedRecipe(
                NamespacedKey(this, "compass"),
                SpecialCompass.item()
            ).shape(
                "AAA",
                "ABA",
                "AAA"
            )
                .setIngredient('A', specialRounding)
                .setIngredient('B', Material.COMPASS)
        )
    }
}
