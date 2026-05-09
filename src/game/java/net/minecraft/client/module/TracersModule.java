package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

public class TracersModule extends Module {

    public TracersModule() {
        super("Tracers", "Render");
    }

    @Override
    public void onRender3D(float partialTicks) {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;

        Entity rv = mc.getRenderViewEntity();
        double eye = rv instanceof net.minecraft.entity.EntityLivingBase
                ? (double) ((net.minecraft.entity.EntityLivingBase) rv).getEyeHeight()
                : 1.62D;
        double sx = 0.0D;
        double sy = eye;
        double sz = 0.0D;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        EaglercraftGPU.glLineWidth(1.5F);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (Object o : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) o;
            if (entity == mc.thePlayer) continue;
            if (!(entity instanceof EntityLivingBase)) continue;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - viewerX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - viewerY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - viewerZ;

            worldrenderer.pos(sx, sy, sz).color(255, 0, 0, 255).endVertex();
            worldrenderer.pos(x, y + entity.height / 2.0F, z).color(255, 0, 0, 255).endVertex();
        }

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
