package com.tl.excel.render;

import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;
import lombok.Getter;

/**
 * AbstractExcelRender
 *
 * @author WangPanYong
 * @since 2024/01/18
 */
@Getter
public abstract class AbstractExcelRender implements ExcelRender{
	protected final ExcelConfig config;

	protected final TemplateRule templateRule;

	public AbstractExcelRender(ExcelConfig config, TemplateRule templateRule) {
		this.config = config;
		this.templateRule = templateRule;
	}


}
