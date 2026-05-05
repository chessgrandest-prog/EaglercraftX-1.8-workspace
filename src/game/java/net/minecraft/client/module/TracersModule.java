package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class TracersModule extends Module {

    public TracersModule() {
        super("Tracers", "Render");
    }

    @Override
    public void onRender(float partialTicks) {   // <-- now matches the base class
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        int cx = mc.displayWidth / 2;
        int cy = mc.displayHeight / 2;

        for (Object o : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) o;
            if (entity == mc.thePlayer) continue;
            if (!(entity instanceof EntityLivingBase)) continue;

            double dx = entity.posX - mc.getRenderManager().viewerPosX;
            double dy = entity.posY + entity.getEyeHeight() - mc.getRenderManager().viewerPosY;
            double dz = entity.posZ - mc.getRenderManager().viewerPosZ;

            if (dz <= 0.1) continue;

            double fov = 70.0;
            double scale = mc.displayHeight / (2.0 * Math.tan(Math.toRadians(fov / 2.0)));
            double sx = mc.displayWidth / 2.0 + (dx / dz) * scale;
            double sy = mc.displayHeight / 2.0 - (dy / dz) * scale;

            drawLine(cx, cy, (int)sx, (int)sy, 0xFFFF0000);
        }
    }

    private void drawLine(int x1, int y1, int x2, int y2, int color) {
        if (x1 == x2 && y1 == y2) return;
        float dx = x2 - x1;
        float dy = y2 - y1;
        float steps = Math.max(Math.abs(dx), Math.abs(dy));
        float xInc = dx / steps;
        float yInc = dy / steps;
        for (int i = 0; i <= steps; i++) {
            int x = (int)(x1 + xInc * i);
            int y = (int)(y1 + yInc * i);
            Gui.drawRect(x, y, x + 1, y + 1, color);
        }
    }
}