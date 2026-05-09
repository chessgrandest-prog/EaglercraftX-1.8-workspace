package net.minecraft.client.module;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class NukerModule extends Module {

    public NukerModule() { super("Nuker", "Misc"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || mc.playerController == null) return;

        BlockPos center = mc.playerController.getCurrentBreakingBlockPos();
        if (center == null && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            center = mc.objectMouseOver.getBlockPos();
        }
        if (center == null) return;

        int y = center.getY();
        for (int ox = -1; ox <= 1; ox++) {
            for (int oz = -1; oz <= 1; oz++) {
                BlockPos pos = new BlockPos(center.getX() + ox, y, center.getZ() + oz);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block.getMaterial() != Material.air && block.getBlockHardness(mc.theWorld, pos) >= 0) {
                    mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                }
            }
        }
    }
}
