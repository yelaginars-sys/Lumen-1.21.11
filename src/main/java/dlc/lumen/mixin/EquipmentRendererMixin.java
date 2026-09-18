package dlc.lumen.mixin;

import dlc.lumen.client.modules.impl.render.ViewArmorDurability;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
   @Unique
   private ItemStack lumen$stack = ItemStack.EMPTY;

   @Inject(
      method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
      at = @At("HEAD")
   )
   private void lumen$captureStack(
      LayerType layerType,
      RegistryKey<EquipmentAsset> asset,
      Model model,
      Object state,
      ItemStack stack,
      MatrixStack matrices,
      OrderedRenderCommandQueue queue,
      int light,
      Identifier texture,
      int x,
      int y,
      CallbackInfo ci
   ) {
      this.lumen$stack = stack;
   }

   @ModifyArg(
      method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/command/RenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
      ),
      index = 6
   )
   private int lumen$durabilityTint(int color) {
      ViewArmorDurability module = ViewArmorDurability.INSTANCE;
      return module != null && module.shouldTint(this.lumen$stack) ? module.barColor(this.lumen$stack) : color;
   }
}