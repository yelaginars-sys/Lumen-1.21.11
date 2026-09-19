package dlc.lumen.api.utils.client;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFWNativeWin32;

/**
 * Заголовок окна "Lumen 1.21.11" + тёмный системный тулбар Windows.
 * Тулбар — через DWM (DWMWA_USE_IMMERSIVE_DARK_MODE), только Windows, всё в try/catch.
 */
public final class WindowChrome {
   private static final String TITLE = "Lumen 1.21.11";
   private static volatile boolean darkApplied;

   private WindowChrome() {
   }

   public static void apply(MinecraftClient mc) {
      if (mc == null || mc.getWindow() == null) {
         return;
      }
      try {
         mc.getWindow().setTitle(TITLE);
      } catch (Throwable ignored) {
      }
      if (darkApplied) {
         return;
      }
      try {
         String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
         if (!os.contains("win")) {
            return;
         }
         long hwnd = GLFWNativeWin32.glfwGetWin32Window(mc.getWindow().getHandle());
         if (hwnd == 0L) {
            return;
         }
         DwmApi api = Native.load("dwmapi", DwmApi.class);
         int ok = api.DwmSetWindowAttribute(new Pointer(hwnd), 20, new IntByReference(1), 4);
         if (ok == 0) {
            darkApplied = true;
         }
      } catch (Throwable ignored) {
      }
   }

   private interface DwmApi extends Library {
      int DwmSetWindowAttribute(Pointer hwnd, int dwAttribute, IntByReference pvAttribute, int cbAttribute);
   }
}
