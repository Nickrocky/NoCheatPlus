/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.neatmonster.nocheatplus.compat.bukkit.model;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.util.BoundingBox;

import fr.neatmonster.nocheatplus.compat.Bridge1_13;
import fr.neatmonster.nocheatplus.utilities.map.BlockCache;

public class BukkitWall implements BukkitShapeModel {

    private final double minXZ;
    private final double maxXZ;
    private final double height;

    public BukkitWall(double inset, double height) {
        this(inset, 1.0 - inset, height);
    }

    public BukkitWall(double minXZ, double maxXZ, double height) {
        this.minXZ = minXZ;
        this.maxXZ = maxXZ;
        this.height = height;
    }

    @Override
    public double[] getShape(final BlockCache blockCache, 
            final World world, final int x, final int y, final int z) {

        final Block block = world.getBlockAt(x, y, z);
        if (Bridge1_13.hasBoundingBox()) {
            BoundingBox bd = block.getBoundingBox();
            return new double[] {bd.getMinX()-x, bd.getMinY()-y, bd.getMinZ()-z, bd.getMaxX()-x, bd.getMaxY()-y, bd.getMaxZ()-z};
        }
        final BlockState state = block.getState();
        final BlockData blockData = state.getBlockData();
        if (blockData instanceof MultipleFacing) {
            // Note isPassableWorkaround for these (no voxel shapes / multi cuboid yet).
            final MultipleFacing fence = (MultipleFacing) blockData;
            // TODO: If height > 1.0, check if it needs to be capped, provided relevant.
            double[] res = new double[] {minXZ, 0.0, minXZ, maxXZ, height, maxXZ};
            for (final BlockFace face : fence.getFaces()) {
                switch (face) {
                    case EAST:
                        res[3] = 1.0;
                        break;
                    case NORTH:
                        res[2] = 0.0;
                        break;
                    case WEST:
                        res[0] = 0.0;
                        break;
                    case SOUTH:
                        res[5] = 1.0;
                        break;
                    default:
                        break;

                }
            }
            final Set<BlockFace> a = fence.getFaces(); 
            if (!a.contains(BlockFace.UP) && a.size() == 2) {
            	if (a.contains(BlockFace.SOUTH)) {
            		res[0] = 0.33;
            		res[3] = 1 - 0.33;
            	}
            	if (a.contains(BlockFace.WEST)) {
            		res[2] = 0.33;
            		res[5] = 1 - 0.33;
            	}
            }
            return res;
        }

        return new double[] {0.0, 0.0, 0.0, 1.0, 1.0, 1.0};
    }

    @Override
    public int getFakeData(final BlockCache blockCache, 
            final World world, final int x, final int y, final int z) {
        return 0;
    }

}
