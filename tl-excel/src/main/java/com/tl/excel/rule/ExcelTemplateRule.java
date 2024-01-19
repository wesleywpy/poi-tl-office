package com.tl.excel.rule;

import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ExcelTemplateRule
 *
 * @author WangPanYong
 * @since 2023/07/18
 */
public class ExcelTemplateRule implements TemplateRule {

	public static final String DEFAULT_PIC_SYMBOL = "#";

	public static final String PREFIX = "{{";

	public static final String SUFFIX = "}}";

	private final Map<TLFieldType, String> fieldSymbols = new HashMap<>();

	public ExcelTemplateRule() {
		fieldSymbols.put(TLFieldType.PICTURE, DEFAULT_PIC_SYMBOL);
	}

	@Override
	public String prefix() {
		return PREFIX;
	}

	@Override
	public String suffix() {
		return SUFFIX;
	}

	@Override
	public Map<TLFieldType, String> fieldSymbols() {
		return fieldSymbols;
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
		StringBuilder builder = new StringBuilder(PREFIX);
		if (TLFieldType.PICTURE.equals(templateField.getType())) {
			builder.append(DEFAULT_PIC_SYMBOL);
		}
		builder.append(templateField.getName());
		Optional.ofNullable(templateField.getParams())
				.ifPresent(params -> params.forEach(p -> builder.append("(").append(p).append(")")));
		builder.append(SUFFIX);
		return builder.toString();
	}
}
