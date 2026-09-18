package dlc.lumen.api.storages.implement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Ключи конфигов вместо файлов на диске.
 * JSON -> gzip -> base64 -> "LUMEN1-<base64>".
 * Никаких файлов на C:, только строка для переноса/хранения.
 */
public final class ConfigKeyCodec {
    private static final String PREFIX = "LUMEN1-";

    private ConfigKeyCodec() {
    }

    public static String encode(JsonObject json) throws Exception {
        byte[] raw = json.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(raw.length);
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(raw);
        }
        return PREFIX + Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    public static JsonObject decode(String key) throws Exception {
        if (key == null) {
            throw new IllegalArgumentException("empty key");
        }
        String s = key.trim();
        // разрешаем вставку с пробелами/переносами из чата
        s = s.replaceAll("\\s+", "");
        if (s.startsWith(PREFIX)) {
            s = s.substring(PREFIX.length());
        }
        byte[] compressed = Base64.getDecoder().decode(s);
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = gzip.read(buf)) != -1) {
                bos.write(buf, 0, n);
            }
            String json = bos.toString(StandardCharsets.UTF_8);
            return JsonParser.parseString(json).getAsJsonObject();
        }
    }

    public static boolean looksLikeKey(String s) {
        if (s == null) {
            return false;
        }
        String t = s.trim().replaceAll("\\s+", "");
        return t.startsWith(PREFIX) && t.length() > PREFIX.length() + 16;
    }
}