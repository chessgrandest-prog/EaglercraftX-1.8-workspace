package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.module.Module;
import net.minecraft.client.module.ModuleManager;

public class GuiClientSettings extends GuiScreen {

    private final GuiScreen parentGui;

    // Neon palette
    private static final int NEON_GREEN  = 0xFF39FF14;
    private static final int NEON_PURPLE = 0xFFBC13FE;
    private static final int DARK_BG     = 0xD5030303;
    private static final int CARD_BG     = 0xF00C0C0C;
    private static final int GLOW_GREEN  = 0x8039FF14;

    private static final String[] CATEGORY_ORDER = { "Combat", "Movement", "Render", "Misc" };

    // Persistent panel positions (survives menu close/reopen)
    private static final Map<String, float[]> savedPanelPositions = new HashMap<>();
    private static final Map<String, Boolean> savedPanelCollapsed = new HashMap<>();

    private static class CategoryPanel {
        String category;
        List<Module> modules = new ArrayList<>();
        float x, y;
        int width = 160;            // narrower panels
        int headerHeight = 16;
        boolean collapsed = false;
    }

    private final List<CategoryPanel> panels = new ArrayList<>();

    // Dragging
    private int draggingIndex = -1;
    private float dragOffsetX, dragOffsetY;

    // Floating particles
    private static class Particle {
        float x, y, vx, vy;
        int color;
    }
    private final List<Particle> particles = new ArrayList<>();
    private static final int PARTICLE_COUNT = 40;

    // Keybind assignment state
    private Module keybindModule = null;   // currently waiting for key

    public GuiClientSettings(GuiScreen parent) {
        this.parentGui = parent;
    }

    @Override
    public void initGui() {
        panels.clear();

        Map<String, List<Module>> grouped = new LinkedHashMap<>();
        for (String cat : CATEGORY_ORDER) grouped.put(cat, new ArrayList<>());
        for (Module m : ModuleManager.getModules()) {
            String cat = m.getCategory();
            if (!grouped.containsKey(cat)) grouped.put(cat, new ArrayList<>());
            grouped.get(cat).add(m);
        }

        float startX = 20, startY = 20, gapY = 6;
        for (String cat : CATEGORY_ORDER) {
            List<Module> mods = grouped.get(cat);
            if (mods == null || mods.isEmpty()) continue;
            CategoryPanel panel = new CategoryPanel();
            panel.category = cat;
            panel.modules = mods;

            // Restore saved position if available, otherwise use default layout
            if (savedPanelPositions.containsKey(cat)) {
                float[] pos = savedPanelPositions.get(cat);
                panel.x = pos[0];
                panel.y = pos[1];
            } else {
                panel.x = startX;
                panel.y = startY;
            }

            // Restore collapsed state
            if (savedPanelCollapsed.containsKey(cat)) {
                panel.collapsed = savedPanelCollapsed.get(cat);
            }

            panels.add(panel);

            // Only advance default startY for panels that don't have saved positions
            if (!savedPanelPositions.containsKey(cat)) {
                int rows = panel.collapsed ? 0 : mods.size();
                startY += panel.headerHeight + rows * 14 + 6 + gapY;
            }
        }

        if (particles.isEmpty()) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                Particle p = new Particle();
                p.x = (float)(Math.random() * this.width);
                p.y = (float)(Math.random() * this.height);
                p.vx = (float)(Math.random() * 0.8 - 0.4);
                p.vy = (float)(Math.random() * 0.8 - 0.4);
                p.color = Math.random() < 0.5 ? NEON_GREEN : NEON_PURPLE;
                particles.add(p);
            }
        }
    }

    @Override
    public void updateScreen() {
        for (Particle p : particles) {
            p.x += p.vx;
            p.y += p.vy;
            if (p.x < 0) p.x += this.width;
            if (p.x >= this.width) p.x -= this.width;
            if (p.y < 0) p.y += this.height;
            if (p.y >= this.height) p.y -= this.height;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, this.width, this.height, DARK_BG);

        // Particles & Plexus effect
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);
            int alpha = 0x60;
            int glow = (alpha << 24) | (p1.color & 0x00FFFFFF);
            drawRect((int)(p1.x - 1), (int)(p1.y - 1), (int)(p1.x + 2), (int)(p1.y + 2), glow);
            drawRect((int)p1.x, (int)p1.y, (int)p1.x + 1, (int)p1.y + 1, p1.color);

            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);
                float distSq = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
                if (distSq < 2500) { // 50 pixels distance
                    float dist = (float)Math.sqrt(distSq);
                    float lineAlpha = 1.0f - (dist / 50.0f);
                    int color = (int)(lineAlpha * 0x44) << 24 | (0xFFFFFF & p1.color);
                    // Draw a thin line (approx with rect)
                    drawRect((int)p1.x, (int)p1.y, (int)p1.x + 1, (int)p1.y + 1, color);
                }
            }
        }

        // Panels
        for (CategoryPanel panel : panels) {
            drawPanel(panel, mouseX, mouseY);
        }

        // If rebinding a key, show a hint
        if (keybindModule != null) {
            String msg = "Press a key for " + keybindModule.getName() + " (ESC to cancel)";
            drawRect(0, this.height - 30, this.width, this.height, 0xCC000000);
            this.drawCenteredString(this.fontRendererObj, msg, this.width / 2, this.height - 22, NEON_GREEN);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawPanel(CategoryPanel panel, int mouseX, int mouseY) {
        int px = (int)panel.x, py = (int)panel.y, w = panel.width, hh = panel.headerHeight;
        int contentRows = panel.collapsed ? 0 : panel.modules.size();
        int totalHeight = hh + contentRows * 14 + 6;

        // Glow border
        this.drawGradientRect(px - 2, py - 2, px + w + 2, py + totalHeight + 2, NEON_PURPLE, NEON_GREEN);
        // Card
        drawRect(px, py, px + w, py + totalHeight, CARD_BG);

        // Header
        boolean headerHovered = mouseX >= px && mouseX <= px + w && mouseY >= py && mouseY <= py + hh;
        int headerColor = headerHovered ? NEON_PURPLE : 0xAA000000 | (NEON_PURPLE & 0x00FFFFFF);
        this.drawGradientRect(px, py, px + w, py + hh, headerColor, 0xFF000000);

        String arrow = panel.collapsed ? ">" : "v";
        this.drawString(this.fontRendererObj, arrow, px + 5, py + 4, 0xFFFFFFFF);
        this.drawString(this.fontRendererObj, panel.category.toUpperCase(), px + 18, py + 4, 0xFFFFFFFF);

        // Modules (if expanded)
        if (!panel.collapsed) {
            int rowY = py + hh + 3;
            for (Module m : panel.modules) {
                boolean enabled = m.isEnabled();
                boolean rebinding = (keybindModule == m);
                boolean hovered = mouseX >= px && mouseX <= px + w && mouseY >= rowY && mouseY <= rowY + 14;

                if (rebinding) {
                    drawRect(px + 2, rowY, px + w - 2, rowY + 14, NEON_PURPLE);
                } else if (hovered) {
                    drawRect(px + 2, rowY, px + w - 2, rowY + 14, 0x44FFFFFF);
                }

                // Status dot
                int dotColor = enabled ? NEON_GREEN : 0xFF555555;
                drawRect(px + 6, rowY + 5, px + 10, rowY + 9, dotColor);

                // Name + keybind indicator
                String keyName = m.getKeyName();
                String display = m.getName();
                if (keyName != null) display += " [" + keyName + "]";
                this.drawString(this.fontRendererObj, display, px + 14, rowY + 4, 0xFFFFFFFF);

                rowY += 14;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (int i = panels.size() - 1; i >= 0; i--) {
                CategoryPanel panel = panels.get(i);
                int px = (int)panel.x, py = (int)panel.y, w = panel.width, hh = panel.headerHeight;

                // Header (drag)
                if (mouseX >= px && mouseX <= px + w && mouseY >= py && mouseY <= py + hh) {
                    draggingIndex = i;
                    dragOffsetX = mouseX - panel.x;
                    dragOffsetY = mouseY - panel.y;
                    return;
                }

                // Module rows
                if (!panel.collapsed) {
                    int rowY = py + hh + 3;
                    for (Module m : panel.modules) {
                        if (mouseX >= px && mouseX <= px + w && mouseY >= rowY && mouseY <= rowY + 14) {
                            m.toggle();
                            return;
                        }
                        rowY += 14;
                    }
                }
            }
        } else if (mouseButton == 1) {
            // Right click: collapse/expand on header, or start keybind assignment on module row
            for (int i = panels.size() - 1; i >= 0; i--) {
                CategoryPanel panel = panels.get(i);
                int px = (int)panel.x, py = (int)panel.y, w = panel.width, hh = panel.headerHeight;

                // Header right-click = collapse/expand
                if (mouseX >= px && mouseX <= px + w && mouseY >= py && mouseY <= py + hh) {
                    panel.collapsed = !panel.collapsed;
                    savedPanelCollapsed.put(panel.category, panel.collapsed);
                    return;
                }

                // Module right-click = start keybind
                if (!panel.collapsed) {
                    int rowY = py + hh + 3;
                    for (Module m : panel.modules) {
                        if (mouseX >= px && mouseX <= px + w && mouseY >= rowY && mouseY <= rowY + 14) {
                            keybindModule = (keybindModule == m) ? null : m;
                            return;
                        }
                        rowY += 14;
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (draggingIndex >= 0) {
            // Save position on drag release
            CategoryPanel panel = panels.get(draggingIndex);
            savedPanelPositions.put(panel.category, new float[]{ panel.x, panel.y });
        }
        draggingIndex = -1;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (draggingIndex >= 0 && clickedMouseButton == 0) {
            CategoryPanel panel = panels.get(draggingIndex);
            panel.x = mouseX - dragOffsetX;
            panel.y = mouseY - dragOffsetY;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keybindModule != null) {
            if (keyCode == 1) { // ESC
                keybindModule = null;
            } else {
                keybindModule.setKeyCode(keyCode);
                keybindModule = null;
            }
        } else if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindClose.getKeyCode()) {
            // ESC or inventory key closes the GUI
            this.mc.displayGuiScreen(parentGui);
        } else if (keyCode == 54) { // Right Shift toggles GUI itself
            this.mc.displayGuiScreen(parentGui);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}