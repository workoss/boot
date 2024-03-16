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

import java.util.ArrayList;

public class OcrResult implements OcrOutput {
    private final double dbNetTime;
    private final ArrayList<TextBlock> textBlocks;
    private double detectTime;
    private String strRes;

    public OcrResult(double dbNetTime, ArrayList<TextBlock> textBlocks, double detectTime, String strRes) {
        this.dbNetTime = dbNetTime;
        this.textBlocks = textBlocks;
        this.detectTime = detectTime;
        this.strRes = strRes;
    }

    public double getDbNetTime() {
        return dbNetTime;
    }

    public ArrayList<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public double getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(double detectTime) {
        this.detectTime = detectTime;
    }

    public String getStrRes() {
        return strRes;
    }

    public void setStrRes(String strRes) {
        this.strRes = strRes;
    }

    @Override
    public String toString() {
        return "OcrResult{" +
                "dbNetTime=" + dbNetTime +
                ", textBlocks=" + textBlocks +
                ", detectTime=" + detectTime +
                ", strRes='" + strRes + '\'' +
                '}';
    }
}
