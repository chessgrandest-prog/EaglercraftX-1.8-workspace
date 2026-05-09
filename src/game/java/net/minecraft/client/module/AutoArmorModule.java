package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmorModule extends Module {
    private int delay = 0;
    /** Armor slot (5–8) to click next tick after picking up better gear from inventory. */
    private int pendingArmorSlotClick = -1;

    public AutoArmorModule() { super("AutoArmor", "Combat"); }

    @Override
    public void onDisable() {
        pendingArmorSlotClick = -1;
        delay = 0;
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (mc.thePlayer.openContainer != mc.thePlayer.inventoryContainer) return;
        if (delay > 0) { delay--; return; }

        if (pendingArmorSlotClick >= 0) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                    pendingArmorSlotClick, 0, 0, mc.thePlayer);
            pendingArmorSlotClick = -1;
            delay = 4;
            return;
        }

        for (int armorType = 0; armorType < 4; armorType++) {
            int containerSlot = 5 + armorType;
            ItemStack current = mc.thePlayer.inventoryContainer.getSlot(containerSlot).getStack();
            int currentPriority = -1;
            if (current != null && current.getItem() instanceof ItemArmor) {
                currentPriority = getArmorPriority((ItemArmor) current.getItem());
            }

            int bestSlot = -1;
            int bestPriority = currentPriority;
            for (int j = 9; j < 45; j++) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(j).getStack();
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    if (armor.armorType == armorType) {
                        int priority = getArmorPriority(armor);
                        if (priority > bestPriority) {
                            bestPriority = priority;
                            bestSlot = j;
                        }
                    }
                }
            }

            if (bestSlot != -1) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                        bestSlot, 0, 0, mc.thePlayer);
                pendingArmorSlotClick = containerSlot;
                delay = 3;
                return;
            }
        }
    }

    private int getArmorPriority(ItemArmor armor) {
        switch (armor.getArmorMaterial()) {
            case DIAMOND: return 4;
            case IRON: return 3;
            case CHAIN: return 2;
            case GOLD: return 1;
            case LEATHER: return 0;
            default: return -1;
        }
    }
}
