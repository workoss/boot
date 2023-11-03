package com.workoss.boot.ocr;

import lombok.Data;

/**
 * @author workoss
 */
@Data
public class OcrConfig {

    /**
     * CPU 核心数量，默认 1
     */
    private int numThread = 1;



    public OcrConfig(int numThread) {
        this.numThread = numThread;
    }

}
