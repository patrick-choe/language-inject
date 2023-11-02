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

package io.github.patrick.languageinject.impl.v1_18;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import io.github.patrick.languageinject.ILanguageLoader;
import io.github.patrick.languageinject.LanguageInjectPlugin;
import io.github.patrick.languageinject.impl.LanguageLoaderUtil;
import net.minecraft.SharedConstants;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class LanguageLoaderImpl implements ILanguageLoader {
    Language defaultLanguage = Language.getInstance();
    private static final String DEFAULT_LANGUAGE_NAME = "en_us";

    @Override
    public void loadLanguage(String language) {
        if (language.equals(DEFAULT_LANGUAGE_NAME)) {
            Language.inject(defaultLanguage);
            return;
        }

        File dataFolder = LanguageInjectPlugin.instance.getDataFolder();
        File file = LanguageLoaderUtil.loadLanguageFile(dataFolder, SharedConstants.getCurrentVersion().getName(), language);
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        try (InputStream stream = FileUtils.openInputStream(file)) {
            Language.loadFromJson(stream, builder::put);
        } catch (JsonParseException | IOException exception) {
            LogManager.getLogger().error("Couldn't read strings from {}", file.getPath(), exception);
        }

        final Map<String, String> map = builder.build();

        Language.inject(new Language() {
            public String getOrDefault(String key) {
                return map.getOrDefault(key, key);
            }

            public boolean has(String key) {
                return map.containsKey(key);
            }

            public boolean isDefaultRightToLeft() {
                return LanguageLoaderUtil.isDefaultRightToLeft(language);
            }

            public FormattedCharSequence getVisualOrder(FormattedText text) {
                return (visitor) -> text.visit((style, string) -> StringDecomposer.iterateFormatted(string, style, visitor) ? Optional.empty() : FormattedText.STOP_ITERATION, Style.EMPTY).isPresent();
            }
        });
    }

    @Override
    public void unloadLanguage() {
        Language.inject(defaultLanguage);
    }
}