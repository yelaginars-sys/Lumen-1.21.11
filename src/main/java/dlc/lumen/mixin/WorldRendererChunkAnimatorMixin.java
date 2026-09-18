package dlc.lumen.mixin;

import dlc.lumen.client.modules.impl.render.ChunkAnimator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.ScopedProfiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public class WorldRendererChunkAnimatorMixin {
   @Inject(method = "addBuiltChunk", at = @At("HEAD"))
   private void lumen$chunkBuilt(BuiltChunk builtChunk, CallbackInfo ci) {
      ChunkAnimator.INSTANCE.onChunkBuilt(builtChunk.getSectionPos());
   }
}