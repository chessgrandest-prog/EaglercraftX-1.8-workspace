package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class KillAuraModule extends Module {

    private static final double RANGE = 4.5;
    private static final int ATTACK_COOLDOWN = 1;
    private int cooldown = 0;

    public KillAuraModule() {
        super("KillAura", "Combat");
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

        Entity target = findNearestTarget(mc);
        if (target == null) return;

        faceEntity(mc, target);

        mc.playerController.attackEntity(mc.thePlayer, target);
        mc.thePlayer.swingItem();

        cooldown = ATTACK_COOLDOWN;
    }

    private Entity findNearestTarget(Minecraft mc) {
        Entity nearest = null;
        double nearestDist = RANGE;

        for (Object obj : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) obj;
            if (entity == mc.thePlayer) continue;
            if (!(entity instanceof EntityLivingBase)) continue;
            if (!entity.isEntityAlive()) continue;

            double dist = mc.thePlayer.getDistanceToEntity(entity);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    private void faceEntity(Minecraft mc, Entity target) {
        double dx = target.posX - mc.thePlayer.posX;
        double dy = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = target.posZ - mc.thePlayer.posZ;
        double horizontalDist = MathHelper.sqrt_double(dx * dx + dz * dz);

        float yaw = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - 90F;
        float pitch = (float) -(Math.atan2(dy, horizontalDist) * (180D / Math.PI));

        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;
    }
}