package dlc.lumen.client.social;

import com.mojang.blaze3d.systems.RenderSystem;
import dlc.lumen.Lumen;
import dlc.lumen.api.utils.render.ShaderUtils;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class GlobalProfileAvatar {
   private static final Identifier AVATAR_TEXTURE_ID = Identifier.of("lumen", "global_profile_avatar");
   private static final int AVATAR_SIZE = 256;
   private static NativeImageBackedTexture avatarTexture;
   private static long avatarLastModified = Long.MIN_VALUE;

   private GlobalProfileAvatar() {
   }

   public static synchronized boolean hasSavedAvatar() {
      File var0 = avatarFile();
      return var0.isFile() && var0.length() > 0L;
   }

   public static synchronized boolean saveFromFile(File source) {
      if (source != null && source.isFile()) {
         try {
            BufferedImage var1 = ImageIO.read(source);
            if (var1 != null && var1.getWidth() > 0 && var1.getHeight() > 0) {
               int var2 = Math.min(var1.getWidth(), var1.getHeight());
               int var3 = (var1.getWidth() - var2) / 2;
               int var4 = (var1.getHeight() - var2) / 2;
               BufferedImage var5 = new BufferedImage(256, 256, 2);
               Graphics2D var6 = var5.createGraphics();
               var6.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
               var6.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               var6.drawImage(var1, 0, 0, 256, 256, var3, var4, var3 + var2, var4 + var2, null);
               var6.dispose();
               File var7 = avatarFile();
               File var8 = var7.getParentFile();
               if (var8 != null) {
                  var8.mkdirs();
               }

               ImageIO.write(var5, "png", var7);
               releaseAvatarTexture();
               return true;
            } else {
               return false;
            }
         } catch (Exception var9) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static synchronized void clear() {
      File var0 = avatarFile();
      if (var0.exists()) {
         var0.delete();
      }

      releaseAvatarTexture();
   }

   public static synchronized Identifier textureId() {
      ensureAvatarTexture();
      return avatarTexture == null ? null : AVATAR_TEXTURE_ID;
   }

   public static synchronized boolean drawRounded(DrawContext context, float x, float y, float w, float h, float radius, int color) {
      Identifier var7 = textureId();
      if (context != null && var7 != null) {
         // TODO 1.21.11: rounded corners need RenderPipeline rework, plain blit for now
         context.drawTexturedQuad(var7, (int)x, (int)y, (int)w, (int)h, 0.0F, 0.0F, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private static File avatarFile() {
      // без файлов: аватара на диске нет
      if (Lumen.INSTANCE == null || Lumen.INSTANCE.globalsDir == null) {
         return null;
      }
      File var0 = Lumen.INSTANCE.globalsDir;
      return new File(var0, "global-avatar.png");
   }

   private static void ensureAvatarTexture() {
      File var0 = avatarFile();
      if (var0 == null) {
         return;
      }
      long var1 = var0.isFile() ? var0.lastModified() : Long.MIN_VALUE;
      if (!var0.isFile()) {
         releaseAvatarTexture();
      } else if (avatarTexture == null || avatarLastModified != var1) {
         releaseAvatarTexture();
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3 != null) {
            try (InputStream var4 = Files.newInputStream(var0.toPath())) {
               NativeImage var5 = NativeImage.read(var4);
               avatarTexture = new NativeImageBackedTexture(() -> "lumen-avatar", var5);
               var3.getTextureManager().registerTexture(AVATAR_TEXTURE_ID, avatarTexture);
               avatarLastModified = var1;
            } catch (Exception var9) {
               releaseAvatarTexture();
            }
         }
      }
   }

   private static void releaseAvatarTexture() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null) {
         var0.getTextureManager().destroyTexture(AVATAR_TEXTURE_ID);
      }

      if (avatarTexture != null) {
         avatarTexture = null;
      }

      avatarLastModified = Long.MIN_VALUE;
   }
}