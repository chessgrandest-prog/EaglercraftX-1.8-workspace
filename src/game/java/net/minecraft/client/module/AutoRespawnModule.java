package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;

public class AutoRespawnModule extends Module {

    public AutoRespawnModule() {
        super("AutoRespawn", "Misc");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        // If the player is dead and the death screen is showing, immediately respawn
        if (mc.thePlayer.getHealth() <= 0.0F && mc.currentScreen instanceof GuiGameOver) {
            mc.thePlayer.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }
}