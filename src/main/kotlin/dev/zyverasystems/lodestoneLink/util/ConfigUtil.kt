package dev.zyverasystems.lodestoneLink.util

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

object ConfigUtil {
    lateinit var config: FileConfiguration
    private lateinit var plugin: JavaPlugin

    fun FileConfiguration.getStringNn(path: String, def: String = "Path $path not found"): String {
        return this.getString(path, def).toString()
    }

    fun init(plugin: JavaPlugin? = null) {
        if (plugin != null) {
            this.plugin = plugin
            this.config = plugin.config
        }

        reload()
    }

    private fun reload() {
        plugin.reloadConfig()
    }
}