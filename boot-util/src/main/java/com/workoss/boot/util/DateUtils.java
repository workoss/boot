/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.collection.CollectionUtils;
import com.workoss.boot.util.collection.Pair;
import com.workoss.boot.util.exception.BootException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 时间处理工具类，针对 Date 和 LocalDateTime 相关日期操作
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class DateUtils {

	private static final Map<Pattern, Pair<Class<?>, String>> PATTERN_PATTERNSTR_MAP = new LinkedHashMap<>();

	private static final Map<String, DateTimeFormatter> PATTERN_DATEFORMAT_MAP = new ConcurrentHashMap<>();

	static {
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$"),
				Pair.of(LocalDateTime.class, "yyyy-MM-dd HH:mm:ss"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$"),
				Pair.of(LocalDateTime.class, "yyyy-MM-dd HH:mm:ss.SSS"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z$"),
				Pair.of(LocalDateTime.class, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"),
				Pair.of(LocalDateTime.class, "yyyy-MM-dd'T'HH:mm:ss"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}$"),
				Pair.of(LocalDateTime.class, "yyyy/MM/dd HH:mm:ss"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}$"),
				Pair.of(LocalDateTime.class, "yyyyMMddHHmmss"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$"), Pair.of(LocalDate.class, "yyyy-MM-dd"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{4}\\d{2}\\d{2}$"), Pair.of(LocalDate.class, "yyyyMMdd"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{2}:\\d{2}:\\d{2}$"), Pair.of(LocalTime.class, "HH:mm:ss"));
		PATTERN_PATTERNSTR_MAP.put(Pattern.compile("^\\d{2}\\d{2}\\d{2}$"), Pair.of(LocalTime.class, "HHmmss"));
	}

	/**
	 * 日期默认格式
	 */
	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
	public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

	private DateUtils() {
	}

	public static DateTimeFormatter getDateTimeFormatter(String pattern) {
		if (StringUtils.isBlank(pattern)) {
			pattern = DEFAULT_DATE_TIME_PATTERN;
		}
		if (!PATTERN_DATEFORMAT_MAP.containsKey(pattern)) {
			PATTERN_DATEFORMAT_MAP.put(pattern, DateTimeFormatter.ofPattern(pattern));
		}
		return PATTERN_DATEFORMAT_MAP.get(pattern);
	}

	/**
	 * <p>
	 * 获取当前时间(LocalDateTime)
	 * </p>
	 * @return LocalDateTime
	 */
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

	/**
	 * <p>
	 * 获取当前时间(java.util.Date)
	 * </p>
	 * @return java.util.Date
	 */
	public static Date getCurrentDate() {
		return Date.from(getCurrentDateTime().toInstant(ZoneOffset.ofHours(8)));
	}

	/**
	 * <p>
	 * 按指定格式获取当前时间字符串
	 * </p>
	 * <pre>
	 *     getCurrentDateTime("yyyy-MM-dd HH:mm:ss") == 2020-09-16 17:22:00
	 * </pre>
	 * @param pattern 示例 ：yyyy-MM-dd HH:mm:ss
	 * @return string
	 */
	public static String getCurrentDateTime(@Nullable String pattern) {
		return getCurrentDateTime().format(getDateTimeFormatter(pattern));
	}

	/**
	 * <p>
	 * Date To LocalDateTime
	 * </p>
	 * @param date Date
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(@NonNull Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	/**
	 * Date To LocalDate
	 * @param date Date
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(@NonNull Date date) {
		return toLocalDateTime(date).toLocalDate();
	}

	/**
	 * Date To LocalTime
	 * @param date Date
	 * @return LocalTime
	 */
	public static LocalTime toLocalTime(@NonNull Date date) {
		return toLocalDateTime(date).toLocalTime();
	}

	/**
	 * LocalDateTime To Date
	 * @param localDateTime LocalDateTime
	 * @return Date
	 */
	public static Date toDate(@NonNull LocalDateTime localDateTime) {
		return Date.from(localDateTime.toInstant(ZoneOffset.ofHours(8)));
	}

	/**
	 * LocalDate To Date
	 * @param localDate LocalDate
	 * @return Date
	 */
	public static Date toDate(@NonNull LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
	}

	/**
	 * <p>
	 * 格式化localDateTime日期为字符串
	 * </p>
	 * <pre>
	 *   format(date, "yyyy/MM/dd HH:mm:ss") = "2019/02/10 23:12:10"
	 *   format(date, "abc") throw IllegalArgumentException(Illegal pattern)
	 * </pre>
	 * @param localDateTime LocalDateTime
	 * @param pattern 示例 ：yyyy-MM-dd HH:mm:ss
	 * @return string
	 */
	public static String format(@NonNull LocalDateTime localDateTime, @Nullable String pattern) {
		return localDateTime.format(getDateTimeFormatter(pattern));
	}

	/**
	 * <p>
	 * 格式化 LocalDate 成字符串
	 * </p>
	 * @param localDate LocalDate
	 * @param pattern 示例 ：yyyy-MM-dd HH:mm:ss
	 * @return string
	 */
	public static String format(@NonNull LocalDate localDate, @Nullable String pattern) {
		return localDate.format(getDateTimeFormatter(pattern));
	}

	/**
	 * <p>
	 * 格式化日期
	 * </p>
	 * <pre>
	 * formatDate(date, "yyyy/MM/dd HH:mm:ss") = "2019/02/10 23:12:10"
	 * formatDate(date, "abc") throw IllegalArgumentException(Illegal pattern)
	 * </pre>
	 * @param date 日期
	 * @param pattern 格式字符串，示例 ：yyyy-MM-dd HH:mm:ss
	 * @return string
	 */
	public static String format(@NonNull Date date, @Nullable String pattern) {
		return format(toLocalDateTime(date), pattern);
	}

	/**
	 * <p>
	 * 格式化Date为字符串，默认格式 yyyy-MM-dd HH:mm:ss
	 * </p>
	 * <pre>
	 *  formatDate(date) == "2019-02-10 23:12:10"
	 * </pre>
	 * @param date 日期
	 * @return String
	 */
	public static String format(@NonNull Date date) {
		return format(date, DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * <p>
	 * 解析日期字符为LocalDateTime 默认格式为："yyyy-MM-dd HH:mm:ss"
	 * </p>
	 * @param str 日期字符串，示例：2019-02-10 23:12:10
	 * @return LocalDateTime
	 */
	public static LocalDateTime parse(@Nullable String str) {
		for (Map.Entry<Pattern, Pair<Class<?>, String>> patternPairEntry : PATTERN_PATTERNSTR_MAP.entrySet()) {
			if (patternPairEntry.getKey().matcher(str).matches()) {
				Pair<Class<?>, String> pair = patternPairEntry.getValue();
				Class<?> clazz = pair.getFirst();
				if (LocalDateTime.class.getName().equalsIgnoreCase(clazz.getName())) {
					return LocalDateTime.parse(str, getDateTimeFormatter(pair.getSecond()));
				}
				else if (LocalDate.class.getName().equalsIgnoreCase(clazz.getName())) {
					return LocalDateTime.of(LocalDate.parse(str, getDateTimeFormatter(pair.getSecond())),
							LocalTime.MIN);
				}
				else if (LocalTime.class.getName().equalsIgnoreCase(clazz.getName())) {
					return LocalDateTime.of(LocalDate.of(1970, 1, 1),
							LocalTime.parse(str, getDateTimeFormatter(pair.getSecond())));
				}
			}
		}
		throw new RuntimeException("没有找到匹配[" + str + "]的pattern");
	}

	/**
	 * <p>
	 * 格式化日期时间，在不确定格式的时候使用
	 * </p>
	 * @param dateTime 日期字符串，示例：2019-02-10 23:12:10
	 * @param patterns 格式字符串数组
	 * @return LocalDateTime
	 */
	public static LocalDateTime parse(@NonNull String dateTime, @Nullable String... patterns) {
		if (CollectionUtils.isEmpty(patterns)) {
			return parse(dateTime);
		}
		Exception e = null;
		for (String pattern : patterns) {
			try {
				return LocalDateTime.parse(dateTime, getDateTimeFormatter(pattern));
			}
			catch (Exception ignored) {
				e = ignored;
			}
		}
		throw new RuntimeException(e);
	}


	public static LocalTime localTimeParse(@NonNull String time, @Nullable String... patterns) {
		if (CollectionUtils.isEmpty(patterns)) {
			return parse(time).toLocalTime();
		}
		Exception e = null;
		for (String pattern : patterns) {
			try {
				return LocalTime.parse(time, getDateTimeFormatter(pattern));
			}
			catch (Exception ignored) {
				e = ignored;
			}
		}
		throw new RuntimeException(e);
	}
	/**
	 * <p>
	 * 格式化日期
	 * </p>
	 * @param dateTime 日期字符串
	 * @param patterns 格式字符串数组
	 * @return LocalDate
	 */
	public static LocalDate localDateParse(@NonNull String dateTime, @Nullable String... patterns) {
		if (CollectionUtils.isEmpty(patterns)) {
			return parse(dateTime).toLocalDate();
		}
		Exception e = null;
		for (String pattern : patterns) {
			try {
				return LocalDate.parse(dateTime, getDateTimeFormatter(pattern));
			}
			catch (Exception ignored) {
				e = ignored;
			}
		}
		throw new RuntimeException(e);
	}

	/**
	 * <p>
	 * 格式化日期
	 * </p>
	 * @param dateTime 日期字符串
	 * @param patterns 日期格式化
	 * @return Date
	 */
	public static Date parseDate(@NonNull String dateTime, @Nullable String... patterns) {
		try {
			return toDate(parse(dateTime, patterns));
		}
		catch (Exception e) {
			return toDate(localDateParse(dateTime, patterns));
		}
	}

	/**
	 * <p>
	 * 基于date增加天数
	 * </p>
	 * @param date 日期
	 * @param day 天数
	 * @return Date
	 */
	public static Date plusDays(Date date, long day) {
		return toDate(plusDays(toLocalDateTime(date), day));
	}

	/**
	 * <p>
	 * 基于LocalDateTime增加天数
	 * </p>
	 * @param date 日期
	 * @param day 天数
	 * @return LocalDateTime
	 */
	public static LocalDateTime plusDays(@NonNull LocalDateTime date, long day) {
		return plus(date, day, ChronoUnit.DAYS);
	}

	/**
	 * <p>
	 * 基于Date增加小时数
	 * </p>
	 * @param date 日期
	 * @param hours 小时数
	 * @return Date
	 */
	public static Date plusHours(@NonNull Date date, long hours) {
		return toDate(plusHours(toLocalDateTime(date), hours));
	}

	/**
	 * <p>
	 * 基于LocalDateTime增加小时数
	 * </p>
	 * @param localDateTime 日期
	 * @param hours 小时数
	 * @return LocalDateTime
	 */
	public static LocalDateTime plusHours(@NonNull LocalDateTime localDateTime, long hours) {
		return plus(localDateTime, hours, ChronoUnit.HOURS);
	}

	/**
	 * <p>
	 * 基于Date增加分钟数
	 * </p>
	 * @param date 日期
	 * @param minutes 数
	 * @return Date
	 */
	public static Date plusMinutes(@NonNull Date date, long minutes) {
		return toDate(plusMinutes(toLocalDateTime(date), minutes));
	}

	/**
	 * <p>
	 * 基于LocalDateTime增加分钟数
	 * </p>
	 * @param localDateTime 日期
	 * @param minutes 分钟数
	 * @return LocalDateTime
	 */
	public static LocalDateTime plusMinutes(@NonNull LocalDateTime localDateTime, long minutes) {
		return plus(localDateTime, minutes, ChronoUnit.MINUTES);
	}

	/**
	 * <p>
	 * 基于date增加秒数
	 * </p>
	 * @param date 日期
	 * @param seconds 秒
	 * @return Date
	 */
	public static Date plusSeconds(@NonNull Date date, long seconds) {
		return toDate(plusSeconds(toLocalDateTime(date), seconds));
	}

	/**
	 * <p>
	 * 基于LocalDateTime增加秒数
	 * </p>
	 * @param localDateTime 日期
	 * @param seconds 秒
	 * @return LocalDateTime
	 */
	public static LocalDateTime plusSeconds(@NonNull LocalDateTime localDateTime, long seconds) {
		return plus(localDateTime, seconds, ChronoUnit.SECONDS);
	}

	/**
	 * <p>
	 * 基于LocalDateTime增加指定单位的时间数量
	 * </p>
	 * @param date 日期
	 * @param amountToAdd 数量
	 * @param unit 单位 详见TemporalUnit实现类 ChronoUnit
	 * @return LocalDateTime
	 */
	public static LocalDateTime plus(@NonNull LocalDateTime date, long amountToAdd, @NonNull TemporalUnit unit) {
		return date.plus(amountToAdd, unit);
	}

	/**
	 * 减少day
	 * @param date 日期
	 * @param day 天数
	 * @return Date
	 */
	public static Date minusDays(@NonNull Date date, long day) {
		return toDate(minusDays(toLocalDateTime(date), day));
	}

	/**
	 * 减少day
	 * @param date 日期
	 * @param day 天数
	 * @return LocalDateTime
	 */
	public static LocalDateTime minusDays(@NonNull LocalDateTime date, long day) {
		return minus(date, day, ChronoUnit.DAYS);
	}

	/**
	 * 减少小时
	 * @param date 日期
	 * @param hours 小时数
	 * @return Date
	 */
	public static Date minusHours(@NonNull Date date, long hours) {
		return toDate(minusHours(toLocalDateTime(date), hours));
	}

	/**
	 * 减少小时
	 * @param date 日期
	 * @param hours 小时数
	 * @return LocalDateTime
	 */
	public static LocalDateTime minusHours(@NonNull LocalDateTime date, long hours) {
		return minus(date, hours, ChronoUnit.HOURS);
	}

	/**
	 * 减少分钟
	 * @param date 日期
	 * @param minutes 数
	 * @return Date
	 */
	public static Date minusMinutes(@NonNull Date date, long minutes) {
		return toDate(minusMinutes(toLocalDateTime(date), minutes));
	}

	/**
	 * 减少分钟
	 * @param date 日期
	 * @param minutes 分钟数
	 * @return LocalDateTime
	 */
	public static LocalDateTime minusMinutes(@NonNull LocalDateTime date, long minutes) {
		return minus(date, minutes, ChronoUnit.MINUTES);
	}

	/**
	 * 减少秒
	 * @param date 日期
	 * @param seconds 秒
	 * @return Date
	 */
	public static Date minusSeconds(@NonNull Date date, long seconds) {
		return toDate(minusSeconds(toLocalDateTime(date), seconds));
	}

	/**
	 * 减少秒
	 * @param date 日期
	 * @param seconds 秒
	 * @return LocalDateTime
	 */
	public static LocalDateTime minusSeconds(@NonNull LocalDateTime date, long seconds) {
		return minus(date, seconds, ChronoUnit.SECONDS);
	}

	/**
	 * 减少
	 * @param date 日期
	 * @param amountToSubstract 数量
	 * @param unit 单位 ChronoUnit
	 * @return LocalDateTime
	 */
	public static LocalDateTime minus(@NonNull LocalDateTime date, long amountToSubstract, @NonNull TemporalUnit unit) {
		return date.minus(amountToSubstract, unit);
	}

	/**
	 * secondDate - firstDate 秒
	 * @param firstDate 第一个日期
	 * @param secondDate 第二个日期
	 * @return 秒
	 */
	public static long betweenSeconds(@NonNull Date firstDate, @NonNull Date secondDate) {
		return between(firstDate, secondDate, ChronoUnit.SECONDS);
	}

	/**
	 * secondDate - firstDate chronoUnit
	 * @param firstDate 第一个日期
	 * @param secondDate 第二个日期
	 * @param chronoUnit 单位
	 * @return 秒
	 */
	public static long between(@NonNull Date firstDate, @NonNull Date secondDate, @NonNull ChronoUnit chronoUnit) {
		return chronoUnit.between(toLocalDateTime(firstDate), toLocalDateTime(secondDate));
	}

	/**
	 * secondDate - firstDate 秒
	 * @param firstDate 第一个日期
	 * @param secondDate 第二个日期
	 * @return 秒
	 */
	public static long betweenSeconds(@NonNull LocalDateTime firstDate, @NonNull LocalDateTime secondDate) {
		return between(firstDate, secondDate, ChronoUnit.SECONDS);
	}

	/**
	 * secondDate - firstDate chronoUnit
	 * @param firstDate 第一个日期
	 * @param secondDate 第二个日期
	 * @param chronoUnit 单位
	 * @return 秒
	 */
	public static long between(@NonNull LocalDateTime firstDate, @NonNull LocalDateTime secondDate,
			@NonNull ChronoUnit chronoUnit) {
		return chronoUnit.between(firstDate, secondDate);
	}

	/**
	 * 获取localdatetime毫秒数
	 * @param localDateTime 日期
	 * @return 秒
	 */
	public static long getMillis(@NonNull LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return 0;
		}
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * <p>
	 * 获取localdatetime秒数
	 * </p>
	 * @param localDateTime 日期
	 * @return 秒
	 */
	public static long getSecond(@NonNull LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return 0;
		}
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
	}

	/**
	 * <p>
	 * 获取某天开始时间，如：2020-09-17 00:00:00
	 * </p>
	 * @param localDateTime 日期
	 * @return LocalDateTime
	 */
	public static LocalDateTime getDayStart(@NonNull LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		localDateTime = localDateTime.withHour(0).withMinute(0).withSecond(0);
		return localDateTime;
	}

	/**
	 * <p>
	 * 获取某天结束时间，如：2020-09-17 23:59:59
	 * </p>
	 * @param localDateTime 日期
	 * @return LocalDateTime
	 */
	public static LocalDateTime getDayEnd(@NonNull LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		localDateTime = localDateTime.withHour(23).withMinute(59).withSecond(59);
		return localDateTime;
	}

	/**
	 * <p>
	 * 获取当天开始时间，如：2020-09-17 00:00:00
	 * </p>
	 * @return LocalDateTime
	 */
	public static LocalDateTime getCurrentDayStart() {
		return getDayStart(LocalDateTime.now());
	}

	/**
	 * <p>
	 * 获取当天结束时间 如：2020-09-17 23:59:59
	 * </p>
	 * @return LocalDateTime
	 */
	public static LocalDateTime getCurrentDayEnd() {
		return getDayEnd(LocalDateTime.now());
	}

}
