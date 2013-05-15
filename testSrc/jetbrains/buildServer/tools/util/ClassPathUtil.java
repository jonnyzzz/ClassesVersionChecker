package jetbrains.buildServer.tools.util;

/**
 * Created 15.05.13 16:18
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * User: Eugene.Petrenko
 * Date: 13.02.2007
 * Time: 18:28:18
 */
public class ClassPathUtil {
  @Nullable
  public static File getClassFile(@NotNull final Class<?> clazz) throws IOException {
    String path = "/" + clazz.getName().replace('.', '/') + ".class";
    final URL url = clazz.getResource(path);
    if (url == null) {
      return null;
    }

    String urlStr = URLDecoder.decode(url.toExternalForm().replace("+", "%2B"), "UTF-8");
    int startIndex = urlStr.indexOf(':');
    while (startIndex >= 0 && urlStr.charAt(startIndex + 1) != '/') {
      startIndex = urlStr.indexOf(':', startIndex + 1);
    }
    if (startIndex >= 0) {
      urlStr = urlStr.substring(startIndex + 1);
    }

    if (urlStr.startsWith("/") && urlStr.indexOf(":") == 2) {
      urlStr = urlStr.substring(1);
    }

    return new File(urlStr);
  }
}