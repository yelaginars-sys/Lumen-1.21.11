package dlc.lumen.mixin;

import dlc.lumen.api.QClient;
import dlc.lumen.api.events.EventInvoker;
import dlc.lumen.api.events.implement.Event3DRender;
import dlc.lumen.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import dlc.lumen.client.bots.core.BotViewRenderer;
import dlc.lumen.client.modules.impl.render.Optimizer;
import dlc.lumen.client.modules.impl.render.Removals;
import dlc.lumen.client.modules.impl.render.ShaderEsp;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.option.CloudRenderMode;import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.memory.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements QClient {
   @Inject(method = "renderParticles", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderParticles(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice fog, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Частицы")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesParticles()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderWeather(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice fog, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Погода")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesWeather()) {
            ci.cancel();
         }
      }
   }

   // TODO 1.21.11: addWeatherParticlesAndSound removed, weather handled in renderWeather frame-graph pass

   @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderClouds(
      FrameGraphBuilder frameGraphBuilder,
      CloudRenderMode renderMode,
      Vec3d cameraPos,
      long time,
      float ticks,
      int color,
      float cloudHeight,
      CallbackInfo ci
   ) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Облака")) {
            ci.cancel();
         }

         Optimizer optimizer = ModuleClass.optimizer;
         if (optimizer != null && optimizer.removesClouds()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "fillBlockEntityRenderStates", at = @At("HEAD"), cancellable = true)
   private void wonderful$renderBlockEntities(Camera camera, float tickDelta, WorldRenderState state, CallbackInfo ci) {
      if (ModuleClass.INSTANCE != null) {
         Removals removals = ModuleClass.removals;
         if (removals != null && removals.isEnabled("Блок-сущности")) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "render", at = @At("RETURN"))
   private void render(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      Matrix4f viewMatrix,
      GpuBufferSlice fog,
      Vector4f fogColor,
      boolean renderParticles,
      CallbackInfo ci
   ) {
      if (!BotViewRenderer.inBotPip) {
         ShaderEsp.INSTANCE.setOutlineFramebuffer(((WorldRendererAccessor)this).lumen$getEntityOutlineFramebufferRaw());
         boolean has3DListeners = EventInvoker.hasListeners(Event3DRender.class);
         if (has3DListeners) {
            Profilers.get().swap("wonderful_renderWorld");
            MatrixStack matrices = new MatrixStack();
            matrices.multiplyPositionMatrix(positionMatrix);
            if (has3DListeners) {
               new Event3DRender(matrices, positionMatrix, projectionMatrix, camera, tickCounter.getTickProgress(false)).call();
            }
         }
      }
   }

   @Inject(method = "drawEntityOutlinesFramebuffer", at = @At("HEAD"), cancellable = true)
   private void wonderful$drawEntityOutlinesFramebuffer(CallbackInfo ci) {
      ShaderEsp esp = ShaderEsp.INSTANCE;
      if (esp != null && esp.isEnable() && esp.hasTargets()) {
         ci.cancel();
      }
   }

   @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
   public void onDrawBlockOutline(
      MatrixStack matrices,
      net.minecraft.client.render.VertexConsumer vertexConsumers,
      double x,
      double y,
      double z,
      net.minecraft.client.render.state.OutlineRenderState outlineState,
      int color,
      float lineWidth,
      CallbackInfo ci
   ) {
      if (ModuleClass.blockOverlay.isEnable()) {
         ci.cancel();
      }
   }
}