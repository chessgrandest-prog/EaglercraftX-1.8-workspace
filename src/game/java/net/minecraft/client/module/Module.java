package net.minecraft.client.module;

public class Module {
    private final String name;
    private final String category;
    private boolean enabled;
    private int keyCode = 0;   // 0 = unbound

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void toggle() { enabled = !enabled; }
    public void setEnabled(boolean e) { enabled = e; }

    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int code) { this.keyCode = code; }
    public String getKeyName() {
        if (keyCode == 0) return null;
        return net.minecraft.client.settings.GameSettings.getKeyDisplayString(keyCode);
    }

    public void onUpdate() {}
    public void onRender(float partialTicks) {}
}