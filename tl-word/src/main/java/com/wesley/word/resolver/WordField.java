package com.wesley.word.resolver;

import cn.hutool.core.util.StrUtil;
import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * WordField
 *
 * @author Wesley
 * @since 2024/08/30
 */
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"name","group"})
@ToString
public class WordField implements TemplateField {

	protected String name;
	protected String group;
	protected String content;
	protected List<String> params;
	protected TLFieldType fieldType = TLFieldType.TEXT;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getLocation() {
		return StrUtil.EMPTY;
	}

	@Override
	public List<String> getParams() {
		return params;
	}

	@Override
	public TLFieldType getType() {
		return fieldType;
	}


}
