package dlc.lumen.client.ui.mainmenu;

import dlc.lumen.api.utils.color.ColorUtils;
import java.util.Random;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Рандомные обои меню: один случайный фон на запуск игры.
 * Кадры 1920x1080 из D:\minecraft-wallpapers (mp4 не крутятся в MC, берём кадры).
 * Рисуем cover-crop (без растяжек) на титле, аккаунтах, настройках, сетевой и одиночной.
 */
public final class MenuWallpapers {
   private static final int COUNT = 10;
   private static final int TEX_W = 1920;
   private static final int TEX_H = 1080;
   private static volatile Identifier current;

   private MenuWallpapers() {
   }

   public static Identifier current() {
      Identifier id = current;
      if (id == null) {
         int n = new Random().nextInt(COUNT);
         id = Identifier.of("lumen", "textures/menu_bg/wall_" + (n < 10 ? "0" + n : String.valueOf(n)) + ".jpg");
         current = id;
      }
      return id;
   }

   public static void render(DrawContext context, int width, int height) {
      if (context == null || width <= 0 || height <= 0) {
         return;
      }
      try {
         float scale = Math.max(width / (float)TEX_W, height / (float)TEX_H);
         float visW = width / scale;
         float visH = height / scale;
         float u = (TEX_W - visW) / 2.0F;
         float v = (TEX_H - visH) / 2.0F;
         context.drawTexture(RenderPipelines.GUI_TEXTURED, current(), 0, 0, u, v, width, height, TEX_W, TEX_H);
      } catch (Throwable var6) {
         context.fillGradient(0, 0, width, height, ColorUtils.rgb(12, 18, 32), ColorUtils.rgb(26, 36, 58));
      }
   }
}
