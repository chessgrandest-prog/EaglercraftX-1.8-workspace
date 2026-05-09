package net.minecraft.client.module;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/** Wall phase: brief noClip while inside solid blocks or pushing into walls. */
public class PhaseModule extends Module {

    public PhaseModule() { super("Phase", "Movement"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        BlockPos inside = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 0.2D, mc.thePlayer.posZ);
        BlockPos head = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ);
        boolean inSolid = mc.theWorld.getBlockState(inside).getBlock().getMaterial() != Material.air
                || mc.theWorld.getBlockState(head).getBlock().getMaterial() != Material.air;

        boolean pushingWall = mc.thePlayer.isCollidedHorizontally
                && (Math.abs(mc.thePlayer.motionX) > 0.02D || Math.abs(mc.thePlayer.motionZ) > 0.02D);

        if (inSolid || pushingWall) {
            mc.thePlayer.noClip = true;
            mc.thePlayer.fallDistance = 0;
        } else {
            mc.thePlayer.noClip = false;
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = false;
        }
    }
}
