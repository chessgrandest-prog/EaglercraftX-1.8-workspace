package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class StepModule extends Module {
    public StepModule() { super("Step", "Movement"); }

    @Override
    public void onUpdate() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        
        if (isEnabled()) {
            mc.thePlayer.stepHeight = 1.0F;
        } else {
            mc.thePlayer.stepHeight = 0.5F;
        }
    }

    @Override
    public void toggle() {
        if (isEnabled()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                mc.thePlayer.stepHeight = 0.5F; // reset to default
            }
        }
        super.toggle();
    }
}
