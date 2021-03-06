/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.world;

import java.util.Arrays;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

public class WorldCopier {

    public void copyArena(SkyBlockLocation toMin, SkyBlockLocationRange from) {
        Validate.notNull(toMin, "ToMin cannot be null");
        Validate.notNull(from, "From cannot be null");
        copy(from.min, from.max, toMin);
    }

    public void destroyArena(SkyBlockLocation arenaMin, SkyBlockLocationRange area) {
        Validate.notNull(arenaMin, "ArenaMin cannot be null");
        Validate.notNull(area, "Area cannot be null");
        World world = Bukkit.getWorld(arenaMin.world);
        Validate.isTrue(world != null, "World not found");
        SkyBlockLocation clearingMin = new SkyBlockLocation(arenaMin.x + area.min.x, arenaMin.y + area.min.y, arenaMin.z + area.min.z, null);
        SkyBlockLocation clearingMax = new SkyBlockLocation(arenaMin.x + area.max.x, arenaMin.y + area.max.y, arenaMin.z + area.max.z, null);
        destroyArena(clearingMin, clearingMax, world);
    }

    public void destroyArena(SkyBlockLocation min, SkyBlockLocation max, World world) {
        Validate.notNull(min, "Min cannot be null");
        Validate.notNull(max, "Max cannot be null");
        Validate.notNull(world, "World cannot be null");
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    public void copy(SkyBlockLocation fromMin, SkyBlockLocation fromMax, SkyBlockLocation toMin) {
        Validate.notNull(fromMin, "FromMin cannot be null");
        Validate.notNull(fromMax, "FromMax cannot be null");
        Validate.notNull(toMin, "ToMin cannot be null");
        Validate.isTrue(fromMin.world.equals(fromMax.world), "From min and from max must be in the same world");
        World fromWorld = Bukkit.getWorld(fromMin.world);
        World toWorld = Bukkit.getWorld(toMin.world);
        Validate.notNull(fromWorld, "From world doesn't exist");
        Validate.notNull(toWorld, "To world doesn't exist");
        int xLength = fromMax.x - fromMin.x;
        int yLength = fromMax.y - fromMin.y;
        int zLength = fromMax.z - fromMin.z;
        for (int x = 0; x <= xLength; x++) {
            for (int y = 0; y <= yLength; y++) {
                for (int z = 0; z <= zLength; z++) {
                    Block from = fromWorld.getBlockAt(fromMin.x + x, fromMin.y + y, fromMin.z + z);
                    Block to = toWorld.getBlockAt(toMin.x + x, toMin.y + y, toMin.z + z);
                    //noinspection deprecation
                    to.setTypeIdAndData(from.getTypeId(), from.getData(), false);
                    BlockState fromState = from.getState();
                    if (fromState instanceof Chest) {
                        Chest toChest = (Chest) to.getState();
                        Chest fromChest = (Chest) fromState;
                        ItemStack[] contents = fromChest.getBlockInventory().getContents();
                        toChest.getBlockInventory().setContents(Arrays.copyOf(contents, contents.length));
                        toChest.update(true, false);
                    } else if (fromState instanceof Dispenser) {
                        Dispenser toDispenser = (Dispenser) to.getState();
                        Dispenser fromDispenser = (Dispenser) fromState;
                        ItemStack[] contents = fromDispenser.getInventory().getContents();
                        toDispenser.getInventory().setContents(Arrays.copyOf(contents, contents.length));
                        toDispenser.update(true, false);
                    }
                }
            }
        }
    }
}
