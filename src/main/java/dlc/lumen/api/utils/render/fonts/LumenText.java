package dlc.lumen.api.utils.render.fonts;

import dlc.lumen.api.QClient;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * 1.21.11: кастомных шейдеров нет, MSDF-атласы напрямую не отрисовать.
 * Текст в HUD рисуем штатным TextRenderer:
 * - иконочные шрифты (icon/icon1/iconnew) — свои TTF через assets/lumen/font/*.json,
 *   выбираются стилем StyleSpriteSource.Font;
 * - текстовые (suisse/inter_medium/...) — дефолтным шрифтом ванилы;
 * - §-коды разбираем сами на сегменты (DrawContext.drawText(String) их не парсит).
 * Метрики берутся из того же TextRenderer — тексты не "съезжают".
 */
public final class LumenText implements QClient {
   private static final float ICON_BASE_SIZE = 20.0F;
   // Текст: Narezka шире MSDF-suisse в ~2.2 раза (замер по advances: ratio 0.46 равномерно).
   // В 1.21.4 MSDF масштабировал шрифты как size * 0.5F. Для совпадения размеров плашек и текста:
   private static final float TEXT_BASE_SIZE = 20.0F;

   private LumenText() {
   }

   private static TextRenderer renderer() {
      return mc == null ? null : mc.textRenderer;
   }

   /** id шрифта для стиля; null = дефолтный шрифт ванилы. */
   private static Identifier fontId(String font) {
      return switch (normalize(font)) {
         case "icon" -> Identifier.of("lumen", "icon");
         case "icon1" -> Identifier.of("lumen", "icon1");
         case "iconnew" -> Identifier.of("lumen", "iconnew");
         case "icons", "iconz" -> Identifier.of("lumen", "icon");
         case "sf_regular" -> Identifier.of("lumen", "sf_regular");
         case "inter_medium", "modern" -> Identifier.of("lumen", "inter_medium");
         case "narezka" -> Identifier.of("lumen", "narezka");
         case "suisse", "lumen", "logo", "wave", "energy", "tyzik", "wonderful" ->
            Identifier.of("lumen", "suisse");
         default -> null;
      };
   }

   private static String normalize(String font) {
      if (font == null) {
         return "";
      }
      // MsdfFont хранит имена вида "lumen/inter_medium", "lumen/icons".
      int i = font.lastIndexOf('/');
      return i >= 0 ? font.substring(i + 1) : font;
   }

   private static boolean isIcon(String font) {
      return switch (normalize(font)) {
         case "icon", "icon1", "iconnew", "icons", "iconz" -> true;
         default -> false;
      };
   }

   private static boolean isCustomText(String font) {
      return fontId(font) != null && !isIcon(font);
   }

   private static final class Seg {
      String text;
      int rgb;
      boolean bold;
      boolean italic;
      boolean strike;
      boolean underline;
   }

   private static Formatting codeToFormatting(char code) {
      return switch (code) {
         case '0' -> Formatting.BLACK;
         case '1' -> Formatting.DARK_BLUE;
         case '2' -> Formatting.DARK_GREEN;
         case '3' -> Formatting.DARK_AQUA;
         case '4' -> Formatting.DARK_RED;
         case '5' -> Formatting.DARK_PURPLE;
         case '6' -> Formatting.GOLD;
         case '7' -> Formatting.GRAY;
         case '8' -> Formatting.DARK_GRAY;
         case '9' -> Formatting.BLUE;
         case 'a', 'A' -> Formatting.GREEN;
         case 'b', 'B' -> Formatting.AQUA;
         case 'c', 'C' -> Formatting.RED;
         case 'd', 'D' -> Formatting.LIGHT_PURPLE;
         case 'e', 'E' -> Formatting.YELLOW;
         case 'f', 'F' -> Formatting.WHITE;
         default -> null;
      };
   }

   private static List<Seg> split(String text, int defaultRgb) {
      List<Seg> out = new ArrayList<>();
      StringBuilder cur = new StringBuilder();
      int rgb = defaultRgb;
      boolean bold = false, italic = false, strike = false, underline = false;
      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if ((c == 167 || c == '&') && i + 1 < text.length()) {
            char code = text.charAt(i + 1);
            Formatting fmt = codeToFormatting(code);
            boolean handled = true;
            if (fmt != null) {
               Integer v = fmt.getColorValue();
               if (v != null) {
                  if (cur.length() > 0) {
                     out.add(makeSeg(cur.toString(), rgb, bold, italic, strike, underline));
                     cur.setLength(0);
                  }
                  rgb = v;
                  bold = italic = strike = underline = false;
               }
            } else if (code == 'l' || code == 'L') {
               bold = true;
            } else if (code == 'o' || code == 'O') {
               italic = true;
            } else if (code == 'm' || code == 'M') {
               strike = true;
            } else if (code == 'n' || code == 'N') {
               underline = true;
            } else if (code == 'r' || code == 'R') {
               if (cur.length() > 0) {
                  out.add(makeSeg(cur.toString(), rgb, bold, italic, strike, underline));
                  cur.setLength(0);
               }
               rgb = defaultRgb;
               bold = italic = strike = underline = false;
            } else {
               handled = false;
            }
            if (handled) {
               i++;
               continue;
            }
         }
         cur.append(c);
      }
      if (cur.length() > 0) {
         out.add(makeSeg(cur.toString(), rgb, bold, italic, strike, underline));
      }
      return out;
   }

   private static Seg makeSeg(String text, int rgb, boolean bold, boolean italic, boolean strike, boolean underline) {
      Seg s = new Seg();
      s.text = text;
      s.rgb = rgb;
      s.bold = bold;
      s.italic = italic;
      s.strike = strike;
      s.underline = underline;
      return s;
   }

   private static Text buildText(String font, String text, int defaultRgb) {
      Identifier id = fontId(font);
      StyleSpriteSource src = id == null ? null : new StyleSpriteSource.Font(id);
      MutableText root = Text.empty();
      for (Seg seg : split(text, defaultRgb)) {
         Style st = Style.EMPTY.withColor(seg.rgb);
         if (src != null) {
            st = st.withFont(src);
         }
         if (seg.bold) {
            st = st.withBold(true);
         }
         if (seg.italic) {
            st = st.withItalic(true);
         }
         if (seg.strike) {
            st = st.withStrikethrough(true);
         }
         if (seg.underline) {
            st = st.withUnderline(true);
         }
         root.append(Text.literal(seg.text).setStyle(st));
      }
      return root;
   }

   public static float width(String font, String text, float size) {
      TextRenderer tr = renderer();
      if (tr == null || text == null || text.isEmpty()) {
         return 0.0F;
      }
      float w = tr.getWidth(buildText(font, text, 0xFFFFFF));
      if (isIcon(font)) {
         w *= size / ICON_BASE_SIZE;
      } else if (isCustomText(font)) {
         w *= size / TEXT_BASE_SIZE;
      }
      return w;
   }

   public static void draw(DrawContext ctx, String font, String text, float x, float y, float size, int color, boolean shadow) {
      TextRenderer tr = renderer();
      if (tr == null || ctx == null || text == null || text.isEmpty()) {
         return;
      }
      int alpha = color >>> 24 & 0xFF;
      if (alpha == 0) {
         alpha = 255;
      }
      int rgb = color & 0xFFFFFF;
      Text t = buildText(font, text, rgb);
      int argb = alpha << 24 | rgb;
      float scale;
      if (isIcon(font)) {
         scale = size / ICON_BASE_SIZE;
      } else if (isCustomText(font)) {
         scale = size / TEXT_BASE_SIZE;
      } else {
         ctx.drawText(tr, t, (int) x, (int) y, argb, shadow);
         return;
      }
      if (scale <= 0.0F) {
         return;
      }
      var matrices = ctx.getMatrices();
      matrices.pushMatrix();
      try {
         matrices.translate(x, y);
         matrices.scale(scale, scale);
         ctx.drawText(tr, t, 0, 0, argb, shadow);
      } finally {
         matrices.popMatrix();
      }
   }
}
