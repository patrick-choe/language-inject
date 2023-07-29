package io.github.patrick.languageinject;

import org.bukkit.Bukkit;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Runtime.Version;

public class LanguageManager {
    private static final Version[] updateVersions;
    private static ILanguageLoader LOADER = null;

    static {
        updateVersions = new Version[]{Version.parse("1.19.4"), Version.parse("1.18")};
    }

    static ILanguageLoader getInstance() {
        if (LOADER != null) {
            return LOADER;
        }


        String minecraftVersion = getMinecraftVersion();
        Version version = Version.parse(minecraftVersion);
        Version selected = null;

        for (Version updateVersion : updateVersions) {
            if (version.compareTo(updateVersion) >= 0) {
                selected = updateVersion;
                break;
            }
        }

        if (selected != null) {
            try {
                String className = "io.github.patrick.languageinject.impl.v" + selected.toString().replace('.', '_') + ".LanguageLoaderImpl";
                Class<? extends ILanguageLoader> clazz = Class.forName(className, true, ILanguageLoader.class.getClassLoader()).asSubclass(ILanguageLoader.class);
                LOADER = clazz.getConstructor().newInstance();

                return LOADER;
            } catch (Throwable throwable) {
                throw new RuntimeException("Unable to initialize LanguageLoader.", throwable);
            }
        } else {
            throw new UnsupportedOperationException("Version '" + version + "' is not supported.");
        }
    }

    private static String getMinecraftVersion() {
        Matcher matcher = Pattern.compile("(?<=\\(MC: )[\\d.]+?(?=\\))").matcher(Bukkit.getVersion());

        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new NoSuchElementException("No minecraft version found.");
        }
    }
}
