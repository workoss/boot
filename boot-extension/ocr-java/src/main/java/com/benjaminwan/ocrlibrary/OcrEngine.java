package com.benjaminwan.ocrlibrary;

import com.workoss.boot.util.jni.NativeLibraryLoader;

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
