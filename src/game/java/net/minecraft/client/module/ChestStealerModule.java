package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ChestStealerModule extends Module {

    private int delay = 0;

    public ChestStealerModule() {
        super("ChestStealer", "Misc");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null || !(mc.currentScreen instanceof GuiContainer)) return;
        if (mc.thePlayer == null) return;

        Container container = mc.thePlayer.openContainer;
        if (container == null) return;

        // Small delay between taking items
        if (delay > 0) {
            delay--;
            return;
        }

        for (int i = 0; i < container.inventorySlots.size(); ++i) {
            Slot slot = container.getSlot(i);
            // Take items only from the chest, not from your own inventory
            if (slot.getHasStack() && slot.inventory != mc.thePlayer.inventory) {
                // Shift-click moves the stack quickly
                mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, 1, mc.thePlayer);
                delay = 1;  // steal every 4 ticks (about 0.2s)
                return;
            }
        }
    }
}