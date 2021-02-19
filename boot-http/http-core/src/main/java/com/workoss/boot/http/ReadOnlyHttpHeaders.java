package com.workoss.boot.http;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

class ReadOnlyHttpHeaders extends HttpHeaders {

	@Nullable
	private MediaType cachedContentType;

	@Nullable
	private List<MediaType> cachedAccept;

	ReadOnlyHttpHeaders(MultiValueMap<String, String> headers) {
		super(headers);
	}

	@Override
	public MediaType getContentType() {
		if (this.cachedContentType != null) {
			return this.cachedContentType;
		}
		else {
			MediaType contentType = super.getContentType();
			this.cachedContentType = contentType;
			return contentType;
		}
	}

	@Override
	public List<MediaType> getAccept() {
		if (this.cachedAccept != null) {
			return this.cachedAccept;
		}
		else {
			List<MediaType> accept = super.getAccept();
			this.cachedAccept = accept;
			return accept;
		}
	}

	@Override
	public void clearContentHeaders() {
		// No-op.
	}

	@Override
	public List<String> get(Object key) {
		List<String> values = this.headers.get(key);
		return (values != null ? Collections.unmodifiableList(values) : null);
	}

	@Override
	public void add(String headerName, @Nullable String headerValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAll(String key, List<? extends String> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAll(MultiValueMap<String, String> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(String headerName, @Nullable String headerValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAll(Map<String, String> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> toSingleValueMap() {
		return Collections.unmodifiableMap(this.headers.toSingleValueMap());
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(this.headers.keySet());
	}

	@Override
	public List<String> put(String key, List<String> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<List<String>> values() {
		return Collections.unmodifiableCollection(this.headers.values());
	}

	@Override
	public Set<Map.Entry<String, List<String>>> entrySet() {
		return this.headers.entrySet().stream().map(AbstractMap.SimpleImmutableEntry::new)
				.collect(Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), // Retain
																									// original
																									// ordering
																									// of
																									// entries
						Collections::unmodifiableSet));
	}

}
