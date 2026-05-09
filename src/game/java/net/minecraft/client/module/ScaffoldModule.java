package net.minecraft.client.module;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ScaffoldModule extends Module {
    public ScaffoldModule() { super("Scaffold", "Misc"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        int slot = findBlockSlot(mc);
        if (slot == -1) return;

        int oldSlot = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.inventory.currentItem = slot;

        double mx = mc.thePlayer.motionX;
        double mz = mc.thePlayer.motionZ;
        float yaw = mc.thePlayer.rotationYaw * 0.017453292F;
        float forward = mc.thePlayer.movementInput.moveForward;
        float strafe = mc.thePlayer.movementInput.moveStrafe;
        float f = MathHelper.sin(yaw);
        float f1 = MathHelper.cos(yaw);

        double forwardX = (double)(-strafe * f + forward * f1);
        double forwardZ = (double)(forward * f + strafe * f1);
        double len = MathHelper.sqrt_double(forwardX * forwardX + forwardZ * forwardZ);
        if (len > 1.0E-4D) {
            forwardX /= len;
            forwardZ /= len;
        } else {
            forwardX = -MathHelper.sin(yaw);
            forwardZ = MathHelper.cos(yaw);
        }

        double px = mc.thePlayer.posX;
        double pz = mc.thePlayer.posZ;
        placeIfAirBelow(mc, px, pz);
        placeIfAirBelow(mc, px + forwardX * 0.35D, pz + forwardZ * 0.35D);
        placeIfAirBelow(mc, px + mx * 2.0D, pz + mz * 2.0D);

        mc.thePlayer.inventory.currentItem = oldSlot;
    }

    private void placeIfAirBelow(Minecraft mc, double x, double z) {
        BlockPos below = new BlockPos(Math.floor(x), Math.floor(mc.thePlayer.posY) - 1, Math.floor(z));
        if (mc.theWorld.getBlockState(below).getBlock().getMaterial() != Material.air) {
            return;
        }
        BlockPos placeOn = below.down();
        if (mc.theWorld.getBlockState(placeOn).getBlock().getMaterial() == Material.air) {
            return;
        }
        Vec3 hit = new Vec3((double) below.getX() + 0.5D, (double) below.getY(), (double) below.getZ() + 0.5D);
        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                mc.thePlayer.inventory.getCurrentItem(), placeOn, EnumFacing.UP, hit);
        mc.thePlayer.swingItem();
    }

    private int findBlockSlot(Minecraft mc) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return -1;
    }
}
