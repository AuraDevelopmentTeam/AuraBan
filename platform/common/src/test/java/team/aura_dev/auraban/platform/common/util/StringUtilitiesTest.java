package team.aura_dev.auraban.platform.common.util;

import static org.junit.Assert.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

@SuppressFBWarnings(
  value = "DMI_HARDCODED_ABSOLUTE_FILENAME",
  justification = "Path is hard coded for testing of absolute Windows paths"
)
public class StringUtilitiesTest {
  public static final Path SIMPLE_PATH = Paths.get("foo", "bar", "foobar");
  public static final Path COMPLICATED_PATH = Paths.get("test", "%~;äöé漢");
  public static final Path WINDOWS_PATH = Paths.get("C:\\test folder\\äöé漢");

  @Test
  public void urlEncodeTest() {
    assertEquals(
        "test%40%3A%C3%A4%C3%B6%C3%BC%26%3F+%40test",
        StringUtilities.urlEncode("test@:äöü&? @test"));
  }

  @Test
  public void urlEncodePathTest() {
    assertEquals("foo/bar/foobar", StringUtilities.urlEncodePath(SIMPLE_PATH).replace('\\', '/'));
    assertEquals(
        "test/%25%7E%3B%C3%A4%C3%B6%C3%A9%E6%BC%A2",
        StringUtilities.urlEncodePath(COMPLICATED_PATH).replace('\\', '/'));
    assertEquals(
        "C:\\test+folder\\%C3%A4%C3%B6%C3%A9%E6%BC%A2",
        StringUtilities.urlEncodePath(WINDOWS_PATH));
  }
}
