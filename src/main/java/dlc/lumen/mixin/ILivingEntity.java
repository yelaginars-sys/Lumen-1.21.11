package dlc.lumen.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface ILivingEntity {
   @Accessor("lastAttackedTicks")
   int getLastAttackedTicks();

   @Accessor("jumpingCooldown")
   void setJumpingCooldown(int var1);

   @Invoker("getGravity")
   double invokeGetGravity();
}