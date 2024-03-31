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
package com.benjaminwan.ocrlibrary;

import com.workoss.boot.util.jni.NativeLibraryLoader;
import io.github.workoss.jni.JniLibLoader;

import java.io.IOException;

/**
 * OCR引擎对象，负责接收参数并执行OCR
 */
public class OcrEngine {

    public static String libPath;

    public  String getLibPath() {
        return libPath;
    }

    public OcrEngine(OcrType ocrType) {
        //加载lib
        try {
            String strTmp = System.getProperty("java.io.tmpdir");
            libPath = strTmp+ocrType.name().toLowerCase();
//            JniLibLoader.getInstance().loadLibrary(OcrEngine.class.getClassLoader(),"rapid-ocr",false);
            NativeLibraryLoader.getInstance()
                    .loadLibrary(OcrEngine.class.getClassLoader(),strTmp,ocrType.name().toLowerCase(),"rapid_ocr");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public native boolean setNumThread(int numThread);

    public native void initLogger(boolean isConsole, boolean isPartImg, boolean isResultImg);

    public native void enableResultText(String imagePath);

    public native boolean initModels(String modelsDir, String detName, String clsName, String recName, String keysName);

    public native void setGpuIndex(int gpuIndex);

    public native String getVersion();

    public native OcrResult detect(
            String input, int padding, int maxSideLen,
            float boxScoreThresh, float boxThresh,
            float unClipRatio, boolean doAngle, boolean mostAngle
    );
}
