package net.minecraft.client.module;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class ParkourModule extends Module {

    private int cooldown = 0;

    public ParkourModule() {
        super("Parkour", "Movement");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (!mc.thePlayer.onGround) return;
        if (mc.thePlayer.movementInput.moveForward < 0.78F) return;

        float yaw = mc.thePlayer.rotationYaw * 0.017453292F;
        double fx = (double) (-MathHelper.sin(yaw));
        double fz = (double) MathHelper.cos(yaw);

        int px = MathHelper.floor_double(mc.thePlayer.posX);
        int py = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY);
        int pz = MathHelper.floor_double(mc.thePlayer.posZ);

        int ax = px + (Math.abs(fx) > Math.abs(fz) ? (fx > 0 ? 1 : -1) : 0);
        int az = pz + (Math.abs(fz) >= Math.abs(fx) ? (fz > 0 ? 1 : -1) : 0);

        BlockPos aheadFoot = new BlockPos(ax, py, az);
        BlockPos aheadBelow = aheadFoot.down();
        BlockPos underPlayer = new BlockPos(px, py - 1, pz);

        boolean gapAhead = mc.theWorld.getBlockState(aheadFoot).getBlock().getMaterial() == Material.air
                || !mc.theWorld.getBlockState(aheadFoot).getBlock().getMaterial().blocksMovement();
        boolean supportAhead = mc.theWorld.getBlockState(aheadBelow).getBlock().getMaterial().blocksMovement();
        boolean standingOnSolid = mc.theWorld.getBlockState(underPlayer).getBlock().getMaterial().blocksMovement();

        if (standingOnSolid && gapAhead && supportAhead) {
            mc.thePlayer.jump();
            cooldown = 8;
        }
    }
}
