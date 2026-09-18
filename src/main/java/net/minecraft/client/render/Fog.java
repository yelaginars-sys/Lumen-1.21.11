package net.minecraft.client.render;

public class Fog {
    public static final Fog DUMMY = new Fog(0, 0, null, 0, 0, 0, 1.0f);

    public Fog(float start, float end, Object shape, float red, float green, float blue, float alpha) {}
    public Fog() {}

    public Object shape() { return null; }
    public float red() { return 0.0f; }
    public float green() { return 0.0f; }
    public float blue() { return 0.0f; }
    public float alpha() { return 1.0f; }
}