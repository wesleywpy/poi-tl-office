package com.tl.excel.resolver;

import cn.hutool.core.util.StrUtil;
import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * ExcelField
 *
 * @author Wesley
 * @since 2023/07/13
 */
@Setter
@EqualsAndHashCode(of = {"name","location"})
@ToString
public class ExcelField implements TemplateField {

	private String name;
	private String content;
	/**
	 * sheetIndex_rowIndex_colIndex
	 */
	private ExcelLocator location;
	private List<String> params;
	private TLFieldType fieldType = TLFieldType.TEXT;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getLocation() {
		return Objects.nonNull(location) ? location.toString() : StrUtil.EMPTY;
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
