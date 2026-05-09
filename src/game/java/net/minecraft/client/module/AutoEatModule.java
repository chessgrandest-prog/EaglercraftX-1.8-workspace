package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class AutoEatModule extends Module {
    public AutoEatModule() { super("AutoEat", "Misc"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (mc.currentScreen != null) return;

        if (mc.thePlayer.getFoodStats().getFoodLevel() <= 14) {
            int foodSlot = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                if (stack != null && stack.getItem() instanceof ItemFood) {
                    foodSlot = i;
                    break;
                }
            }
            if (foodSlot != -1) {
                ItemStack stack = mc.thePlayer.inventory.mainInventory[foodSlot];
                mc.thePlayer.inventory.currentItem = foodSlot;
                if (stack != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack)) {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        }
    }
}
