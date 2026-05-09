package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;

import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;

public class GuiCustomMainMenu extends GuiScreen {
    private static final ResourceLocation customBackground = new ResourceLocation("textures/gui/title/neonskull_background.png");
    private float updateCounter;

    public GuiCustomMainMenu() {
        this.updateCounter = 0;
    }

    @Override
    public void initGui() {
        int i = this.height / 4 + 48;
        this.buttonList.clear();
        
        // Modern vertical buttons
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, i, 200, 20, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, i + 24, 200, 20, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, i + 48, 200, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, i + 72, 200, 20, I18n.format("menu.editProfile")));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, i + 96, 200, 20, "Credits"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        if (button.id == 1) this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(this));
        if (button.id == 2) this.mc.displayGuiScreen(new GuiMultiplayer(this));
        if (button.id == 4) this.mc.displayGuiScreen(new net.lax1dude.eaglercraft.v1_8.profile.GuiScreenEditProfile(this));
        if (button.id == 5) {
             String resStr = net.lax1dude.eaglercraft.v1_8.EagRuntime.getResourceString("/assets/eagler/CREDITS.txt");
             if (resStr != null) {
                 net.lax1dude.eaglercraft.v1_8.EagRuntime.openCreditsPopup(resStr);
             }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updateCounter += partialTicks;
        
        // Draw background
        this.mc.getTextureManager().bindTexture(customBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Subtle drift animation
        float driftX = MathHelper.sin(updateCounter * 0.01f) * 10.0f;
        float driftY = MathHelper.cos(updateCounter * 0.01f) * 10.0f;
        
        drawModalRectWithCustomSizedTexture(-20 + (int)driftX, -20 + (int)driftY, 0, 0, this.width + 40, this.height + 40, this.width + 40, this.height + 40);

        // Overlay gradient
        this.drawGradientRect(0, 0, this.width, this.height, 0x00000000, 0xAA000000);
        
        // Title text with glow
        String title = "NEONSKULL";
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.width / 2, this.height / 4 - 30, 0);
        GlStateManager.scale(4.0F, 4.0F, 1.0F);
        
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float pulse = 0.5f + 0.5f * MathHelper.sin(updateCounter * 0.05f);
        int glowAlpha = (int)(100 + 100 * pulse);
        int glowColor = (glowAlpha << 24) | 0xAA22FF; // Purple-ish glow
        
        this.drawString(this.fontRendererObj, title, -this.fontRendererObj.getStringWidth(title) / 2 + 1, -10 + 1, glowColor);
        this.drawString(this.fontRendererObj, title, -this.fontRendererObj.getStringWidth(title) / 2 - 1, -10 - 1, glowColor);
        this.drawString(this.fontRendererObj, title, -this.fontRendererObj.getStringWidth(title) / 2 + 1, -10 - 1, glowColor);
        this.drawString(this.fontRendererObj, title, -this.fontRendererObj.getStringWidth(title) / 2 - 1, -10 + 1, glowColor);
        
        this.drawCenteredString(this.fontRendererObj, title, 0, -10, 0xFFFFFF);
        GlStateManager.popMatrix();

        // Footer info
        String version = "v1.0.0 - EaglercraftX 1.8";
        this.drawString(this.fontRendererObj, version, 5, this.height - 12, 0xAAAAAA);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
