package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class FreecamModule extends Module {
    private double savedX, savedY, savedZ;
    private float savedYaw, savedPitch;

    private double camX, camY, camZ;
    private double prevCamX, prevCamY, prevCamZ;

    private static final double SPEED = 0.55D;

    public FreecamModule() { super("Freecam", "Render"); }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            savedX = mc.thePlayer.posX;
            savedY = mc.thePlayer.posY;
            savedZ = mc.thePlayer.posZ;
            savedYaw = mc.thePlayer.rotationYaw;
            savedPitch = mc.thePlayer.rotationPitch;
            camX = prevCamX = savedX;
            camY = prevCamY = savedY;
            camZ = prevCamZ = savedZ;
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = false;
            mc.thePlayer.setPositionAndRotation(savedX, savedY, savedZ, savedYaw, savedPitch);
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
        }
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        mc.thePlayer.noClip = true;
        mc.thePlayer.setPosition(savedX, savedY, savedZ);
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
        mc.thePlayer.fallDistance = 0;

        prevCamX = camX;
        prevCamY = camY;
        prevCamZ = camZ;

        Vec3 look = mc.thePlayer.getLook(1.0F);
        double lx = look.xCoord;
        double ly = look.yCoord;
        double lz = look.zCoord;

        float yawRad = mc.thePlayer.rotationYaw * 0.017453292F;
        double strafeX = MathHelper.cos(yawRad);
        double strafeZ = MathHelper.sin(yawRad);

        double moveX = 0;
        double moveY = 0;
        double moveZ = 0;

        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            moveX += lx;
            moveY += ly;
            moveZ += lz;
        }
        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            moveX -= lx;
            moveY -= ly;
            moveZ -= lz;
        }
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            moveX += strafeZ;
            moveZ -= strafeX;
        }
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            moveX -= strafeZ;
            moveZ += strafeX;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            moveY += 1.0D;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            moveY -= 1.0D;
        }

        double len = Math.sqrt(moveX * moveX + moveY * moveY + moveZ * moveZ);
        if (len > 1.0E-4D) {
            moveX = moveX / len * SPEED;
            moveY = moveY / len * SPEED;
            moveZ = moveZ / len * SPEED;
            camX += moveX;
            camY += moveY;
            camZ += moveZ;
        }
    }

    public double getInterpCamX(float partialTicks) {
        return prevCamX + (camX - prevCamX) * (double) partialTicks;
    }

    public double getInterpCamY(float partialTicks) {
        return prevCamY + (camY - prevCamY) * (double) partialTicks;
    }

    public double getInterpCamZ(float partialTicks) {
        return prevCamZ + (camZ - prevCamZ) * (double) partialTicks;
    }
}
