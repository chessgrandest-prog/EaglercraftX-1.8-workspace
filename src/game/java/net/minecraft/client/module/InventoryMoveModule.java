package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

public class InventoryMoveModule extends Module {
    public InventoryMoveModule() { super("InventoryMove", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) return;
        if (mc.thePlayer == null) return;

        // Simulate movement keys while in inventory
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);

        mc.thePlayer.movementInput.updatePlayerMoveState();
    }
}