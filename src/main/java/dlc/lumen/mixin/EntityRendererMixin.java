package dlc.lumen.mixin;

import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<S extends EntityRenderState> {
   @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
   private void lumen$renderLabelIfPresent(
      S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci
   ) {
      if (ModuleClass.INSTANCE != null && ModuleClass.nameTags != null) {
         if (ModuleClass.nameTags.shouldReplaceVanillaLabel(state)) {
            ci.cancel();
         }
      }
   }
}