package com.tl.core.rule;

import com.tl.core.TemplateField;

/**
 * TemplateRule
 * 模板配置规则
 * @author WangPanYong
 * @since 2023/07/18
 */
public interface TemplateRule {

	/**
	 * @return java.lang.String 标签前缀
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	String prefix();

	/**
	 * @return java.lang.String 标签后缀
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	String suffix();

	/**
	 *
	 * @return java.lang.String 图片字段标识
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	String picSymbol();

	/**
	 *
	 * @return java.lang.String 匹配模板字段正则
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	String regexField();

	/**
	 * 例如：{{name(p1)(p2)}} , 提取其中 p1,p2
	 *
	 * @return java.lang.String 匹配模板字段的参数正则
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	String regexFieldParam();

	/**
	 *
	 * @param templateField 模板字段
	 * @return java.lang.String 配置字段内容
	 * @author Wesley
	 * @since 2023/07/27
	 **/
	String generateFullName(TemplateField templateField);

}
