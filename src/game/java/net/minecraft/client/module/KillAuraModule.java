package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KillAuraModule extends Module {

    private static final double RANGE = 4.5;
    private static final int TICKS_BETWEEN_BURSTS = 2;
    private int cooldown = 0;

    public KillAuraModule() {
        super("KillAura", "Combat");
    }

    @Override
    public void onUpdate() {
        if (!isEnabled())
            return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        List<EntityLivingBase> targets = findTargetsInRange(mc);
        if (targets.isEmpty())
            return;

        for (EntityLivingBase target : targets) {
            mc.playerController.attackEntity(mc.thePlayer, target);
            mc.thePlayer.swingItem();
        }
        cooldown = TICKS_BETWEEN_BURSTS;
    }

    private List<EntityLivingBase> findTargetsInRange(Minecraft mc) {
        List<EntityLivingBase> list = new ArrayList<>();
        for (Object obj : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) obj;
            if (entity == mc.thePlayer)
                continue;
            if (!(entity instanceof EntityLivingBase))
                continue;
            if (!entity.isEntityAlive())
                continue;

            double dist = mc.thePlayer.getDistanceToEntity(entity);
            if (dist < RANGE) {
                list.add((EntityLivingBase) entity);
            }
        }
        Collections.sort(list, new Comparator<EntityLivingBase>() {
            @Override
            public int compare(EntityLivingBase a, EntityLivingBase b) {
                return Double.compare(mc.thePlayer.getDistanceToEntity(a), mc.thePlayer.getDistanceToEntity(b));
            }
        });
        return list;
    }
}
