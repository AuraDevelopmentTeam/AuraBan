package team.aura_dev.auraban.platform.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.codehaus.plexus.util.StringUtils;

@UtilityClass
public class StringUtilities {
  private static final String UTF_8 = StandardCharsets.UTF_8.name();
  private static final Pattern WINDOWS_DRIVE_LETTER = Pattern.compile("^([A-Z])%3A");

  @SneakyThrows(UnsupportedEncodingException.class)
  public static String urlEncode(String str) {
    return URLEncoder.encode(str, UTF_8);
  }

  public static String urlEncodePath(Path path) {
    final String halfFixed = StringUtils.replace(urlEncode(path.toString()), "%5C", "\\");
    final Matcher matcher = WINDOWS_DRIVE_LETTER.matcher(halfFixed);

    return matcher.replaceAll("$1:");
  }
}
