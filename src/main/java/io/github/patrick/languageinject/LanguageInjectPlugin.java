/*
 * Copyright (C) 2023 PatrickKR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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