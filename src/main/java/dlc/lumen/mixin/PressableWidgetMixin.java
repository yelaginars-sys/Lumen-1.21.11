package dlc.lumen.mixin;

import dlc.lumen.api.utils.color.ColorUtils;
import dlc.lumen.api.utils.render.RenderUtils;
import dlc.lumen.api.utils.render.fonts.LumenText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Кастомные кнопки Lumen вместо ванильных серых:
 * тёмная скруглённая плашка, подсветка темы при наведении, текст своим шрифтом.
 * Клик/навигация/наррация не тронуты, иконки сабклассов дорисовываются как раньше.
 */
@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin extends ClickableWidget {
   public PressableWidgetMixin(int x, int y, int width, int height, Text message) {
      super(x, y, width, height, message);
   }

   @Shadow
   protected abstract void drawIcon(DrawContext context, int mouseX, int mouseY, float delta);

   @Unique
   private static long lumen$lastLog = 0L;

   private static int lumen$theme() {
      try {
         return ColorUtils.getThemeColor();
      } catch (Throwable var1) {
         return -9073971;
      }
   }

   @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
   private void lumen$customButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      // TEMP-DEBUG: доказать что хендлер вызывается (убрать после проверки).
      long now = System.currentTimeMillis();
      if (now - lumen$lastLog > 10000L) {
         lumen$lastLog = now;
         System.out.println("[LumenButtons] renderWidget fired");
      }
      ClickableWidget self = (ClickableWidget)(Object)this;
      int x = self.getX();
      int y = self.getY();
      int w = self.getWidth();
      int h = self.getHeight();
      if (w <= 0 || h <= 0) {
         ci.cancel();
         return;
      }
      boolean on = self.active;
      boolean hover = on && (self.isHovered() || self.isFocused());
      int theme = lumen$theme();
      int bg = on ? ColorUtils.rgba(16, 19, 28, 215) : ColorUtils.rgba(12, 13, 18, 180);
      int border = !on ? ColorUtils.rgba(255, 255, 255, 28) : (hover ? theme : ColorUtils.rgba(255, 255, 255, 55));
      float r = Math.min(6.0F, h / 2.0F - 1.0F);
      MatrixStack matrices = new MatrixStack();
      RenderUtils.currentContext = context;
      try {
         RenderUtils.drawRoundedRect(matrices, x, y, w, h, r, bg);
         RenderUtils.drawRoundedRectOutline(matrices, x + 0.5F, y + 0.5F, w - 1.0F, h - 1.0F, Math.max(0.0F, r - 0.5F), 1.0F, border, border, border, border);
         String label = self.getMessage().getString();
         if (label != null && !label.isEmpty()) {
            float size = 11.0F;
            float tw = LumenText.width("suisse", label, size);
            int color;
            if (!on) {
               color = 0xFFA0A0A0;
            } else if (hover) {
               color = 0xFFFFFFA0;
            } else {
               color = 0xFFFFFFFF;
            }
            float ty = y + (h - size * 0.72F) / 2.0F;
            LumenText.draw(context, "suisse", label, x + (w - tw) / 2.0F, ty, size, color, true);
         }
      } finally {
         RenderUtils.currentContext = null;
      }
      try {
         this.drawIcon(context, mouseX, mouseY, delta);
      } catch (Throwable ignored) {
      }
      ci.cancel();
   }
}
