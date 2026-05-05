package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class BHopModule extends Module {
    public BHopModule() { super("BHop", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (!mc.thePlayer.onGround) return;
        if (mc.thePlayer.moveForward > 0) {
            mc.thePlayer.jump();
        }
    }
}