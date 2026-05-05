package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class FullbrightModule extends Module {

    private float originalGamma = 0.5F; // safe default

    public FullbrightModule() {
        super("Fullbright", "Render");
    }

    @Override
    public void onUpdate() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings == null) return;

        if (this.isEnabled()) {
            // Set gamma to max (10.0) every tick while enabled
            mc.gameSettings.gammaSetting = 10.0F;
        } else {
            // Restore to a normal value (we store it when first toggled)
            // This could be refined, but for now we just keep it at 10
            // A better way: cache the original gamma when enabled and restore when disabled
            // For simplicity, we'll just not change it back automatically here,
            // but the module system doesn't have an explicit "onDisable" yet.
            // We'll add a simple workaround:
        }
    }

    @Override
    public void toggle() {
        super.toggle();
        // If we just enabled, cache the current gamma
        if (isEnabled()) {
            originalGamma = Minecraft.getMinecraft().gameSettings.gammaSetting;
        } else {
            // Restore the original gamma when disabled
            Minecraft.getMinecraft().gameSettings.gammaSetting = originalGamma;
        }
    }
}