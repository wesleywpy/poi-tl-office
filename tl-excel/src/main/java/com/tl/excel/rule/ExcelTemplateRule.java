package com.tl.excel.rule;

import com.tl.core.rule.TemplateRule;

/**
 * ExcelTemplateRule
 *
 * @author WangPanYong
 * @since 2023/07/18
 */
public class ExcelTemplateRule implements TemplateRule {

	public static final String DEFAULT_PIC_SYMBOL = "@";

	private final String prefix = "{{";

	private final String suffix = "}}";

	private final String picSymbol = DEFAULT_PIC_SYMBOL;

	@Override
	public String prefix() {
		return prefix;
	}

	@Override
	public String suffix() {
		return suffix;
	}

	@Override
	public String picSymbol() {
		return picSymbol;
	}

	@Override
	public String regexField() {
		return "\\{\\{(.*?)\\}\\}";
	}

	@Override
	public String regexFieldParam() {
		return "\\(([^\\)]\\w*?)\\)";
	}
}
