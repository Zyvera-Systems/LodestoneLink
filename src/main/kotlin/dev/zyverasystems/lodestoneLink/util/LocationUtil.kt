package dev.zyverasystems.lodestoneLink.util

import org.bukkit.Bukkit
import org.bukkit.Location

object LocationUtil {
    /**
     * Serialize Location to String
     * @param loc
     * @return String
     * @see deserialize
     */
    fun serialize(loc: Location): String {
        return loc.getWorld().name + ":" +
                loc.x + ":" +
                loc.y + ":" +
                loc.z + ":" +
                loc.yaw + ":" +
                loc.pitch
    }

    /**
     * Deserialize String to Location
     * @param s raw data input
     * @return Location
     * @see serialize
     */
    fun deserialize(s: String): Location {
        val parts: Array<String?> = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return Location(
            Bukkit.getWorld(parts[0]!!),
            parts[1]!!.toDouble(),
            parts[2]!!.toDouble(),
            parts[3]!!.toDouble(),
            parts[4]!!.toFloat(),
            parts[5]!!.toFloat()
        )
    }
}