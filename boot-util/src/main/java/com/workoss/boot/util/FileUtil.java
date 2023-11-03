package com.workoss.boot.util;

import com.workoss.boot.util.collection.Pair;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author workoss
 */
public class FileUtil {
    private static final String JAR_PROTOCAL = "jar";
    private static final Lazy<PathMatcher> SUB_PATTERN_LAZY = Lazy
            .of(() -> FileSystems.getDefault().getPathMatcher("glob:**/*"));


    public static List<Pair<String, InputStream>> findFiles(ClassLoader classLoader, String sourceDir) {
        if (classLoader == null) {
            classLoader = FileUtil.class.getClassLoader();
        }
        if (!sourceDir.endsWith("/")){
            sourceDir = sourceDir+"/";
        }
        URL url = classLoader.getResource(sourceDir);
        if (url != null && JAR_PROTOCAL.equalsIgnoreCase(url.getProtocol())) {
            return readFromJar(url, sourceDir);
        } else {
            return readFromResource(url, sourceDir);
        }
    }

    private static List<Pair<String, InputStream>> readFromResource(URL url, String sourceDir) {
        try (Stream<Path> paths = Files
                .find(Paths.get(url.toURI()), 5, (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())
                .filter(path -> SUB_PATTERN_LAZY.get().matches(path))) {
            return paths.map(path -> {
                File file = path.toFile();
                try {
                    InputStream fileInputStream = new FileInputStream(path.toFile());
                    return Pair.of(file.getName(), fileInputStream);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Pair<String, InputStream>> readFromJar(URL url, String sourceDir) {
        List<Pair<String, InputStream>> fileInfos = new ArrayList<>();
        try {
            URLConnection con = url.openConnection();
            JarURLConnection jarCon = (JarURLConnection) con;
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> enumeration = jarFile.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                String filePattern = entry.getName();
                if (!filePattern.startsWith(sourceDir)) {
                    continue;
                }
                Path path = Paths.get(filePattern);
                if (!SUB_PATTERN_LAZY.get().matches(path)) {
                    continue;
                }
                InputStream inputStream = jarFile.getInputStream(entry);
                fileInfos.add(Pair.of(filePattern.replace(sourceDir,StringUtils.EMPTY), inputStream));
            }
        } catch (Exception ignored) {
        }
        return fileInfos;
    }

}
