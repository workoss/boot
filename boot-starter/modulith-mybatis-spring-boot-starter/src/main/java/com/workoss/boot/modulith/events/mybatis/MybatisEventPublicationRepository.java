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
package com.workoss.boot.modulith.events.mybatis;

import com.workoss.boot.plugin.mybatis.DynamicDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.modulith.events.core.EventPublication;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.modulith.events.core.PublicationTargetIdentifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author workoss
 */
@Slf4j
class MybatisEventPublicationRepository implements EventPublicationRepository {

	private static final String SQL_STATEMENT_INSERT = "INSERT INTO EVENT_PUBLICATION (ID, EVENT_TYPE, LISTENER_ID, PUBLICATION_DATE, SERIALIZED_EVENT) VALUES (#{params.id}, #{params.eventType}, #{params.listenerId}, #{params.publicationDate}, #{params.serializedEvent})";

	private static final String SQL_STATEMENT_FIND_UNCOMPLETED = "SELECT ID, COMPLETION_DATE, EVENT_TYPE, LISTENER_ID, PUBLICATION_DATE, SERIALIZED_EVENT FROM EVENT_PUBLICATION WHERE COMPLETION_DATE IS NULL ORDER BY PUBLICATION_DATE ASC";

	private static final String SQL_STATEMENT_UPDATE = "UPDATE EVENT_PUBLICATION SET COMPLETION_DATE = #{params.completionDate} WHERE ID = #{params.id}";

	private static final String SQL_STATEMENT_UPDATE_BY_EVENT_AND_LISTENER_ID = "UPDATE EVENT_PUBLICATION SET COMPLETION_DATE = #{params.completionDate} WHERE LISTENER_ID = #{params.listenerId} AND SERIALIZED_EVENT = #{params.serializedEvent}";

	private static final String SQL_STATEMENT_FIND_BY_EVENT_AND_LISTENER_ID = "SELECT * FROM EVENT_PUBLICATION WHERE SERIALIZED_EVENT = #{params.serializedEvent} AND LISTENER_ID = ? AND COMPLETION_DATE IS NULL ORDER BY PUBLICATION_DATE";

	private static final String SQL_STATEMENT_DELETE_UNCOMPLETED = "DELETE FROM EVENT_PUBLICATION WHERE COMPLETION_DATE IS NOT NULL ";

	private static final String SQL_STATEMENT_DELETE_UNCOMPLETED_BEFORE = "DELETE FROM EVENT_PUBLICATION WHERE COMPLETION_DATE < #{params.completionDate}";

	private final EventSerializer serializer;

	private final DynamicDao dynamicDao;

	private final DatabaseType databaseType;

	MybatisEventPublicationRepository(EventSerializer serializer, DynamicDao dynamicDao, DatabaseType databaseType) {
		this.serializer = serializer;
		this.dynamicDao = dynamicDao;
		this.databaseType = databaseType;
	}

	@Override
	public EventPublication create(EventPublication publication) {
		String serializedEvent = this.serializeEvent(publication.getEvent());
		Map<String, Object> params = new HashMap<>();
		params.put("id", databaseType.uuidToDatabase(publication.getIdentifier()));
		params.put("eventType", publication.getEvent().getClass().getName());
		params.put("listenerId", publication.getTargetIdentifier().getValue());
		params.put("publicationDate", Timestamp.from(publication.getPublicationDate()));
		params.put("serializedEvent", serializedEvent);
		dynamicDao.executeUpdate(SQL_STATEMENT_INSERT, params);
		return publication;
	}

	@Transactional
	@Override
	public void markCompleted(Object event, PublicationTargetIdentifier identifier, Instant completionDate) {
		Map<String, Object> params = new HashMap<>();
		params.put("completionDate", Timestamp.from(completionDate));
		params.put("listenerId", identifier.getValue());
		params.put("serializedEvent", serializer.serialize(event));
		dynamicDao.executeUpdate(SQL_STATEMENT_UPDATE_BY_EVENT_AND_LISTENER_ID, params);
	}

	@Override
	public List<EventPublication> findIncompletePublications() {
		List<Map<String, Object>> mapList = dynamicDao.executeQuery(SQL_STATEMENT_FIND_UNCOMPLETED,
				Collections.emptyMap());
		if (mapList == null || mapList.isEmpty()) {
			return Collections.emptyList();
		}
		return mapList.stream().map(this::resultMapToPublication).collect(Collectors.toList());
	}

	@Override
	public Optional<EventPublication> findIncompletePublicationsByEventAndTargetIdentifier(Object event,
			PublicationTargetIdentifier targetIdentifier) {
		String listenerId = targetIdentifier.getValue();
		Map<String, Object> params = new HashMap<>();
		params.put("listenerId", listenerId);
		params.put("serializedEvent", serializer.serialize(event));
		List<Map<String, Object>> mapList = dynamicDao.executeQuery(SQL_STATEMENT_FIND_BY_EVENT_AND_LISTENER_ID,
				params);
		if (mapList == null || mapList.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(resultMapToPublication(mapList.get(0)));
	}

	private EventPublication resultMapToPublication(Map<String, Object> map) {
		UUID id = (UUID) map.get("ID");
		var eventClass = loadClass(id, (String) map.get("EVENT_TYPE"));
		if (eventClass == null) {
			return null;
		}
		Instant completionDate = map.containsKey("COMPLETION_DATE") ? (Instant) map.get("COMPLETION_DATE") : null;
		var publicationDate = (Instant) map.get("PUBLICATION_DATE");
		var listenerId = (String) map.get("LISTENER_ID");
		var serializedEvent = (String) map.get("SERIALIZED_EVENT");
		return new MybatisEventPublication(id, publicationDate, listenerId, serializedEvent, eventClass, serializer,
				completionDate);
	}

	@Override
	public void deleteCompletedPublications() {
		dynamicDao.executeUpdate(SQL_STATEMENT_DELETE_UNCOMPLETED, null);
	}

	@Override
	public void deleteCompletedPublicationsBefore(Instant instant) {
		Map<String, Object> params = new HashMap<>();
		params.put("completionDate", Timestamp.from(instant));
		dynamicDao.executeUpdate(SQL_STATEMENT_DELETE_UNCOMPLETED_BEFORE, params);
	}

	@Nullable
	private Class<?> loadClass(UUID id, String className) {
		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			log.warn("Event '{}' of unknown type '{}' found", id, className);
			return null;
		}
	}

	private String serializeEvent(Object event) {
		return this.serializer.serialize(event).toString();
	}

	private static class MybatisEventPublication implements EventPublication {

		private final UUID id;

		private final Instant publicationDate;

		private final String listenerId;

		private final String serializedEvent;

		private final Class<?> eventType;

		private final EventSerializer serializer;

		@Nullable
		private Instant completionDate;

		public MybatisEventPublication(UUID id, Instant publicationDate, String listenerId, String serializedEvent,
				Class<?> eventType, EventSerializer serializer, @Nullable Instant completionDate) {
			Assert.notNull(id, "Id must not be null!");
			Assert.notNull(publicationDate, "Publication date must not be null!");
			Assert.hasText(listenerId, "Listener id must not be null or empty!");
			Assert.hasText(serializedEvent, "Serialized event must not be null or empty!");
			Assert.notNull(eventType, "Event type must not be null!");
			Assert.notNull(serializer, "EventSerializer must not be null!");
			this.id = id;
			this.publicationDate = publicationDate;
			this.listenerId = listenerId;
			this.serializedEvent = serializedEvent;
			this.eventType = eventType;
			this.serializer = serializer;
			this.completionDate = completionDate;
		}

		public UUID getIdentifier() {
			return this.id;
		}

		public Object getEvent() {
			return this.serializer.deserialize(this.serializedEvent, this.eventType);
		}

		public PublicationTargetIdentifier getTargetIdentifier() {
			return PublicationTargetIdentifier.of(this.listenerId);
		}

		public Instant getPublicationDate() {
			return this.publicationDate;
		}

		public Optional<Instant> getCompletionDate() {
			return Optional.ofNullable(this.completionDate);
		}

		public boolean isPublicationCompleted() {
			return this.completionDate != null;
		}

		public void markCompleted(Instant instant) {
			this.completionDate = instant;
		}

		public boolean equals(@Nullable Object obj) {
			if (this == obj) {
				return true;
			}
			else if (!(obj instanceof MybatisEventPublication)) {
				return false;
			}
			else {
				MybatisEventPublication that = (MybatisEventPublication) obj;
				return Objects.equals(this.completionDate, that.completionDate)
						&& Objects.equals(this.eventType, that.eventType) && Objects.equals(this.id, that.id)
						&& Objects.equals(this.listenerId, that.listenerId)
						&& Objects.equals(this.publicationDate, that.publicationDate)
						&& Objects.equals(this.serializedEvent, that.serializedEvent)
						&& Objects.equals(this.serializer, that.serializer);
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[] { this.completionDate, this.eventType, this.id, this.listenerId,
					this.publicationDate, this.serializedEvent, this.serializer });
		}

	}

}
