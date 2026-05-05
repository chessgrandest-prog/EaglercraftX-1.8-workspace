package net.minecraft.client.module;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MinimapModule extends Module {

    private static final int SIZE = 64;            // map width/height in pixels
    private static final int UPDATE_INTERVAL = 10;  // ticks between updates

    private int[] colors = new int[SIZE * SIZE];   // cached pixel colours
    private int tickCounter = 0;
    private boolean needsUpdate = true;

    public MinimapModule() {
        super("Minimap", "Render");
    }

    @Override
    public void toggle() {
        super.toggle();
        needsUpdate = true;  // refresh when toggled on
    }

    /** Called from GuiIngame.renderGameOverlay */
    public void renderMinimap() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        tickCounter++;
        if (tickCounter >= UPDATE_INTERVAL) {
            tickCounter = 0;
            needsUpdate = true;
        }

        if (needsUpdate) {
            updateMinimap(mc);
            needsUpdate = false;
        }

        // Position (top‑right, adjust as you like)
        int x = mc.displayWidth / 2 + 100;
        int y = 10;

        // Dark frame
        Gui.drawRect(x - 1, y - 1, x + SIZE + 1, y + SIZE + 1, 0xAA000000);

        // Draw terrain
        for (int py = 0; py < SIZE; py++) {
            for (int px = 0; px < SIZE; px++) {
                int idx = py * SIZE + px;
                int color = colors[idx];
                if (color != 0) {
                    Gui.drawRect(x + px, y + py, x + px + 1, y + py + 1, color);
                }
            }
        }

        // Player marker (white border with red center)
        int cx = x + SIZE / 2;
        int cy = y + SIZE / 2;
        Gui.drawRect(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);
        Gui.drawRect(cx, cy, cx + 1, cy + 1, 0xFFFF0000);
    }

    private void updateMinimap(Minecraft mc) {
        World world = mc.theWorld;
        int playerX = (int) Math.floor(mc.thePlayer.posX);
        int playerZ = (int) Math.floor(mc.thePlayer.posZ);

        for (int iz = 0; iz < SIZE; iz++) {
            for (int ix = 0; ix < SIZE; ix++) {
                int worldX = playerX + (ix - SIZE / 2);
                int worldZ = playerZ + (iz - SIZE / 2);

                // Find the highest solid block at this column
                BlockPos topPos = getTopBlock(world, worldX, worldZ);
                if (topPos == null) {
                    colors[iz * SIZE + ix] = 0xFF000000; // black if void
                    continue;
                }

                Block block = world.getBlockState(topPos).getBlock();
                MapColor mapColor = block.getMapColor(world.getBlockState(topPos));
                if (mapColor == null) {
                    colors[iz * SIZE + ix] = 0xFF000000;
                } else {
                    colors[iz * SIZE + ix] = mapColor.colorValue | 0xFF000000;
                }
            }
        }
    }

    /**
     * Finds the highest non‑air block directly above the ground,
     * starting from the build limit and moving down.
     */
    private BlockPos getTopBlock(World world, int x, int z) {
        for (int y = 255; y > 0; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (world.getBlockState(pos).getBlock().getMaterial() != Material.air) {
                return pos;
            }
        }
        return null;
    }
}