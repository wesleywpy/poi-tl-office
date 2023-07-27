package com.tl.excel.rule;

import cn.hutool.core.text.StrBuilder;
import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;

import java.util.Optional;

/**
 * ExcelTemplateRule
 *
 * @author WangPanYong
 * @since 2023/07/18
 */
public class ExcelTemplateRule implements TemplateRule {

	public static final String DEFAULT_PIC_SYMBOL = "#";

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

	@Override
	public String generateFullName(TemplateField templateField) {
		StringBuilder builder = new StringBuilder(prefix);
		if (TLFieldType.PICTURE.equals(templateField.getType())) {
			builder.append(DEFAULT_PIC_SYMBOL);
		}
		builder.append(templateField.getName());
		Optional.ofNullable(templateField.getParams())
				.ifPresent(params -> params.forEach(p -> builder.append("(").append(p).append(")")));
		builder.append(suffix);
		return builder.toString();
	}
}
