/**
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
#include <jni.h>
/* Header for class com_workoss_boot_engine_ZenEngineLoader */

#ifndef _Included_com_workoss_boot_engine_ZenEngineLoader
#define _Included_com_workoss_boot_engine_ZenEngineLoader
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_workoss_boot_engine_ZenEngineLoader
 * Method:    evaluate
 * Signature: ([B[BZI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_workoss_boot_engine_ZenEngineLoader_evaluate
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jboolean, jint);

/*
 * Class:     com_workoss_boot_engine_ZenEngineLoader
 * Method:    validate
 * Signature: ([B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_workoss_boot_engine_ZenEngineLoader_validate
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     com_workoss_boot_engine_ZenEngineLoader
 * Method:    expression
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_workoss_boot_engine_ZenEngineLoader_expression
  (JNIEnv *, jclass, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
