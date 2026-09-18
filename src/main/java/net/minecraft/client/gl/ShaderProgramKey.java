package net.minecraft.client.gl;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Identifier;

public class ShaderProgramKey {
    private final Identifier id;
    private final VertexFormat format;

    public ShaderProgramKey(Identifier id, VertexFormat format) {
        this.id = id;
        this.format = format;
    }

    public Identifier getId() {
        return id;
    }

    public VertexFormat getFormat() {
        return format;
    }
}