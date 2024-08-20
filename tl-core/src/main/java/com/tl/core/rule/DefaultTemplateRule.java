package com.tl.core.rule;

import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * DefaultTemplateRule
 *
 * @author WangPanYong
 * @since 2024/08/20
 */
public class DefaultTemplateRule implements TemplateRule {

	public static final String DEFAULT_PIC_SYMBOL = "#";

	private static final String PREFIX = "{{";

	private static final String SUFFIX = "}}";

	private final Map<TLFieldType, String> fieldSymbols = new HashMap<>();

	public DefaultTemplateRule() {
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
