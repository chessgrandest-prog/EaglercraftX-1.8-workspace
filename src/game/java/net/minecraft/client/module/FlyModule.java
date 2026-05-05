package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class FlyModule extends Module {
    public FlyModule() { super("Fly", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        mc.thePlayer.capabilities.isFlying = true;
        mc.thePlayer.capabilities.allowFlying = true;
        mc.thePlayer.sendPlayerAbilities();
    }

    @Override
    public void toggle() {
        if (isEnabled()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.capabilities.allowFlying = false;
                mc.thePlayer.sendPlayerAbilities();
            }
        }
        super.toggle();
    }
}