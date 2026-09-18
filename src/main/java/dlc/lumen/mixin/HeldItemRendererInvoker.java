package dlc.lumen.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HeldItemRenderer.class)
public interface HeldItemRendererInvoker {
   @Accessor("mainHand")
   ItemStack whylol$getMainHand();

   @Accessor("offHand")
   ItemStack whylol$getOffHand();

   @Invoker("renderFirstPersonItem")
   void whylol$callRenderFirstPersonItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10
   );

   @Invoker("applyEquipOffset")
   void whylol$applyEquipOffset(MatrixStack var1, Arm var2, float var3);

   @Invoker("swingArm")
   void whylol$callSwingArm(float var1, MatrixStack var2, int var3, Arm var4);
}