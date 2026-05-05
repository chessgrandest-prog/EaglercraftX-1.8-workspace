package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ESPModule extends Module {

    public ESPModule() {
        super("ESP", "Render");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        // Force all living entities to show their nametag
        for (Object o : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) o;
            if (entity == mc.thePlayer) continue;
            if (entity instanceof EntityLivingBase) {
                entity.setAlwaysRenderNameTag(true);
            }
        }
    }

    @Override
    public void toggle() {
        super.toggle();
        // When disabled, stop forcing nametags
        if (!isEnabled()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                for (Object o : mc.theWorld.loadedEntityList) {
                    Entity entity = (Entity) o;
                    if (entity instanceof EntityLivingBase) {
                        entity.setAlwaysRenderNameTag(false);
                    }
                }
            }
        }
    }
}