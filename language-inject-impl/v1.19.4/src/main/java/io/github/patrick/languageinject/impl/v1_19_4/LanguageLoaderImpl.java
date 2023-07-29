package io.github.patrick.languageinject.impl.v1_19_4;

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

    @Override
    public void loadLanguage(String language) {
        File rootFolder = LanguageInjectPlugin.instance.getDataFolder();
        File file = LanguageLoaderUtil.loadLanguageFile(rootFolder, SharedConstants.getCurrentVersion().getName(), language);
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        try (InputStream stream = FileUtils.openInputStream(file)) {
            Language.loadFromJson(stream, builder::put);
        } catch (JsonParseException | IOException exception) {
            LogManager.getLogger().error("Couldn't read strings from {}", file.getPath(), exception);
        }

        final Map<String, String> map = builder.build();

        Language.inject(new Language() {
            public String getOrDefault(String key, String fallback) {
                return map.getOrDefault(key, fallback);
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
