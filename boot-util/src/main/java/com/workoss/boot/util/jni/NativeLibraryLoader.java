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
package com.workoss.boot.util.jni;

import com.workoss.boot.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class NativeLibraryLoader {

	private static final Logger log = LoggerFactory.getLogger(NativeLibraryLoader.class);

	private static final Lazy<NativeLibraryLoader> INSTANCE = Lazy.of(NativeLibraryLoader::new);

	private static boolean initialized = false;

	private NativeLibraryLoader() {
	}

	public static NativeLibraryLoader getInstance() {
		return INSTANCE.get();
	}

	public synchronized void loadLibrary(final String libName) throws IOException {
		loadLibrary(NativeLibraryLoader.class.getClassLoader(), libName);
	}

	public synchronized void loadLibrary(final ClassLoader classLoader, final String libName) throws IOException {
		String strTmp = System.getProperty("java.io.tmpdir");
		loadLibrary(classLoader, strTmp, libName);
	}

	public synchronized void loadLibrary(final ClassLoader classLoader, final String tmpDir, final String libName)
			throws IOException {
		String jniLibraryName = Environment.getJniLibraryFileName(libName);
		String fallbackJniLibraryName = Environment.getFallbackJniLibraryFileName(libName);
		// dynamic library , static library , static library fallback
		for (String libraryName : Arrays.asList(jniLibraryName, fallbackJniLibraryName)) {
			try {
				System.loadLibrary(libraryName);
				log.info("[LIB] load {}:{} success", libName, libraryName);
				return;
			}
			catch (UnsatisfiedLinkError e) {
				log.warn("[LIB] load {}:{} error:{}", libName, libraryName, e.getMessage());
				// ignore -
			}
		}
		// jar
		loadLibrary(classLoader, tmpDir, Environment.getJniLibraryFileName(libName),
				Environment.getFallbackJniLibraryFileName(libName));

	}

	void loadLibrary(final ClassLoader classLoader, final String tmpDir, final String jniLibraryFileName,
			final String fallbackJniLibraryFileName) throws IOException {
		if (initialized) {
			return;
		}
		String libraryFilePath = loadLibraryFile(classLoader, tmpDir, jniLibraryFileName, fallbackJniLibraryFileName)
			.getAbsolutePath();
		System.load(libraryFilePath);
		log.info("[LIB] load {} success", libraryFilePath);
		initialized = true;
	}

	File loadLibraryFile(ClassLoader classLoader, final String tmpDir, final String jniLibraryFileName,
			final String fallbackJniLibraryFileName) throws IOException {
		InputStream inputStream = null;
		String libraryFileName = jniLibraryFileName;
		if (classLoader == null) {
			classLoader = NativeLibraryLoader.class.getClassLoader();
		}
		try {
			inputStream = classLoader.getResourceAsStream(libraryFileName);
			if (inputStream == null) {
				if (fallbackJniLibraryFileName == null) {
					throw new RuntimeException(libraryFileName + " was not found inside JAR.");
				}
				libraryFileName = fallbackJniLibraryFileName;
				inputStream = classLoader.getResourceAsStream(libraryFileName);
				if (fallbackJniLibraryFileName == null) {
					throw new RuntimeException(libraryFileName + " was not found inside JAR.");
				}
			}
			File temp;
			if (tmpDir == null || tmpDir.isEmpty()) {
				temp = File.createTempFile(libraryFileName, Environment.getJniLibSuffix());
			}
			else {
				final File parentDir = new File(tmpDir);
				if (!parentDir.exists()) {
					throw new RuntimeException("Directory: " + parentDir.getAbsolutePath() + " does not exist!");
				}
				temp = new File(parentDir, libraryFileName);
				if (temp.exists() && !temp.delete()) {
					throw new RuntimeException(
							"File: " + temp.getAbsolutePath() + " already exists and cannot be removed.");
				}
				if (!temp.createNewFile()) {
					throw new RuntimeException("File: " + temp.getAbsolutePath() + " could not be created.");
				}
			}
			if (!temp.exists()) {
				throw new RuntimeException("File " + temp.getAbsolutePath() + " does not exist.");
			}
			else {
				temp.deleteOnExit();
			}
			// copy the library from the Jar file to the temp destination
			Files.copy(inputStream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			// return the temporary library file
			return temp;

		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

}
