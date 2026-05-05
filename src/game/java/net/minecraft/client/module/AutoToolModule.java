package net.minecraft.client.module;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;

public class AutoToolModule extends Module {

    private int delay = 0;

    public AutoToolModule() {
        super("AutoTool", "Combat");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK)
            return;
        if (mc.thePlayer == null || mc.thePlayer.capabilities.isCreativeMode) return;
        if (mc.playerController == null) return;

        // Only change tool if we're actually breaking a block (left click held)
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) return;

        if (delay > 0) {
            delay--;
            return;
        }

        BlockPos pos = mc.objectMouseOver.getBlockPos();
        Block block = mc.theWorld.getBlockState(pos).getBlock();

        // Find the best tool in the hotbar for this block
        int bestSlot = -1;
        float bestSpeed = 1.0F;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) continue;
            float speed = stack.getStrVsBlock(block);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1 && bestSlot != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            delay = 5;  // wait a few ticks to avoid switching constantly
        }
    }
}