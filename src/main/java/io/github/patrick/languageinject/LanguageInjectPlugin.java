package io.github.patrick.languageinject;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class LanguageInjectPlugin extends JavaPlugin {
    public static JavaPlugin instance;
    private ILanguageLoader loader;

    @Override
    public void onLoad() {
        instance = this;
        loader = LanguageManager.getInstance();

        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        String language = getConfig().getString("language", "en_us");
        language = language.toLowerCase(Locale.ROOT).replace('-', '_');
        loader.loadLanguage(language);
    }

    @Override
    public void onDisable() {
        loader.unloadLanguage();
    }
}