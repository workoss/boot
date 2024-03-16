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
package com.workoss.boot.ocr;

import com.benjaminwan.ocrlibrary.OcrResult;
import com.workoss.boot.util.FileUtil;
import com.workoss.boot.util.collection.Pair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author workoss
 */
class OcrUtilTest {

    @Test
    void newOnnxBuild() {
//        List<Pair<String, InputStream>> files = FileUtil.findFiles(OcrUtilTest.class.getClassLoader(), "onnx/models/");
//        for (Pair<String, InputStream> file : files) {
//            System.out.println(file.getFirst());
//        }

        OcrResult detect = OcrUtil.newOnnxBuild(null)
                .detect(getResourcePath("/40.png"), null);
        System.out.println(detect);
    }

    @Test
    void newNcnnBuild() {
        OcrResult detect = OcrUtil.newNcnnBuild(null)
                .detect(getResourcePath("/40.png"), null);
        System.out.println(detect);
    }

    private static String getResourcePath(String path) {
        return new File(OcrUtilTest.class.getResource(path).getFile()).toString();
    }
}