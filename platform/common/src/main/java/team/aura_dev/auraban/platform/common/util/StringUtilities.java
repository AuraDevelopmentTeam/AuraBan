package team.aura_dev.auraban.platform.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.codehaus.plexus.util.StringUtils;

@UtilityClass
public class StringUtilities {
  private static final String UTF_8 = StandardCharsets.UTF_8.name();

  @SneakyThrows(UnsupportedEncodingException.class)
  public static String urlEncode(String str) {
    return URLEncoder.encode(str, UTF_8);
  }

  public static String urlEncodePath(Path path) {
    return StringUtils.replace(urlEncode(path.toString()), "%5C", "\\");
  }
}
