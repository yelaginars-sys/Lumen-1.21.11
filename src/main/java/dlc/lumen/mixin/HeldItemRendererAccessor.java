package dlc.lumen.mixin;

import net.minecraft.client.MinecraftClient;
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
public interface HeldItemRendererAccessor {
   @Accessor("mainHand")
   ItemStack getMainHand();

   @Accessor("offHand")
   ItemStack getOffHand();

   @Accessor("equipProgressMainHand")
   float getEquipProgressMainHand();

   @Accessor("lastEquipProgressMainHand")
   float getPrevEquipProgressMainHand();

   @Accessor("equipProgressOffHand")
   float getEquipProgressOffHand();

   @Accessor("lastEquipProgressOffHand")
   float getPrevEquipProgressOffHand();

   @Accessor("client")
   MinecraftClient getClient();

   @Invoker("renderFirstPersonItem")
   void invokeRenderFirstPersonItem(
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
   void invokeApplyEquipOffset(MatrixStack var1, Arm var2, float var3);

   @Invoker("swingArm")
   void invokeSwingArm(float var1, MatrixStack var2, int var3, Arm var4);

   @Invoker("renderArmHoldingItem")
   void invokeRenderArmHoldingItem(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, float var5, Arm var6);

   @Invoker("renderMapInBothHands")
   void invokeRenderMapInBothHands(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, float var5, float var6);

   @Invoker("renderMapInOneHand")
   void invokeRenderMapInOneHand(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, Arm var5, float var6, ItemStack var7);

   @Invoker("applySwingOffset")
   void invokeApplySwingOffset(MatrixStack var1, Arm var2, float var3);
}