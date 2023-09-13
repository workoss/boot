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

package com.workoss.boot.util.id;

import com.workoss.boot.util.Assert;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.IpUtil;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * fork <a href="https://gitee.com/yu120/sequence">...</a>
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class Sequence {

	private static final Logger logger = LoggerFactory.getLogger(Sequence.class);

	private final static String AT = "@";

	/**
	 * 默认的起始时间，为Thu, 04 Nov 2010 01:42:54 GMT
	 */
	public static long DEFAULT_TWEPOCH = 1288834974657L;

	/**
	 * 默认回拨时间，2S
	 */
	public static long DEFAULT_TIME_OFFSET = 2000L;

	/**
	 * 机器标识位数
	 */
	private static final long WORKER_ID_BITS = 5L;

	private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

	private static final long DATA_CENTER_ID_BITS = 5L;

	private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

	/**
	 * 毫秒内自增位
	 */
	private static final long SEQUENCE_BITS = 12L;

	/**
	 * 机器节点左移12位
	 */
	private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

	/**
	 * 数据中心节点左移 17位
	 */
	private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

	/**
	 * 时间戳左移动位22位
	 */
	private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

	/**
	 * 序列掩码 用于限定序列最大值不超过4095
	 */
	private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

	/**
	 * 初始化时间点 作为基准，一般取系统的最近时间（一旦确定不能变动）
	 */
	private static long twepoch = DEFAULT_TWEPOCH;

	private final long workerId;

	private final long datacenterId;

	private final boolean useSystemClock;

	/**
	 * 允许的时钟回拨毫秒数
	 */
	private final long timeOffset;

	/**
	 * 当在低频模式下时，序号始终为0，导致生成ID始终为偶数<br>
	 * 此属性用于限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题。<br>
	 * 注意次数必须小于{@link #SEQUENCE_MASK}，{@code 0}表示不使用随机数。<br>
	 * 这个上限不包括值本身。
	 */
	private final long randomSequenceLimit;

	/**
	 * 自增序号，当高频模式下时，同一毫秒内生成N个ID，则这个序号在同一毫秒下，自增以避免ID重复。
	 */
	private long sequence = 0L;

	private long lastTimestamp = -1L;

	public Sequence() {
		this(getWorkerId(getDatacenterId(MAX_DATA_CENTER_ID), MAX_WORKER_ID));
	}

	public Sequence(long workerId) {
		this(workerId, getDatacenterId(MAX_DATA_CENTER_ID));
	}

	/**
	 * 有参构造器
	 * @param workerId 工作机器 ID
	 * @param datacenterId 序列号
	 */
	public Sequence(long workerId, long datacenterId) {
		this(null, workerId, datacenterId, false, DEFAULT_TIME_OFFSET, 3);
	}

	public Sequence(LocalDateTime epochDate, long workerId, long datacenterId, boolean useSystemClock, long timeOffset,
			long randomSequenceLimit) {
		Assert.isTrue(!(workerId > MAX_WORKER_ID || workerId < 0),
				String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
		Assert.isTrue(!(datacenterId > MAX_DATA_CENTER_ID || datacenterId < 0),
				String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
		Assert.isTrue(randomSequenceLimit >= 0 && randomSequenceLimit <= SEQUENCE_MASK,
				String.format("randomSequenceLimit can't be less than %d or greater than 0", SEQUENCE_MASK));
		if (epochDate != null) {
			twepoch = DateUtils.getMillis(epochDate);
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
		this.useSystemClock = useSystemClock;
		this.timeOffset = timeOffset;
		this.randomSequenceLimit = randomSequenceLimit;
		logger.info(
				"[SNOWFLAKE] datacenterId:{} workerId:{} twepoch:{} useSystemClock:{} timeOffset:{} randomSequenceLimit:{}",
				datacenterId, workerId, twepoch, useSystemClock, timeOffset, randomSequenceLimit);
	}

	/**
	 * 获取 maxWorkerId
	 */
	protected static long getWorkerId(long datacenterId, long maxWorkerId) {
		StringBuilder mpid = new StringBuilder();
		mpid.append(datacenterId);
		String name = ManagementFactory.getRuntimeMXBean().getName();
		if (StringUtils.isNotBlank(name)) {
			/*
			 * GET jvmPid
			 */
			// int atIndex = name.indexOf(AT);
			// if (atIndex > 0) {
			// mpid.append(name.substring(0, atIndex));
			// } else {
			mpid.append(name.hashCode());
			// }
		}
		/*
		 * MAC + PID 的 hashcode 获取16个低位
		 */
		long workerId = (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
		logger.info("[SNOWFLAKE] mpid:{} datacenterId:{} workerId:{} name:{}", mpid, datacenterId, workerId, name);
		return workerId;
	}

	/**
	 * 数据标识id部分
	 */
	protected static long getDatacenterId(long maxDatacenterId) {
		Assert.isTrue(maxDatacenterId > 0, "maxDatacenterId must be > 0");
		if (maxDatacenterId == Long.MAX_VALUE) {
			maxDatacenterId -= 1;
		}
		long id = 1L;
		try {
			InetAddress localAddress = IpUtil.getLocalAddress();
			logger.info("[SNOWFLAKE] intelAddress:{}", (localAddress == null ? null : localAddress.toString()));
			assert localAddress != null;
			byte[] ipOrMac = localAddress.getAddress();
			if (ipOrMac == null) {
				NetworkInterface network = NetworkInterface.getByInetAddress(localAddress);
				if (null == network) {
					return id;
				}
				ipOrMac = network.getHardwareAddress();
			}
			if (ipOrMac == null) {
				return id;
			}
			id = ((0x000000FF & (long) ipOrMac[ipOrMac.length - 2])
					| (0x0000FF00 & (((long) ipOrMac[ipOrMac.length - 1]) << 8))) >> 6;
			return id % (maxDatacenterId + 1);
		}
		catch (Exception e) {
			logger.warn("[SNOWFLAKE] getDatacenterId: {}", ExceptionUtils.toShortString(e, 2));
		}
		return id;
	}

	/**
	 * 获取下一个 ID
	 * @return 下一个 ID
	 */
	public synchronized long nextId() {
		long timestamp = timeGen();
		if (timestamp < lastTimestamp) {
			long offset = lastTimestamp - timestamp;
			if (offset < timeOffset) {
				try {
					wait(offset << 1);
					timestamp = timeGen();
					if (timestamp < lastTimestamp) {
						throw new RuntimeException(String
							.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
					}
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else {
				// 如果服务器时间有问题(时钟后退) 报错。
				throw new IllegalStateException(
						String.format("Clock moved backwards. Refusing to generate id for %d ms", offset));
			}
		}

		if (timestamp == lastTimestamp) {
			sequence = (sequence + 1) & SEQUENCE_MASK;
			if (sequence == 0) {
				// 同一毫秒的序列数已经达到最大
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		else {
			// issue#I51EJY 不同毫秒内，序列号置为 randomSequenceLimit 随机数
			if (randomSequenceLimit > 1) {
				sequence = ThreadLocalRandom.current().nextLong(randomSequenceLimit);
			}
			else {
				sequence = 0L;
			}
		}

		lastTimestamp = timestamp;

		// 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
		return ((timestamp - twepoch) << TIMESTAMP_LEFT_SHIFT) | (datacenterId << DATA_CENTER_ID_SHIFT)
				| (workerId << WORKER_ID_SHIFT) | sequence;
	}

	/**
	 * 循环等待下一个时间
	 * @param lastTimestamp 上次记录的时间
	 * @return 下一个时间
	 */
	private long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		// 循环直到操作系统时间戳变化
		while (timestamp == lastTimestamp) {
			timestamp = timeGen();
		}
		if (timestamp < lastTimestamp) {
			// 如果发现新的时间戳比上次记录的时间戳数值小，说明操作系统时间发生了倒退，报错
			throw new IllegalStateException(String.format("Clock moved backwards. Refusing to generate id for %d ms",
					(lastTimestamp - timestamp)));
		}
		return timestamp;
	}

	/**
	 * 生成时间戳
	 * @return 时间戳
	 */
	private long timeGen() {
		return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
	}

	public static long parseTimestamp(long id) {
		return (id >> TIMESTAMP_LEFT_SHIFT & ~(-1L << 41L)) + twepoch;
	}

}
