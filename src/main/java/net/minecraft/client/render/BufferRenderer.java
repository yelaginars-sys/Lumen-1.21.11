package net.minecraft.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Совместимый слой: в 1.21.11 BufferRenderer удалён, рисуем через RenderLayer.
// Текстуры не биндятся (старый RenderSystem.setShaderTexture удалён) —
// текстурированные вызовы идут через RenderLayers.entityCutoutNoCull только там,
// где текстура известна (шрифты), остальное рисуется плоским цветом.
public class BufferRenderer {
    private static volatile net.minecraft.util.Identifier fallbackTexture;
    private static final Map<net.minecraft.util.Identifier, RenderLayer> texturedLayers = new ConcurrentHashMap<>();

    /** Текстура для следующего текстурированного draw (выставляет Font/drawImage перед вызовом). */
    public static void bindFallbackTexture(net.minecraft.util.Identifier id) {
        fallbackTexture = id;
    }

    public static void drawWithGlobalProgram(BuiltBuffer buffer) {
        draw(buffer);
    }

    public static void drawWithShader(BuiltBuffer buffer) {
        draw(buffer);
    }

    public static void draw(BuiltBuffer buffer) {
        if (buffer == null) {
            return;
        }
        // В GUI (мира нет) immediate-путь даёт мерцание: матрицы чужого пасса.
        // 2D рисуется через DrawContext, сюда должны доходить только мировые вызовы.
        try {
            if (net.minecraft.client.MinecraftClient.getInstance().world == null) {
                buffer.close();
                return;
            }
        } catch (Throwable t) {
            try {
                buffer.close();
            } catch (Throwable ignored) {
            }
            return;
        }
        try {
            BuiltBuffer.DrawParameters params = buffer.getDrawParameters();
            VertexFormat format = params.format();
            VertexFormat.DrawMode mode = params.mode();
            boolean textured = hasTextureElement(format);
            RenderLayer layer = null;
            if (!textured) {
                layer = pickColorLayer(mode);
            } else {
                net.minecraft.util.Identifier tex = fallbackTexture;
                if (tex != null) {
                    layer = texturedLayers.computeIfAbsent(tex, RenderLayers::entityCutoutNoCull);
                }
            }
            if (layer != null) {
                layer.draw(buffer);
                logOk(format, mode, layer);
                return;
            }
            logFail("no-layer format=" + format + " mode=" + mode + " textured=" + textured);
        } catch (Throwable t) {
            logFail("draw-threw " + t);
        }
        try {
            buffer.close();
        } catch (Throwable ignored) {
        }
    }

    private static boolean loggedOk;
    private static boolean loggedFail;

    private static void logOk(VertexFormat format, VertexFormat.DrawMode mode, RenderLayer layer) {
        if (!loggedOk) {
            loggedOk = true;
            System.out.println("[LumenPort] BufferRenderer.draw OK format=" + format + " mode=" + mode + " layer=" + layer);
        }
    }

    private static void logFail(String msg) {
        if (!loggedFail) {
            loggedFail = true;
            System.out.println("[LumenPort] BufferRenderer.draw FAIL " + msg);
            new Throwable("[LumenPort] first draw failure").printStackTrace(System.out);
        }
    }

    private static boolean hasTextureElement(VertexFormat format) {
        try {
            return format.contains(VertexFormatElement.UV0) || format.contains(VertexFormatElement.UV);
        } catch (Throwable t) {
            return false;
        }
    }

    private static RenderLayer pickColorLayer(VertexFormat.DrawMode mode) {
        try {
            String name = mode.name();
            if (name.contains("LINE")) {
                return RenderLayers.LINES;
            }
            if (name.contains("FAN")) {
                return RenderLayers.debugTriangleFan();
            }
            return RenderLayers.debugQuads();
        } catch (Throwable t) {
            return null;
        }
    }
}
