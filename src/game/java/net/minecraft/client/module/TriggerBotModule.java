package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;

public class TriggerBotModule extends Module {
    public TriggerBotModule() { super("TriggerBot", "Combat"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.objectMouseOver == null) return;
        if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return;
        Entity entity = mc.objectMouseOver.entityHit;
        if (!(entity instanceof EntityLivingBase)) return;
        if (!entity.isEntityAlive()) return;
        mc.playerController.attackEntity(mc.thePlayer, entity);
        mc.thePlayer.swingItem();
    }
}