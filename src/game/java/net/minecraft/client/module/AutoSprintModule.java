package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class AutoSprintModule extends Module {
    public AutoSprintModule() {
        super("AutoSprint", "Movement");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;
        // Force sprint whenever moving forward
        if (player.movementInput.moveForward > 0.0F && !player.isSprinting()) {
            player.setSprinting(true);
        } else if (player.movementInput.moveForward == 0.0F) {
            player.setSprinting(false);
        }
    }
}