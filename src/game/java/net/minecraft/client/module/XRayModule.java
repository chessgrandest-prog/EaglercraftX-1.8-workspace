package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class XRayModule extends Module {
    public XRayModule() {
        super("X-Ray", "Render");
    }

    @Override
    public void onEnable() {
        if (mc.renderGlobal != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    public void onDisable() {
        if (mc.renderGlobal != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    public static boolean isXRayBlock(Block block) {
        return block == Blocks.coal_ore || block == Blocks.iron_ore || block == Blocks.gold_ore ||
               block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore ||
               block == Blocks.diamond_ore || block == Blocks.emerald_ore ||
               block == Blocks.lapis_ore || block == Blocks.quartz_ore ||
               block == Blocks.mob_spawner || block == Blocks.chest || block == Blocks.trapped_chest ||
               block == Blocks.ender_chest || block == Blocks.tnt || block == Blocks.portal ||
               block == Blocks.end_portal || block == Blocks.end_portal_frame;
    }
}
