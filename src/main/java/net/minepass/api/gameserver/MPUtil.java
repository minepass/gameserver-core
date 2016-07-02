package net.minepass.api.gameserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MPUtil {
    private static Pattern versionPattern = Pattern.compile("([0-9]+)(\\.[0-9]+)+([\\.-][0-9a-zA-Z]+)?");

    public static String parseVersion(String input) {
        Matcher m = versionPattern.matcher(input);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }
}
