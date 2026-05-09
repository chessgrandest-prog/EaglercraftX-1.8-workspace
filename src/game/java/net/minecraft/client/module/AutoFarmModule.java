package net.minecraft.client.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class AutoFarmModule extends Module {
    private static final int RADIUS = 4;
    private int tick = 0;

    public AutoFarmModule() { super("AutoFarm", "Misc"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        tick++;
        int px = (int) Math.floor(mc.thePlayer.posX);
        int py = (int) Math.floor(mc.thePlayer.posY);
        int pz = (int) Math.floor(mc.thePlayer.posZ);

        int breaksThisTick = 0;
        final int maxBreaks = 28;

        for (int x = px - RADIUS; x <= px + RADIUS && breaksThisTick < maxBreaks; x++) {
            for (int y = py - 2; y <= py + 2 && breaksThisTick < maxBreaks; y++) {
                for (int z = pz - RADIUS; z <= pz + RADIUS && breaksThisTick < maxBreaks; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = mc.theWorld.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block instanceof BlockCrops) {
                        int age = ((Integer) state.getValue(BlockCrops.AGE)).intValue();
                        if (age >= 7) {
                            mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                            breaksThisTick++;
                        }
                    }
                }
            }
        }

        if (tick % 2 != 0) return;

        int replants = 0;
        final int maxReplants = 10;
        for (int x = px - RADIUS; x <= px + RADIUS && replants < maxReplants; x++) {
            for (int y = py - 2; y <= py + 2 && replants < maxReplants; y++) {
                for (int z = pz - RADIUS; z <= pz + RADIUS && replants < maxReplants; z++) {
                    BlockPos cropAir = new BlockPos(x, y, z);
                    if (mc.theWorld.getBlockState(cropAir).getBlock() != Blocks.air) continue;
                    BlockPos farmlandPos = cropAir.down();
                    if (!(mc.theWorld.getBlockState(farmlandPos).getBlock() instanceof BlockFarmland)) continue;

                    Item seed = guessSeedFromNeighbors(mc, farmlandPos);
                    int slot = findSeedInHotbar(mc, seed);
                    if (slot < 0) continue;

                    int old = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = slot;
                    Vec3 hit = new Vec3((double) farmlandPos.getX() + 0.5D, (double) (farmlandPos.getY() + 1),
                            (double) farmlandPos.getZ() + 0.5D);
                    mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                            mc.thePlayer.inventory.getCurrentItem(), farmlandPos, EnumFacing.UP, hit);
                    mc.thePlayer.inventory.currentItem = old;
                    replants++;
                }
            }
        }
    }

    private Item guessSeedFromNeighbors(Minecraft mc, BlockPos farmlandPos) {
        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            BlockPos neighborCrop = farmlandPos.offset(f).up();
            Block b = mc.theWorld.getBlockState(neighborCrop).getBlock();
            if (b == Blocks.wheat) return Items.wheat_seeds;
            if (b == Blocks.carrots) return Items.carrot;
            if (b == Blocks.potatoes) return Items.potato;
        }
        return Items.wheat_seeds;
    }

    private int findSeedInHotbar(Minecraft mc, Item seed) {
        for (int i = 0; i < 9; i++) {
            ItemStack st = mc.thePlayer.inventory.mainInventory[i];
            if (st != null && st.getItem() == seed && st.stackSize > 0) {
                return i;
            }
        }
        return -1;
    }
}
