import os

target_dir = r"C:\Users\yelag\Downloads\Lumen-1.21.11\src\main\java"

replacements = {
    "import net.minecraft.client.render.VertexFormat.DrawMode;": "// import DrawMode;",
    "import com.mojang.blaze3d.platform.GlStateManager.DstFactor;": "// import DstFactor;",
    "import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;": "// import SrcFactor;",
    "DrawMode.QUADS": "VertexFormat.DrawMode.QUADS",
    "DrawMode.TRIANGLES": "VertexFormat.DrawMode.TRIANGLES",
    "DrawMode.TRIANGLE_STRIP": "VertexFormat.DrawMode.TRIANGLE_STRIP",
    "DrawMode.LINES": "VertexFormat.DrawMode.LINES",
    "DrawMode.LINE_STRIP": "VertexFormat.DrawMode.LINE_STRIP",
    "DstFactor.ONE_MINUS_SRC_ALPHA": "GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA",
    "SrcFactor.SRC_ALPHA": "GlStateManager.SrcFactor.SRC_ALPHA",
}

count = 0
for root, dirs, files in os.walk(target_dir):
    for f in files:
        if f.endswith(".java"):
            filepath = os.path.join(root, f)
            with open(filepath, "r", encoding="utf-8", errors="ignore") as file:
                content = file.read()
            
            modified = False
            for k, v in replacements.items():
                if k in content:
                    content = content.replace(k, v)
                    modified = True
            
            if modified:
                with open(filepath, "w", encoding="utf-8") as file:
                    file.write(content)
                count += 1

print(f"Updated {count} files.")
