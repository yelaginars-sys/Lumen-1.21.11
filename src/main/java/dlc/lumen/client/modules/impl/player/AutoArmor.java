package dlc.lumen.client.modules.impl.player;

import dlc.lumen.api.events.EventLink;
import dlc.lumen.api.events.implement.EventUpdate;
import dlc.lumen.client.modules.Module;
import dlc.lumen.client.modules.settings.implement.FloatSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
   public static AutoArmor INSTANCE = new AutoArmor();
   private final FloatSetting floatSetting = new FloatSetting("Задержка", 25.0F, 1.0F, 1000.0F, 1.0F);
   private long timestamp = 0L;

   public AutoArmor() {
      super("AutoArmor", "Автоматически одевает броню", Module.ModuleCategory.PLAYER);
      this.addSettings(this.floatSetting);
   }

   @EventLink
   public void onEvent(EventUpdate event) {
      if (mc.player != null && mc.world != null) {
         if (!this.helper()) {
            long var2 = System.currentTimeMillis();
         if (!((float)(var2 - this.timestamp) < this.floatSetting.get())) {
               for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
                  if (mc.player.getEquippedStack(slot).isEmpty()) {
                     for (int var6 = 0; var6 < 36; var6++) {
                        ItemStack var7 = mc.player.getInventory().getStack(var6);
                        if (!var7.isEmpty()) {
                           EquippableComponent var8 = var7.get(DataComponentTypes.EQUIPPABLE);
                           if (var8 != null && var8.slot() == slot) {
                              int var10 = var6;
                              if (var6 < 9) {
                                 var10 = var6 + 36;
                              }

                              mc.interactionManager.clickSlot(0, var10, 0, SlotActionType.QUICK_MOVE, mc.player);
                              this.timestamp = var2;
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean helper() {
      return mc.player.input.getMovementInput().y != 0.0F || mc.player.input.getMovementInput().x != 0.0F;
   }
}