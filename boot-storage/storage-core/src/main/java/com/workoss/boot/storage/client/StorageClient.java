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
package com.workoss.boot.storage.client;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.model.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 存储服务接口 包括 上传 下载 预览url 签名
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public interface StorageClient {

	/**
	 * 客户端类型
	 * @return 客户端类型
	 */
	StorageType type();

	/**
	 * 初始化
	 * @param config 配置
	 */
	void init(@NonNull StorageClientConfig config);

	/**
	 * bucket 是否存在
	 * @return true/false
	 */
	boolean doesBucketExist();

	/**
	 * 获取bucket信息
	 * @return bucketInfo
	 */
	StorageBucketInfo getBucket();

	/**
	 * 展示所有buckets信息
	 * @return 列表
	 */
	List<StorageBucketInfo> listBuckets();

	/**
	 * 文件是否存在
	 * @param key 文件key
	 * @return true/false
	 */
	boolean doesObjectExist(@NonNull String key);

	/**
	 * 获取文件信息
	 * @param key 文件key
	 * @return 文件信息
	 */
	StorageFileInfo getObject(@NonNull String key);

	/**
	 * 展示目录文件列表
	 * @param key 目录key
	 * @param delimiter 分割线 若是展示目录下的目录 则null 显示文件 为“/”
	 * @param nextToken 分页下一个标记
	 * @param maxKey 每页大小
	 * @return StorageFileInfoListing
	 */
	StorageFileInfoListing listObjects(@NonNull String key, @Nullable String delimiter, @Nullable String nextToken,
			@Nullable Integer maxKey);

	/**
	 * 上传文件
	 * @param key 文件key
	 * @param file 文件
	 * @param userMetaData 用户head
	 * @param consumer 事件监听
	 * @return 文件信息
	 */
	StorageFileInfo putObject(@NonNull String key, @NonNull File file, @Nullable Map<String, String> userMetaData,
			@Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * 上传文件
	 * @param key 文件key
	 * @param inputStream 流
	 * @param contentType 类型
	 * @param userMetaData 用户head
	 * @param consumer 事件监听
	 * @return 文件信息
	 */
	StorageFileInfo putObject(@NonNull String key, @NonNull InputStream inputStream, @Nullable String contentType,
			@Nullable Map<String, String> userMetaData, @Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * 上传文件
	 * @param key 文件key
	 * @param bytes 二进制
	 * @param contentType 类型
	 * @param userMetaData 用户head
	 * @param consumer 事件监听
	 * @return 文件信息
	 */
	StorageFileInfo putObject(@NonNull String key, @NonNull byte[] bytes, @Nullable String contentType,
			@Nullable Map<String, String> userMetaData, @Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * 下载 sts 不能使用
	 * @param key 文件key
	 * @param consumer 事件监听
	 * @return
	 */
	StorageFileInfo downloadWithSign(@NonNull String key, @Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * 获取文件流
	 * @param key 文件
	 * @param consumer 消费者
	 * @return 流
	 */
	InputStream downloadStream(@NonNull String key, @Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * 下载 二进制
	 * @param key 文件key
	 * @param consumer 消费者
	 * @return byte数组
	 */
	byte[] download(@NonNull String key, @Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * 下载文件
	 * @param key 文件key
	 * @param consumer 消费者
	 * @param destFile 目标文件
	 * @return 文件
	 */
	File download(@NonNull String key, @NonNull File destFile, @Nullable Consumer<StorageProgressEvent> consumer);

	/**
	 * bucket 同源复制 单文件 不增加basePath
	 * @param sourceKeyWithoutBasePath 源key
	 * @param destinationKeyWithoutBasePath 目标key
	 * @param userMetaData 用户属性
	 * @return
	 */
	StorageFileInfo copyObject(@NonNull String sourceKeyWithoutBasePath, @NonNull String destinationKeyWithoutBasePath,
			@Nullable Map<String, String> userMetaData);

	/**
	 * 删除key文件
	 * @param key 文件key
	 */
	void deleteObject(@NonNull String key);

	/**
	 * 生成 h5上传签名
	 * @param key 文件名称 包括 路径
	 * @param mimeType 文件类型 text/plain 为空 则根据key自动获取
	 * @param successActionStatus 可以为空 为空 则web form上传 无字段success_action_status
	 * @return
	 */
	StorageSignature generateWebUploadSign(@NonNull String key, @Nullable String mimeType,
			@Nullable String successActionStatus);

	/**
	 * 生成分享有效url 需要accessId 有权限 暂时不支持 securityToken 形式
	 * @param key 文件key
	 * @param expiration 结束时间
	 * @return 分享url
	 */
	URL generatePresignedUrl(@NonNull String key, @NonNull Date expiration);

	/**
	 * 关闭资源
	 */
	void shutdown();

}
