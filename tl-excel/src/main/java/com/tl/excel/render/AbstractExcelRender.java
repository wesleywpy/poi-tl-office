package com.tl.excel.render;

import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;

/**
 * AbstractExcelRender
 *
 * @author WangPanYong
 * @since 2024/01/18
 */
public abstract class AbstractExcelRender implements ExcelRender{
	protected ExcelConfig config;

	protected TemplateRule templateRule;

	public AbstractExcelRender(ExcelConfig config, TemplateRule templateRule) {
		this.config = config;
		this.templateRule = templateRule;
	}
}
