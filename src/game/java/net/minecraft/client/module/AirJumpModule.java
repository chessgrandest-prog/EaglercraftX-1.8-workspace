package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

/** Former Phase behavior: clip-style movement assist (see PhaseModule for wall phase). */
public class AirJumpModule extends Module {
    public AirJumpModule() { super("AirJump", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        mc.thePlayer.noClip = true;
        mc.thePlayer.fallDistance = 0;
        mc.thePlayer.onGround = true;
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = false;
        }
    }
}
