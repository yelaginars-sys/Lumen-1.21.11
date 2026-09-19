package dlc.lumen.client.ui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.api.QClient;
import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.ShaderUtils;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;

public final class MenuBackground implements QClient {
   private static final Identifier MENU_BG = Identifier.of("lumen", "textures/mainmenu/menu_bg.png");
   private MenuBackground() {
   }

   public static void render(DrawContext context, int width, int height) {
      // Рандомные обои на запуск (кадры из minecraft-wallpapers), фолбэк — градиент.
      try {
         MenuWallpapers.render(context, width, height);
      } catch (Throwable var3) {
         context.fillGradient(0, 0, width, height, ColorUtils.rgb(12, 18, 32), ColorUtils.rgb(26, 36, 58));
      }
   }

   private static boolean helper() {
      try {
         ShaderProgram var0 = null;
         if (var0 == null) {
            return false;
         }

         float var1 = (float)(System.currentTimeMillis() % 1000000L / 1000.0);
         int var2 = helper2();
         float var3 = mc.getWindow().getFramebufferWidth();
         float var4 = mc.getWindow().getFramebufferHeight();
//          RenderSystem.disableDepthTest();
//          RenderSystem.disableCull();
//          RenderSystem.disableBlend();
//          RenderSystem.setShader(ShaderUtils.mainMenuBackground);
         helper3(var0, "Time", var1);
         helper4(var0, "Theme", ColorUtils.redf(var2), ColorUtils.greenf(var2), ColorUtils.bluef(var2));
         helper5(var0, "Resolution", var3, var4);
         BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
         var5.vertex(-1.0F, -1.0F, 0.0F).texture(0.0F, 0.0F);
         var5.vertex(-1.0F, 1.0F, 0.0F).texture(0.0F, 1.0F);
         var5.vertex(1.0F, 1.0F, 0.0F).texture(1.0F, 1.0F);
         var5.vertex(1.0F, -1.0F, 0.0F).texture(1.0F, 0.0F);
         BufferRenderer.drawWithGlobalProgram(var5.end());
//          RenderSystem.enableDepthTest();
//          RenderSystem.enableCull();
//          RenderSystem.enableBlend();
//          RenderSystem.defaultBlendFunc();
//          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         return true;
      } catch (Throwable var6) {
         return false;
      }
   }

   private static int helper2() {
      try {
         return ColorUtils.getThemeColor();
      } catch (Exception var1) {
         return -9073971;
      }
   }

   private static void helper3(ShaderProgram shader, String name, float value) {
      GlUniform var3 = ShaderUtils.uniformOrNull(shader, name);
      if (var3 != null) {
//          var3.set(value);
      }
   }

   private static void helper4(ShaderProgram shader, String name, float x, float y, float z) {
      GlUniform var5 = ShaderUtils.uniformOrNull(shader, name);
      if (var5 != null) {
//          var5.set(x, y, z);
      }
   }

   private static void helper5(ShaderProgram shader, String name, float x, float y) {
      GlUniform var4 = ShaderUtils.uniformOrNull(shader, name);
      if (var4 != null) {
//          var4.set(x, y);
      }
   }
}