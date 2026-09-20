package dlc.lumen.client.ui.mainmenu;

import dlc.lumen.api.utils.color.ColorUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

/**
 * Рандомный видео-фон без потери качества: при каждом запуске выбирается один из MP4 видеофайлов
 * из D:\minecraft-wallpapers (aquarium, beach, cherry-grove, deserts, falling-snow, farm-morning,
 * fireplace, glowing-caves, rainy-swamp, serene-snow).
 * 
 * Отрисовывается анимация 25 FPS во всех меню клиента: Title Screen, Account Manager, Singleplayer, Multiplayer, Loading Screen.
 */
public final class MenuWallpapers {
   private static final Identifier DYNAMIC_TEX_ID = Identifier.of("lumen", "textures/menu_bg/dynamic_video");
   private static final int TEX_W = 1280;
   private static final int TEX_H = 720;
   private static final int FPS = 25;

   private static volatile NativeImageBackedTexture dynamicTexture;
   private static volatile File[] frameFiles;
   private static volatile long startTime = -1L;
   private static volatile int lastFrameIndex = -1;
   private static boolean initialized = false;

   private MenuWallpapers() {
   }

   private static synchronized void init() {
      if (initialized) {
         return;
      }
      initialized = true;

      try {
         File cacheDir = new File(System.getProperty("user.home"), ".lumen/wallpaper_cache");
         File[] subDirs = cacheDir.listFiles(File::isDirectory);
         
         if (subDirs != null && subDirs.length > 0) {
            File chosenDir = subDirs[new Random().nextInt(subDirs.length)];
            File[] files = chosenDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
            if (files != null && files.length > 0) {
               java.util.Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
               frameFiles = files;
               startTime = System.currentTimeMillis();
            }
         }
      } catch (Throwable t) {
         t.printStackTrace();
      }
   }

   private static Identifier currentFrame() {
      if (!initialized) {
         init();
      }

      File[] files = frameFiles;
      if (files == null || files.length == 0) {
         return Identifier.of("lumen", "textures/mainmenu/single_bg.png");
      }

      long elapsed = System.currentTimeMillis() - startTime;
      int frameIndex = (int) ((elapsed / (1000 / FPS)) % files.length);

      if (frameIndex != lastFrameIndex || dynamicTexture == null) {
         lastFrameIndex = frameIndex;
         File frameFile = files[frameIndex];
         try (FileInputStream fis = new FileInputStream(frameFile)) {
            NativeImage newImg = NativeImage.read(fis);
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.getTextureManager() != null) {
               if (dynamicTexture == null || dynamicTexture.getImage() == null || dynamicTexture.getImage().getWidth() != newImg.getWidth() || dynamicTexture.getImage().getHeight() != newImg.getHeight()) {
                  if (dynamicTexture != null) {
                     mc.getTextureManager().destroyTexture(DYNAMIC_TEX_ID);
                     dynamicTexture.close();
                  }
                  dynamicTexture = new NativeImageBackedTexture(() -> "lumen-wallpaper-frame", newImg);
                  mc.getTextureManager().registerTexture(DYNAMIC_TEX_ID, dynamicTexture);
               } else {
                  dynamicTexture.getImage().copyFrom(newImg);
                  dynamicTexture.upload();
                  newImg.close();
               }
            } else {
               newImg.close();
            }
         } catch (Throwable t) {
            t.printStackTrace();
         }
      }

      return DYNAMIC_TEX_ID;
   }

   public static void render(DrawContext context, int width, int height) {
      if (context == null || width <= 0 || height <= 0) {
         return;
      }
      try {
         Identifier tex = currentFrame();
         if (tex != null) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, tex, 0, 0, 0.0F, 0.0F, width, height, width, height);
         }
      } catch (Throwable t) {
         context.fillGradient(0, 0, width, height, ColorUtils.rgb(12, 18, 32), ColorUtils.rgb(26, 36, 58));
      }
   }
}
