package net.minecraft.client.module;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MinimapModule extends Module {

    private static final int SIZE = 64;
    private static final int UPDATE_INTERVAL = 10;

    private int tickCounter = 0;
    private boolean needsUpdate = true;

    private DynamicTexture texture;
    private ResourceLocation textureLocation;

    public MinimapModule() {
        super("Minimap", "Render");
    }

    @Override
    public void toggle() {
        super.toggle();
        needsUpdate = true;
    }

    public void renderMinimap() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        if (texture == null) {
            texture = new DynamicTexture(SIZE, SIZE);
            textureLocation = mc.getTextureManager().getDynamicTextureLocation("minimap", texture);
        }

        tickCounter++;
        if (tickCounter >= UPDATE_INTERVAL) {
            tickCounter = 0;
            needsUpdate = true;
        }

        if (needsUpdate) {
            updateMinimap(mc);
            texture.updateDynamicTexture();
            needsUpdate = false;
        }

        ScaledResolution sr = mc.scaledResolution != null ? mc.scaledResolution : new ScaledResolution(mc);
        int sw = sr.getScaledWidth();
        int x = sw - SIZE - 10;
        int y = 10;

        Gui.drawRect(x - 1, y - 1, x + SIZE + 1, y + SIZE + 1, 0xAA000000);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(textureLocation);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, SIZE, SIZE, SIZE, SIZE);

        int cx = x + SIZE / 2;
        int cy = y + SIZE / 2;
        Gui.drawRect(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFFFFFF);
        Gui.drawRect(cx, cy, cx + 1, cy + 1, 0xFFFF0000);
    }

    private void updateMinimap(Minecraft mc) {
        World world = mc.theWorld;
        int playerX = (int) Math.floor(mc.thePlayer.posX);
        int playerZ = (int) Math.floor(mc.thePlayer.posZ);

        int[] pixels = texture.getTextureData();

        for (int iz = 0; iz < SIZE; iz++) {
            for (int ix = 0; ix < SIZE; ix++) {
                int worldX = playerX + (ix - SIZE / 2);
                int worldZ = playerZ + (iz - SIZE / 2);

                Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(worldX, 0, worldZ));
                int lx = Math.floorMod(worldX, 16);
                int lz = Math.floorMod(worldZ, 16);
                int y = chunk.getHeightValue(lx, lz);

                BlockPos pos = new BlockPos(worldX, y, worldZ);
                Block block = world.getBlockState(pos).getBlock();

                while (block.getMaterial() == Material.air && y > 0) {
                    y--;
                    pos = new BlockPos(worldX, y, worldZ);
                    block = world.getBlockState(pos).getBlock();
                }

                MapColor mapColor = block.getMapColor(world.getBlockState(pos));
                int argb;
                if (mapColor == null) {
                    argb = 0xFF202020;
                } else {
                    int rgb = mapColor.colorValue & 0xFFFFFF;
                    argb = 0xFF000000 | rgb;
                }
                pixels[iz * SIZE + ix] = argb;
            }
        }
    }
}
