package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class SpeedModule extends Module {
    private static final float SPEED_MULTIPLIER = 1.8F;

    public SpeedModule() { super("Speed", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        
        if (mc.thePlayer.onGround && (mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0)) {
            mc.thePlayer.motionX *= 1.25;
            mc.thePlayer.motionZ *= 1.25;
        }
    }
}
