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

package io.github.patrick.languageinject.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class LanguageLoaderUtil {
    private static final String VERSION_MANIFEST_V2_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final String RESOURCE_BASE_URL = "https://resources.download.minecraft.net/";
    private static final List<String> bidirectionalLanguages = Arrays.asList("ar_sa", "fa_ir", "he_il", "yi_de", "zlm_arab");

    public static boolean isDefaultRightToLeft(String language) {
        return bidirectionalLanguages.contains(language);
    }

    public static File loadLanguageFile(File dataFolder, String version, String language) {
        File versionFile = new File(dataFolder, "version/" + version + ".json");
        File languageFile = new File(dataFolder, "language/" + version + "-" + language + ".json");

        try {
            FileUtils.createParentDirectories(versionFile);
            FileUtils.createParentDirectories(languageFile);
        } catch (IOException exception) {
            throw new RuntimeException("Cannot create directory at data folder.", exception);
        }

        if (!versionFile.exists()) {
            downloadVersionFile(versionFile, version);
        }

        if (!languageFile.exists()) {
            downloadLanguageFile(languageFile, versionFile, language);
        }

        return languageFile;
    }

    private static void downloadLanguageFile(File languageFile, File versionFile, String language) {
        JsonObject assets;

        try (InputStream inputStream = FileUtils.openInputStream(versionFile)) {
            assets = copyStreamToJsonObject(inputStream).getAsJsonObject("objects");
        } catch (IOException exception) {
            throw new RuntimeException("Cannot open stream from file '" + versionFile.getPath() + "'.", exception);
        }

        String path = "minecraft/lang/" + language + ".json";

        if (assets.has(path)) {
            String hash = assets.getAsJsonObject(path).getAsJsonPrimitive("hash").getAsString();
            String url = RESOURCE_BASE_URL + "/" + hash.substring(0, 2) + "/" + hash;

            downloadFile(url, languageFile);
        } else {
            throw new RuntimeException("Cannot find language named '" + language + "'.");
        }
    }

    private static void downloadVersionFile(File versionFile, String version) {
        JsonObject manifest = copyURLToJsonObject(VERSION_MANIFEST_V2_URL);

        for (JsonElement element : manifest.getAsJsonArray("versions")) {
            JsonObject object = element.getAsJsonObject();

            if (object.get("id").getAsString().equals(version)) {
                JsonObject versionJson = copyURLToJsonObject(object.get("url").getAsString());
                String url = versionJson.getAsJsonObject("assetIndex").getAsJsonPrimitive("url").getAsString();
                downloadFile(url, versionFile);

                return;
            }
        }

        throw new NoSuchElementException("No matching assets found for version '" + version + "'.");
    }

    private static void downloadFile(String url, File file) {
        try {
            FileUtils.copyURLToFile(new URL(url), file);
        } catch (IOException exception) {
            throw new RuntimeException("Cannot copy url '" + url + "' into file '" + file.getPath() + "'.", exception);
        }
    }

    private static JsonObject copyURLToJsonObject(String url) {
        try (InputStream inputStream = new URL(url).openStream()) {
            return copyStreamToJsonObject(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException("Cannot open stream from url '" + url + "'.", exception);
        }
    }

    private static JsonObject copyStreamToJsonObject(InputStream stream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            return JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        }
    }
}