package net.minecraft.client.gui;

import java.util.ArrayList;
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
            panel.x = startX;
            panel.y = startY;
            panels.add(panel);
            int rows = panel.collapsed ? 0 : mods.size();
            startY += panel.headerHeight + rows * 14 + 6 + gapY;
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

        // Particles
        for (Particle p : particles) {
            int alpha = 0x60;
            int glow = (alpha << 24) | (p.color & 0x00FFFFFF);
            drawRect((int)(p.x - 1), (int)(p.y - 1), (int)(p.x + 2), (int)(p.y + 2), glow);
            drawRect((int)p.x, (int)p.y, (int)p.x + 1, (int)p.y + 1, p.color);
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
        drawRect(px - 2, py - 2, px + w + 2, py + totalHeight + 2, GLOW_GREEN);
        // Card
        drawRect(px, py, px + w, py + totalHeight, CARD_BG);

        // Header
        boolean headerHovered = mouseX >= px && mouseX <= px + w && mouseY >= py && mouseY <= py + hh;
        int headerColor = headerHovered ? NEON_PURPLE : (NEON_PURPLE & 0x00FFFFFF) | 0xAA000000;
        drawRect(px, py, px + w, py + hh, headerColor);

        String arrow = panel.collapsed ? "▶" : "▼";
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
        // If currently waiting for a key, cancel on any click outside? Let's handle clicks normally but stop rebinding only when ESC is pressed.
        if (mouseButton == 0) {
            for (int i = panels.size() - 1; i >= 0; i--) {
                CategoryPanel panel = panels.get(i);
                int px = (int)panel.x, py = (int)panel.y, w = panel.width, hh = panel.headerHeight;

                // Header (drag)
                if (mouseX >= px && mouseX <= px + w && mouseY >= py && mouseY <= py + hh) {
                    // If we were rebinding, cancel on header click? No, let's keep it simple: only ESC cancels rebinding.
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
                            if (!m.isEnabled()) {
                                m.toggle(); // left click toggles only if currently off? Or always toggle? Let's toggle.
                            } else {
                                // shift-click or something? simple: left click toggles
                                m.toggle();
                            }
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
                    return;
                }

                // Module right-click = start keybind
                if (!panel.collapsed) {
                    int rowY = py + hh + 3;
                    for (Module m : panel.modules) {
                        if (mouseX >= px && mouseX <= px + w && mouseY >= rowY && mouseY <= rowY + 14) {
                            // Start keybind assignment for this module
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
        } else if (keyCode == 54) { // Right Shift toggles GUI itself (already handled before opening, but just in case)
            this.mc.displayGuiScreen(parentGui);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}