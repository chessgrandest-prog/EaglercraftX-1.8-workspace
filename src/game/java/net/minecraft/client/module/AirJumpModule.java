package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class AirJumpModule extends Module {
    public AirJumpModule() { super("AirJump", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            if (mc.thePlayer.onGround) return; // normal jump
            mc.thePlayer.motionY = 0.42D; // jump boost in air
        }
    }
}