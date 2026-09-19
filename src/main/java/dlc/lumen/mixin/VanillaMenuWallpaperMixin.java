package dlc.lumen.mixin;

import dlc.lumen.client.ui.mainmenu.MenuWallpapers;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Рандомные обои Lumen вместо ванильного dirt/panorama:
 * сетевая игра, одиночная игра, настройки, загрузка территории.
 */
@Mixin(Screen.class)
public abstract class VanillaMenuWallpaperMixin {
   @Shadow
   public int width;
   @Shadow
   public int height;

   @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
   private void lumen$menuWallpaper(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      Object self = this;
      if (self instanceof MultiplayerScreen
         || self instanceof SelectWorldScreen
         || self instanceof OptionsScreen
         || self instanceof DownloadingTerrainScreen) {
         MenuWallpapers.render(context, this.width, this.height);
         context.fill(0, 0, this.width, this.height, 0x78090A0F);
         ci.cancel();
      }
   }
}
