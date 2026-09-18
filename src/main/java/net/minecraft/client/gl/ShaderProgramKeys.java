package net.minecraft.client.gl;

import net.minecraft.util.Identifier;

public class ShaderProgramKeys {
    public static final ShaderProgramKey POSITION = new ShaderProgramKey(Identifier.ofVanilla("position"), null);
    public static final ShaderProgramKey POSITION_COLOR = new ShaderProgramKey(Identifier.ofVanilla("position_color"), null);
    public static final ShaderProgramKey POSITION_TEX_COLOR = new ShaderProgramKey(Identifier.ofVanilla("position_tex_color"), null);
    public static final ShaderProgramKey POSITION_TEXTURE = new ShaderProgramKey(Identifier.ofVanilla("position_texture"), null);
}