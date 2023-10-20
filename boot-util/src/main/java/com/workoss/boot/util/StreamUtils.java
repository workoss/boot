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

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.util.exception.BootException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author workoss
 */
@SuppressWarnings("unused")
public class StreamUtils {

	/**
	 * The default buffer size used when copying bytes.
	 */
	public static final int BUFFER_SIZE = 4096;

	public static final byte[] EMPTY_CONTENT = new byte[0];

	public static String readFile(InputStream inputStream) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
			}
			return stringBuffer.toString();
		}
		catch (IOException e) {
			throw new BootException(e);
		}
		finally {
			nonClosing(inputStream);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		}
		catch (IOException ignored) {
		}
	}

	public static List<String> readLines(InputStream input, String charsetName) {
		return readLines(input, Charset.forName(charsetName));
	}

	public static List<String> readLines(InputStream input, Charset charset) {
		return readLines(new InputStreamReader(input, charset));
	}

	public static List<String> readLines(Reader reader) {
		return toBufferedReader(reader).lines().collect(Collectors.toList());
	}

	public static BufferedReader toBufferedReader(Reader reader) {
		return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
	}

	public static void write(final String data, final OutputStream output, final Charset charset) throws IOException {
		if (data != null) {
			// Use Charset#encode(String), since calling String#getBytes(Charset) might
			// result in
			// NegativeArraySizeException or OutOfMemoryError.
			// The underlying OutputStream should not be closed, so the channel is not
			// closed.
			Charset writeCharset = charset == null ? StandardCharsets.UTF_8 : charset;
			Channels.newChannel(output).write(writeCharset.encode(data));
		}
	}

	/**
	 * Copy the contents of the given InputStream into a new byte array.
	 * <p>
	 * Leaves the stream open when done.
	 * @param in the stream to copy from (may be {@code null} or empty)
	 * @return the new byte array that has been copied to (possibly empty)
	 * @throws IOException in case of I/O errors
	 */
	public static byte[] copyToByteArray(InputStream in) throws IOException {
		if (in == null) {
			return new byte[0];
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		copy(in, out);
		return out.toByteArray();
	}

	/**
	 * Copy the contents of the given InputStream into a String.
	 * <p>
	 * Leaves the stream open when done.
	 * @param in the InputStream to copy from (may be {@code null} or empty)
	 * @param charset the {@link Charset} to use to decode the bytes
	 * @return the String that has been copied to (possibly empty)
	 * @throws IOException in case of I/O errors
	 */
	public static String copyToString(InputStream in, Charset charset) throws IOException {
		if (in == null) {
			return "";
		}
		StringBuilder out = new StringBuilder();
		try (InputStreamReader reader = new InputStreamReader(in, charset)) {
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = reader.read(buffer)) != -1) {
				out.append(buffer, 0, bytesRead);
			}
		}
		return out.toString();
	}

	/**
	 * Copy the contents of the given {@link ByteArrayOutputStream} into a {@link String}.
	 * <p>
	 * This is a more effective equivalent of
	 * {@code new String(baos.toByteArray(), charset)}.
	 * <p>
	 * As long as the {@code charset} is already available at the point of invocation, no
	 * exception is expected to be thrown by this method.
	 * @param baos the {@code ByteArrayOutputStream} to be copied into a String
	 * @param charset the {@link Charset} to use to decode the bytes
	 * @return the String that has been copied to (possibly empty)
	 * @since 5.2.6
	 */
	public static String copyToString(ByteArrayOutputStream baos, Charset charset) {
		Assert.notNull(baos, "No ByteArrayOutputStream specified");
		Assert.notNull(charset, "No Charset specified");
		try {
			return baos.toString(charset.name());
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("Failed to copy contents of ByteArrayOutputStream into a String", ex);
		}
	}

	/**
	 * Copy the contents of the given byte array to the given OutputStream.
	 * <p>
	 * Leaves the stream open when done.
	 * @param in the byte array to copy from
	 * @param out the OutputStream to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(byte[] in, OutputStream out) throws IOException {
		Assert.notNull(in, "No input byte array specified");
		Assert.notNull(out, "No OutputStream specified");

		out.write(in);
	}

	/**
	 * Copy the contents of the given String to the given output OutputStream.
	 * <p>
	 * Leaves the stream open when done.
	 * @param in the String to copy from
	 * @param charset the Charset
	 * @param out the OutputStream to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(String in, Charset charset, OutputStream out) throws IOException {
		Assert.notNull(in, "No input String specified");
		Assert.notNull(charset, "No Charset specified");
		Assert.notNull(out, "No OutputStream specified");

		try (Writer writer = new OutputStreamWriter(out, charset)) {
			writer.write(in);
			writer.flush();
		}
	}

	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * <p>
	 * Leaves both streams open when done.
	 * @param in the InputStream to copy from
	 * @param out the OutputStream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		Assert.notNull(out, "No OutputStream specified");

		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}

	/**
	 * Copy a range of content of the given InputStream to the given OutputStream.
	 * <p>
	 * If the specified range exceeds the length of the InputStream, this copies up to the
	 * end of the stream and returns the actual number of copied bytes.
	 * <p>
	 * Leaves both streams open when done.
	 * @param in the InputStream to copy from
	 * @param out the OutputStream to copy to
	 * @param start the position to start copying from
	 * @param end the position to end copying
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 * @since 4.3
	 */
	public static long copyRange(InputStream in, OutputStream out, long start, long end) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		Assert.notNull(out, "No OutputStream specified");

		long skipped = in.skip(start);
		if (skipped < start) {
			throw new IOException("Skipped only " + skipped + " bytes out of " + start + " required");
		}

		long bytesToCopy = end - start + 1;
		byte[] buffer = new byte[(int) Math.min(StreamUtils.BUFFER_SIZE, bytesToCopy)];
		while (bytesToCopy > 0) {
			int bytesRead = in.read(buffer);
			if (bytesRead == -1) {
				break;
			}
			else if (bytesRead <= bytesToCopy) {
				out.write(buffer, 0, bytesRead);
				bytesToCopy -= bytesRead;
			}
			else {
				out.write(buffer, 0, (int) bytesToCopy);
				bytesToCopy = 0;
			}
		}
		return (end - start + 1 - bytesToCopy);
	}

	/**
	 * Drain the remaining content of the given InputStream.
	 * <p>
	 * Leaves the InputStream open when done.
	 * @param in the InputStream to drain
	 * @return the number of bytes read
	 * @throws IOException in case of I/O errors
	 * @since 4.3
	 */
	public static int drain(InputStream in) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		int byteCount = 0;
		while ((bytesRead = in.read(buffer)) != -1) {
			byteCount += bytesRead;
		}
		return byteCount;
	}

	/**
	 * Return an efficient empty {@link InputStream}.
	 * @return a {@link ByteArrayInputStream} based on an empty byte array
	 * @since 4.2.2
	 */
	public static InputStream emptyInput() {
		return new ByteArrayInputStream(EMPTY_CONTENT);
	}

	/**
	 * Return a variant of the given {@link InputStream} where calling
	 * {@link InputStream#close() close()} has no effect.
	 * @param in the InputStream to decorate
	 * @return a version of the InputStream that ignores calls to close
	 */
	public static InputStream nonClosing(InputStream in) {
		Assert.notNull(in, "No InputStream specified");
		return new NonClosingInputStream(in);
	}

	/**
	 * Return a variant of the given {@link OutputStream} where calling
	 * {@link OutputStream#close() close()} has no effect.
	 * @param out the OutputStream to decorate
	 * @return a version of the OutputStream that ignores calls to close
	 */
	public static OutputStream nonClosing(OutputStream out) {
		Assert.notNull(out, "No OutputStream specified");
		return new NonClosingOutputStream(out);
	}

	private static class NonClosingInputStream extends FilterInputStream {

		public NonClosingInputStream(InputStream in) {
			super(in);
		}

		@Override
		public void close() {
			// ignore
		}

	}

	private static class NonClosingOutputStream extends FilterOutputStream {

		public NonClosingOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(@NonNull byte[] b, int off, int let) throws IOException {
			// It is critical that we override this method for performance
			this.out.write(b, off, let);
		}

		@Override
		public void close() {
			// ignore
		}

	}

}
