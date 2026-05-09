package net.minecraft.client.module;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;

public class JesusModule extends Module {
    public JesusModule() { super("Jesus", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.isSneaking()) return;

        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
            mc.thePlayer.motionY = 0.11;
        } else if (isOverLiquid(mc)) {
            mc.thePlayer.motionY = 0.0;
            mc.thePlayer.onGround = true;
        }
    }

    private boolean isOverLiquid(Minecraft mc) {
        AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -0.01, 0);
        // Check if there's liquid below
        boolean hasLiquid = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()
                && mc.theWorld.handleMaterialAcceleration(bb, Material.water, mc.thePlayer)
                || mc.theWorld.handleMaterialAcceleration(bb, Material.lava, mc.thePlayer);
        return hasLiquid;
    }
}
