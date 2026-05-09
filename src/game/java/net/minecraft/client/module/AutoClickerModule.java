package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class AutoClickerModule extends Module {
    public AutoClickerModule() { super("AutoClicker", "Combat"); }

    @Override
    public void onUpdate() {
        // Logic handled in Minecraft.java hook for timing accuracy
    }
}
