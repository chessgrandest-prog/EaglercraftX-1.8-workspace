package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class ChestESPModule extends Module {
    public ChestESPModule() { super("ChestESP", "Render"); }

    @Override
    public void onRender3D(float partialTicks) {
        if (!isEnabled()) return;
        
        for (TileEntity te : mc.theWorld.loadedTileEntityList) {
            if (te instanceof TileEntityChest || te instanceof TileEntityEnderChest) {
                double x = te.getPos().getX() - mc.getRenderManager().viewerPosX;
                double y = te.getPos().getY() - mc.getRenderManager().viewerPosY;
                double z = te.getPos().getZ() - mc.getRenderManager().viewerPosZ;
                
                int color = te instanceof TileEntityChest ? 0xFFFFAA00 : 0xFFFF00FF; // Orange for chest, Magenta for ender chest
                renderBox(x, y, z, x + 1, y + 1, z + 1, color);
            }
        }
    }

    private void renderBox(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        int alpha = (color >> 24 & 255);
        int red = (color >> 16 & 255);
        int green = (color >> 8 & 255);
        int blue = (color & 255);
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        RenderGlobal.func_181563_a(new AxisAlignedBB(x1, y1, z1, x2, y2, z2), red, green, blue, alpha);
        
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
