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
package com.workoss.boot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class Environment {

	private static final String[] LINUX_OS_RELEASE_FILES = { "/etc/os-release", "/usr/lib/os-release" };

	private static final Pattern REDHAT_MAJOR_VERSION_REGEX = Pattern.compile("(\\d+)");

	private static final Lazy<String> OS_NAME = Lazy.of(() -> {
		return normalizeOs(System.getProperty("os.name"));
	});

	private static final Lazy<String> OS_ARCH = Lazy.of(() -> {
		return normalizeArch(System.getProperty("os.arch"));
	});

	public static String getJniLibraryFileName(final String name) {
		final String osVersion = System.getProperty("os.version");
		final String detectedName = OS_NAME.get();
		final String detectedArch = OS_ARCH.get();
		if (StringUtils.isBlank(detectedName) || StringUtils.isBlank(detectedArch)) {
			return null;
		}
		return String.format("%s%s-%s-%s%s", getJniLibPrefix(detectedName), name, detectedName, detectedArch,
				getJniLibSuffix());
	}

	public static String getFallbackJniLibraryFileName(final String name) {
		final String detectedName = OS_NAME.get();
		if (StringUtils.isBlank(detectedName)) {
			return null;
		}
		String libPrefix = ("windows".equals(detectedName) ? "" : "lib");
		return String.format("%s%s-%s%s", getJniLibPrefix(detectedName), name, detectedName, getJniLibSuffix());
	}

	private static String getJniLibPrefix(String detectedName) {
		return ("windows".equals(detectedName) ? "" : "lib");
	}

	public static String getJniLibSuffix() {
		final String detectedName = OS_NAME.get();
		if (StringUtils.isBlank(detectedName)) {
			return "";
		}
		switch (detectedName) {
			case "windows" -> {
				return ".dll";
			}
			case "osx" -> {
				return ".dylib";
			}
		}
		return ".so";
	}

	private static String normalize(String value) {
		if (value == null) {
			return "";
		}
		return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
	}

	private static String normalizeOs(String value) {
		value = normalize(value);
		if (value.startsWith("aix")) {
			return "aix";
		}
		if (value.startsWith("hpux")) {
			return "hpux";
		}
		if (value.startsWith("os400")) {
			// Avoid the names such as os4000
			if (value.length() <= 5 || !Character.isDigit(value.charAt(5))) {
				return "os400";
			}
		}
		if (value.startsWith("linux")) {
			return "linux";
		}
		if (value.startsWith("mac") || value.startsWith("osx")) {
			return "osx";
		}
		if (value.startsWith("freebsd")) {
			return "freebsd";
		}
		if (value.startsWith("openbsd")) {
			return "openbsd";
		}
		if (value.startsWith("netbsd")) {
			return "netbsd";
		}
		if (value.startsWith("solaris") || value.startsWith("sunos")) {
			return "sunos";
		}
		if (value.startsWith("windows")) {
			return "windows";
		}
		if (value.startsWith("zos")) {
			return "zos";
		}

		return "unknown";
	}

	private static String normalizeArch(String value) {
		value = normalize(value);
		if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
			return "x86_64";
		}
		if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
			return "x86_32";
		}
		if (value.matches("^(ia64w?|itanium64)$")) {
			return "itanium_64";
		}
		if ("ia64n".equals(value)) {
			return "itanium_32";
		}
		if (value.matches("^(sparc|sparc32)$")) {
			return "sparc_32";
		}
		if (value.matches("^(sparcv9|sparc64)$")) {
			return "sparc_64";
		}
		if (value.matches("^(arm|arm32)$")) {
			return "arm_32";
		}
		if (value.matches("^(arm64)$")) {
			return "aarch_64";
		}
		if ("aarch64".equals(value)) {
			return "aarch_64";
		}
		if (value.matches("^(mips|mips32)$")) {
			return "mips_32";
		}
		if (value.matches("^(mipsel|mips32el)$")) {
			return "mipsel_32";
		}
		if ("mips64".equals(value)) {
			return "mips_64";
		}
		if ("mips64el".equals(value)) {
			return "mipsel_64";
		}
		if (value.matches("^(ppc|ppc32)$")) {
			return "ppc_32";
		}
		if (value.matches("^(ppcle|ppc32le)$")) {
			return "ppcle_32";
		}
		if ("ppc64".equals(value)) {
			return "ppc_64";
		}
		if ("ppc64le".equals(value)) {
			return "ppcle_64";
		}
		if ("s390".equals(value)) {
			return "s390_32";
		}
		if ("s390x".equals(value)) {
			return "s390_64";
		}
		if (value.matches("^(riscv|riscv32)$")) {
			return "riscv";
		}
		if ("riscv64".equals(value)) {
			return "riscv64";
		}
		if ("e2k".equals(value)) {
			return "e2k";
		}
		if ("loongarch64".equals(value)) {
			return "loongarch_64";
		}
		return "unknown";
	}

	private LinuxRelease getLinuxRelease() {
		// First, look for the os-release file.
		for (String osReleaseFileName : LINUX_OS_RELEASE_FILES) {
			LinuxRelease res = parseLinuxOsReleaseFile(osReleaseFileName);
			if (res != null) {
				return res;
			}
		}
		// Older versions of redhat don't have /etc/os-release. In this case, try
		// parsing this file.
		return parseLinuxRedhatReleaseFile("/etc/redhat-release");
	}

	private LinuxRelease parseLinuxOsReleaseFile(String fileName) {

		try (InputStream in = new FileInputStream(fileName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String id = null;
			String version = null;
			final Set<String> likeSet = new LinkedHashSet<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				// Parse the ID line.
				if (line.startsWith("ID=")) {
					// Set the ID for this version.
					id = normalizeOsReleaseValue(line.substring("ID_LIKE=".length()));

					// Also add the ID to the "like" set.
					likeSet.add(id);
					continue;
				}

				// Parse the VERSION_ID line.
				if (line.startsWith("VERSION_ID=")) {
					// Set the ID for this version.
					version = normalizeOsReleaseValue(line.substring("VERSION_ID=".length()));
					continue;
				}

				// Parse the ID_LIKE line.
				if (line.startsWith("ID_LIKE=")) {
					line = normalizeOsReleaseValue(line.substring("ID_LIKE=".length()));

					// Split the line on any whitespace.
					final String[] parts = line.split("\\s+");
					Collections.addAll(likeSet, parts);
				}
			}

			if (id != null) {
				return new LinuxRelease(id, version, likeSet);
			}
		}
		catch (IOException ignored) {
			// Just absorb. Don't treat failure to read /etc/os-release as an error.
		}
		return null;
	}

	private LinuxRelease parseLinuxRedhatReleaseFile(String fileName) {
		try (InputStream in = new FileInputStream(fileName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			// There is only a single line in this file.
			String line = reader.readLine();
			if (line == null) {
				return null;
			}

			line = line.toLowerCase(Locale.US);

			final String id;
			String version = null;
			if (line.contains("centos")) {
				id = "centos";
			}
			else if (line.contains("fedora")) {
				id = "fedora";
			}
			else if (line.contains("red hat enterprise linux")) {
				id = "rhel";
			}
			else {
				// Other variants are not currently supported.
				return null;
			}

			final Matcher versionMatcher = REDHAT_MAJOR_VERSION_REGEX.matcher(line);
			if (versionMatcher.find()) {
				version = versionMatcher.group(1);
			}

			final Set<String> likeSet = new LinkedHashSet<String>(Arrays.asList("rhel", "fedora"));
			likeSet.add(id);
			return new LinuxRelease(id, version, likeSet);
		}
		catch (IOException ignored) {
			// Just absorb. Don't treat failure to read /etc/os-release as an error.
		}
		return null;
	}

	private static String normalizeOsReleaseValue(String value) {
		// Remove any quotes from the string.
		return value.trim().replace("\"", "");
	}

	private static class LinuxRelease {

		final String id;

		final String version;

		final Collection<String> like;

		LinuxRelease(String id, String version, Set<String> like) {
			this.id = id;
			this.version = version;
			this.like = Collections.unmodifiableCollection(like);
		}

	}

}
