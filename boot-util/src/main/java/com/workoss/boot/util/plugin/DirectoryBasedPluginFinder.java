package com.workoss.boot.util.plugin;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PluginFinder 默认实现
 *
 * @author workoss
 */
public class DirectoryBasedPluginFinder implements PluginFinder {

	/** Pattern to match jar files in a directory. */
	private static final String JAR_MATCHER_PATTERN = "glob:**.jar";

	/** Root directory to the plugin folders. */
	private final Path pluginsRootDir;

	/** Matcher for jar files in the filesystem of the root folder. */
	private final PathMatcher jarFileMatcher;

	public DirectoryBasedPluginFinder(Path pluginsRootDir) {
		this.pluginsRootDir = pluginsRootDir;
		this.jarFileMatcher = pluginsRootDir.getFileSystem().getPathMatcher(JAR_MATCHER_PATTERN);
	}

	@Override
	public Collection<PluginDescriptor> findPlugins() throws IOException {

		if (!Files.isDirectory(pluginsRootDir)) {
			throw new IOException("Plugins root directory [" + pluginsRootDir + "] does not exist!");
		}

		return Files.list(pluginsRootDir).filter((Path path) -> Files.isDirectory(path))
				.map(uncheckedFunction(this::createPluginDescriptorForSubDirectory)).collect(Collectors.toList());
	}

	private PluginDescriptor createPluginDescriptorForSubDirectory(Path subDirectory) throws IOException {
		URL[] urls = createJarURLsFromDirectory(subDirectory);
		Arrays.sort(urls, Comparator.comparing(URL::toString));
		// TODO: This class could be extended to parse exclude-pattern from a optional
		// text files in the plugin directories.
		return new PluginDescriptor(subDirectory.getFileName().toString(), urls, new String[0]);
	}

	private URL[] createJarURLsFromDirectory(Path subDirectory) throws IOException {
		URL[] urls = Files.list(subDirectory).filter((Path p) -> Files.isRegularFile(p) && jarFileMatcher.matches(p))
				.map(uncheckedFunction((Path p) -> p.toUri().toURL())).toArray(URL[]::new);

		if (urls.length < 1) {
			throw new IOException("Cannot find any jar files for plugin in directory [" + subDirectory + "]."
					+ " Please provide the jar files for the plugin or delete the directory.");
		}

		return urls;
	}

	public static <A, B> Function<A, B> uncheckedFunction(FunctionWithException<A, B, ?> functionWithException) {
		return (A value) -> {
			try {
				return functionWithException.apply(value);
			}
			catch (Throwable t) {
				throw new PluginRuntimeException(t);
			}
		};
	}

}
