package com.workoss.boot.plugin.mybatis.finder;

public interface ClassFinderMatcher {

	/**
	 * 排序
	 *
	 * @return
	 */
	int order();

	/**
	 * 匹配 过滤查找一级
	 *
	 * @return 是否使用
	 */
	boolean match();

	/**
	 * 初始化
	 * @return EntityClassFinder
	 */
	EntityClassFinder instance();

}
