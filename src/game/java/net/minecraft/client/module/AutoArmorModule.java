package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmorModule extends Module {
    private int delay = 0;

    public AutoArmorModule() { super("AutoArmor", "Combat"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (delay > 0) { delay--; return; }

        // Armor slots: 5=helmet, 6=chestplate, 7=leggings, 8=boots
        for (int i = 5; i <= 8; i++) {
            ItemStack current = mc.thePlayer.inventory.armorInventory[8 - i];
            if (current != null) continue; // slot already occupied

            // Search inventory for best armor for this slot
            int bestSlot = -1;
            int bestPriority = -1;
            for (int j = 9; j < 45; j++) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(j).getStack();
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) stack.getItem();
                    if (armor.armorType == i - 5) { // 0=helmet,1=chest,2=leggings,3=boots
                        int priority = getArmorPriority(armor);
                        if (priority > bestPriority) {
                            bestPriority = priority;
                            bestSlot = j;
                        }
                    }
                }
            }

            if (bestSlot != -1) {
                mc.playerController.windowClick(mc.thePlayer.openContainer.windowId,
                        bestSlot, 0, 1, mc.thePlayer);
                delay = 5;
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