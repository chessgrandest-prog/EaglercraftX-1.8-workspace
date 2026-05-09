package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AutoFishModule extends Module {
    private int reelCooldown = 0;

    public AutoFishModule() {
        super("AutoFish", "Misc");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null) return;

        if (reelCooldown > 0) {
            reelCooldown--;
        }

        ItemStack held = mc.thePlayer.getHeldItem();
        boolean rod = held != null && held.getItem() == Items.fishing_rod;

        if (!rod) return;

        if (mc.thePlayer.fishEntity == null) {
            if (reelCooldown == 0) {
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, held);
                reelCooldown = 15;
            }
            return;
        }

        EntityFishHook hook = mc.thePlayer.fishEntity;
        if (hook.shake > 0 || hook.isCatchablePhase()) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, held);
            reelCooldown = 10;
        }
    }
}
