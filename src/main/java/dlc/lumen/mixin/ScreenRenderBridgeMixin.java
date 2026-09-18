package dlc.lumen.mixin;

import dlc.lumen.api.utils.render.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Мост DrawContext для immediate-примитивов: пока рисуется любой Screen,
// RenderUtils/Font берут его контекст вместо мёртвого immediate-пути.
// Мировые вызовы (Event3DRender) идут мимо — там контекст не ставится.
@Mixin(Screen.class)
public class ScreenRenderBridgeMixin {
   @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"))
   private void lumen$setHudContext(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      RenderUtils.currentContext = context;
   }

   @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("RETURN"))
   private void lumen$clearHudContext(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      RenderUtils.currentContext = null;
   }
}
