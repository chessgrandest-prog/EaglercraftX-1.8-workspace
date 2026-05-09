package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class SpiderModule extends Module {
    public SpiderModule() { super("Spider", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        if (mc.thePlayer.isCollidedHorizontally) {
            mc.thePlayer.motionY = 0.2;
        }
    }
}
