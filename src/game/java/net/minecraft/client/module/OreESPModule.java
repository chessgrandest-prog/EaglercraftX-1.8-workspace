package net.minecraft.client.module;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class OreESPModule extends Module {
    private int scanTimer = 0;
    private java.util.List<BlockPos> orePositions = new java.util.ArrayList<>();

    public OreESPModule() { super("OreESP", "Render"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        scanTimer++;
        if (scanTimer < 40) return; // scan every 2 seconds
        scanTimer = 0;

        orePositions.clear();
        int px = (int) mc.thePlayer.posX;
        int py = (int) mc.thePlayer.posY;
        int pz = (int) mc.thePlayer.posZ;
        int radius = 16;

        for (int x = px - radius; x <= px + radius; x++) {
            for (int y = Math.max(1, py - radius); y <= Math.min(255, py + radius); y++) {
                for (int z = pz - radius; z <= pz + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = mc.theWorld.getBlockState(pos).getBlock();
                    if (isOre(block)) {
                        orePositions.add(pos);
                    }
                }
            }
        }
    }

    @Override
    public void onRender3D(float partialTicks) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;

        double vx = mc.getRenderManager().viewerPosX;
        double vy = mc.getRenderManager().viewerPosY;
        double vz = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        for (BlockPos pos : orePositions) {
            double x = pos.getX() - vx;
            double y = pos.getY() - vy;
            double z = pos.getZ() - vz;
            AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

            Block block = mc.theWorld.getBlockState(pos).getBlock();
            int[] color = getOreColor(block);
            RenderGlobal.func_181563_a(bb, color[0], color[1], color[2], 255);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private boolean isOre(Block block) {
        return block == Blocks.diamond_ore || block == Blocks.iron_ore
                || block == Blocks.gold_ore || block == Blocks.emerald_ore
                || block == Blocks.lapis_ore || block == Blocks.redstone_ore
                || block == Blocks.lit_redstone_ore || block == Blocks.coal_ore
                || block == Blocks.quartz_ore;
    }

    private int[] getOreColor(Block block) {
        if (block == Blocks.diamond_ore) return new int[]{0, 255, 255};
        if (block == Blocks.emerald_ore) return new int[]{0, 255, 0};
        if (block == Blocks.gold_ore) return new int[]{255, 215, 0};
        if (block == Blocks.iron_ore) return new int[]{200, 150, 100};
        if (block == Blocks.lapis_ore) return new int[]{0, 0, 255};
        if (block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore) return new int[]{255, 0, 0};
        if (block == Blocks.coal_ore) return new int[]{60, 60, 60};
        if (block == Blocks.quartz_ore) return new int[]{255, 255, 255};
        return new int[]{255, 255, 255};
    }
}
