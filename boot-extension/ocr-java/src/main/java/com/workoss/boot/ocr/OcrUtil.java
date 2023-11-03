package com.workoss.boot.ocr;

import com.benjaminwan.ocrlibrary.OcrEngine;
import com.benjaminwan.ocrlibrary.OcrResult;
import com.benjaminwan.ocrlibrary.OcrType;
import com.workoss.boot.util.FileUtil;
import com.workoss.boot.util.collection.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author workoss
 */
public class OcrUtil {


    private OcrUtil() {
    }

    public static OcrEngineBuild newOnnxBuild(OcrConfig ocrConfig) {
        return new OcrEngineBuild(OcrType.ONNX, ocrConfig);
    }

    public static OcrEngineBuild newNcnnBuild(OcrConfig ocrConfig) {
        return new OcrEngineBuild(OcrType.NCNN, ocrConfig);
    }

    public static OcrEngineBuild newBuild(OcrType ocrType, OcrConfig ocrConfig) {
        return new OcrEngineBuild(ocrType, ocrConfig);
    }

    @Slf4j
    public static class OcrEngineBuild {

        private static final Map<OcrType, OcrEngine> OCR_MAP = new ConcurrentHashMap<>();

        private OcrType ocrType;

        private OcrConfig ocrConfig;


        public OcrEngineBuild(OcrType ocrType, OcrConfig ocrConfig) {
            this.ocrType = ocrType;
            this.ocrConfig = ocrConfig;
        }

        private OcrEngine getEngine() {
            OcrEngine ocrEngine = OCR_MAP.get(ocrType);
            if (ocrEngine != null) {
                return ocrEngine;
            }
            ocrEngine = new OcrEngine(ocrType);
            ocrEngine.initLogger(false, false, false);
            if (ocrConfig == null) {
                int availableProcessors = Runtime.getRuntime().availableProcessors();
                if (availableProcessors <= 2) {
                    availableProcessors = 2;
                }
                ocrConfig = new OcrConfig(availableProcessors);
            }
            ocrEngine.setNumThread(ocrConfig.getNumThread());
            //移动模型到 临时目录
            String libPath = ocrEngine.getLibPath();
            copyModelsToTemp(ocrType.name().toLowerCase() + "/models", libPath);
            switch (ocrType) {
                case ONNX -> {
                    if (!ocrEngine.initModels(libPath, "ch_PP-OCRv4_det_infer.onnx",
                            "ch_ppocr_mobile_v2.0_cls_infer.onnx", "ch_PP-OCRv4_rec_infer.onnx",
                            "ppocr_keys_v1.txt")) {
                        throw new IllegalArgumentException("模型初始化错误，请检查models/keys路径！");
                    }
                }
                case NCNN -> {
                    ocrEngine.setGpuIndex(0);
                    if (!ocrEngine.initModels(libPath, "ch_PP-OCRv3_det_infer",
                            "ch_ppocr_mobile_v2.0_cls_infer", "ch_PP-OCRv3_rec_infer",
                            "ppocr_keys_v1.txt")) {
                        throw new IllegalArgumentException("模型初始化错误，请检查models/keys路径！");
                    }
                }
            }
            log.atInfo().log("[OCR] INIT ENGINE:ONNX version:{}", ocrEngine.getVersion());
            OCR_MAP.put(OcrType.ONNX, ocrEngine);
            return ocrEngine;
        }

        private void copyModelsToTemp(String soucePath, String tempPath) {
            List<Pair<String, InputStream>> pairs = FileUtil.findFiles(OcrEngine.class.getClassLoader(), soucePath);
            File parentFile = new File(tempPath);
            if (!parentFile.exists()) {
                throw new IllegalArgumentException("动态链接库目录不存在");
            }
            try {
                for (Pair<String, InputStream> pair : pairs) {
                    File targetFile = new File(parentFile, pair.getFirst());
                    Files.copy(pair.getSecond(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        public OcrResult detect(String imagePath, DetectConfig detectConfig) {
            if (detectConfig == null) {
                detectConfig = new DetectConfig();
            }
            return detect(imagePath, detectConfig.getPadding(), detectConfig.getMaxSideLen(),
                    detectConfig.getBoxScoreThresh(), detectConfig.getBoxThresh(),
                    detectConfig.getUnClipRatio(), detectConfig.isDoAngle(), detectConfig.isMostAngle());
        }

        public OcrResult detect(String imagePath, int padding, int maxSideLen,
                                float boxScoreThresh, float boxThresh,
                                float unClipRatio, boolean doAngle, boolean mostAngle) {
            return getEngine().detect(imagePath, padding, maxSideLen, boxScoreThresh, boxThresh, unClipRatio, doAngle, mostAngle);
        }


    }


}
