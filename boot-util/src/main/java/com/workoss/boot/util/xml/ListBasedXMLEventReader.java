/*
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
package com.workoss.boot.util.xml;

import com.workoss.boot.util.Assert;
import com.workoss.boot.util.reflect.ClassUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class ListBasedXMLEventReader implements XMLEventReader {

	private boolean closed;

	private final List<XMLEvent> events;

	private XMLEvent currentEvent;

	private int cursor = 0;

	public ListBasedXMLEventReader(List<XMLEvent> events) {
		Assert.notNull(events, "XMLEvent List must not be null");
		this.events = new ArrayList<>(events);
	}

	@Override
	public boolean hasNext() {
		return (this.cursor < this.events.size());
	}

	@Override
	public Object next() {
		return nextEvent();
	}

	@Override
	public XMLEvent nextEvent() {
		if (hasNext()) {
			this.currentEvent = this.events.get(this.cursor);
			this.cursor++;
			return this.currentEvent;
		}
		else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public XMLEvent peek() {
		if (hasNext()) {
			return this.events.get(this.cursor);
		}
		else {
			return null;
		}
	}

	@Override
	public String getElementText() throws XMLStreamException {
		checkIfClosed();
		if (this.currentEvent == null || !this.currentEvent.isStartElement()) {
			throw new XMLStreamException("Not at START_ELEMENT: " + this.currentEvent);
		}

		StringBuilder builder = new StringBuilder();
		while (true) {
			XMLEvent event = nextEvent();
			if (event.isEndElement()) {
				break;
			}
			else if (!event.isCharacters()) {
				throw new XMLStreamException("Unexpected non-text event: " + event);
			}
			Characters characters = event.asCharacters();
			if (!characters.isIgnorableWhiteSpace()) {
				builder.append(event.asCharacters().getData());
			}
		}
		return builder.toString();
	}

	@Override
	public XMLEvent nextTag() throws XMLStreamException {
		checkIfClosed();

		while (true) {
			XMLEvent event = nextEvent();
			switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
				case XMLStreamConstants.END_ELEMENT:
					return event;
				case XMLStreamConstants.END_DOCUMENT:
					return null;
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					continue;
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					if (!event.asCharacters().isWhiteSpace()) {
						throw new XMLStreamException("Non-ignorable whitespace CDATA or CHARACTERS event: " + event);
					}
					break;
				default:
					throw new XMLStreamException("Expected START_ELEMENT or END_ELEMENT: " + event);
			}
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported on " + ClassUtils.getShortName(getClass()));
	}

	/**
	 * This implementation throws an {@code IllegalArgumentException} for any property.
	 * @throws IllegalArgumentException when called
	 */
	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("Property not supported: [" + name + "]");
	}

	@Override
	public void close() {
		this.closed = true;
		this.events.clear();
	}

	/**
	 * Check if the reader is closed, and throws a {@code XMLStreamException} if so.
	 * @throws XMLStreamException if the reader is closed
	 * @see #close()
	 */
	protected void checkIfClosed() throws XMLStreamException {
		if (this.closed) {
			throw new XMLStreamException("XMLEventReader has been closed");
		}
	}

}
