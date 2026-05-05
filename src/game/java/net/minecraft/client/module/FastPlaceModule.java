package net.minecraft.client.module;

import net.minecraft.client.Minecraft;

public class FastPlaceModule extends Module {

    public FastPlaceModule() {
        super("FastPlace", "Combat");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        // The field is named rightClickDelayTimer inside Minecraft.java
        mc.rightClickDelayTimer = 0;
    }
}