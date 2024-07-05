package com.tl.core;

import com.tl.core.enums.TLFieldType;

import java.util.List;

/**
 * TemplateField
 *
 * @author Wesley
 * @since 2023/07/13
 */
public interface TemplateField {

	/**
	 *
	 * @return java.lang.String 字段名称
	 * @author Wesley
	 * @since 2023/07/13
	 **/
	String getName();

	/**
	 *
	 * @return java.lang.String 分组名称
	 * @author Wesley
	 * @since 2024/07/04
	 **/
	String getGroup();

	/**
	 *
	 * @return java.lang.String 字段内容
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	String getContent();

	/**
	 *
	 * @return java.lang.String 字段定位信息
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	String getLocation();

	/**
	 *
	 * @return java.util.List<java.lang.String> 配置参数
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	List<String> getParams();

	/**
	 *
	 * @return com.tl.core.enums.TLFieldType 字段类型
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	TLFieldType getType();
}
