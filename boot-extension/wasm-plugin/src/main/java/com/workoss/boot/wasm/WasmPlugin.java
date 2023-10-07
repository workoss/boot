/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.wasm;

import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.util.Lazy;
import com.workoss.boot.util.NativeLibraryLoader;
import com.workoss.boot.util.StreamUtils;
import com.workoss.boot.util.exception.ExceptionUtils;
import com.workoss.boot.util.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public final class WasmPlugin implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(WasmPlugin.class);
    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final Map<String, byte[]> PLUGIN_MAP = new ConcurrentHashMap<>();

    private static final Lazy<WasmPlugin> WASM_PLUGIN_LAZY = Lazy.of(() -> new WasmPlugin());


    private WasmPlugin() {
        try {
            NativeLibraryLoader.getInstance().loadLibrary("wasm_plugin");
        } catch (IOException e) {
            throw new WasmPluginException("load lib error");
        }
    }

    public static JsonNode execute(String pluginPath, JsonNode input, JsonNode context) throws WasmPluginException {
        return WASM_PLUGIN_LAZY.get().run(pluginPath, input, context);
    }

    public static JsonNode execute(byte[] pluginBytes, JsonNode input, JsonNode context) throws WasmPluginException {
        return WASM_PLUGIN_LAZY.get().run(pluginBytes, input, context);
    }

    private JsonNode run(String pluginPath, JsonNode input, JsonNode context) throws WasmPluginException {
        byte[] pluginBytes = PLUGIN_MAP.get(pluginPath);
        if (pluginBytes == null) {
            pluginBytes = readPlugin(pluginPath);
        }
        return run(pluginBytes, input, context);
    }

    private JsonNode run(byte[] pluginBytes, JsonNode input, JsonNode context) throws WasmPluginException {
        byte[] resultBytes = WasmPluginLoader.run(pluginBytes, JsonMapper.toJSONBytes(input), context == null ? EMPTY_BYTES : JsonMapper.toJSONBytes(context));
        if (resultBytes == null) {
            return null;
        }
        JsonNode response = JsonMapper.parse(resultBytes);
        log.atDebug().log("[WASM_PLUGIN] INPUT:{} OUTPUT:{}", input, response);
        return response;
    }


    private static byte[] readPlugin(String pluginPath) {
        Path path = Paths.get(pluginPath);
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile());) {
            byte[] bytes = StreamUtils.copyToByteArray(fileInputStream);
            PLUGIN_MAP.put(pluginPath, bytes);
            return bytes;
        } catch (FileNotFoundException e) {
            throw new WasmPluginException("-4", "not found plugin file:" + pluginPath);
        } catch (IOException e) {
            throw new WasmPluginException("-4", "load plugin file:" + pluginPath + " " + ExceptionUtils.toString(e));
        }
    }

    @Override
    public void close() throws Exception {

    }
}
