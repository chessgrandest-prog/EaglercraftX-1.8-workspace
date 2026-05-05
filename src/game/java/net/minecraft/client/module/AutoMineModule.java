package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

public class AutoMineModule extends Module {
    public AutoMineModule() { super("AutoMine", "Combat"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.objectMouseOver == null) return;
        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        if (mc.gameSettings.keyBindAttack.isKeyDown()) return; // let player mine manually
        mc.playerController.onPlayerDamageBlock(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);
        mc.thePlayer.swingItem();
    }
}