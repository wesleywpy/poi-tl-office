package com.tl.excel.render.name;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.render.NameExcelRender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ListNameHandler
 *
 * @author WangPanYong
 * @since 2024/07/15
 */
public class ListNameHandler implements NameHandler{

	private final NameExcelRender nameRender;

	/**
	 * 名称管理器前缀：list渲染
	 */
	public static final String NAME_PREFIX = "TL_list_";

    public ListNameHandler(NameExcelRender nameRender) {
        this.nameRender = nameRender;
    }

    @Override
	public boolean support(NameWrapper nameWrapper) {
		return nameWrapper.getNameName().startsWith(NAME_PREFIX);
	}

	@Override
	public void handle(NameWrapper nameWrapper, List<TemplateField> templateFields, RenderDataFinder dataFinder) {
		String groupName = nameWrapper.getNameName().substring(NAME_PREFIX.length());
		List<TemplateField> fields = templateFields.stream()
												   .filter(f -> groupName.equals(f.getGroup()))
												   .collect(Collectors.toList());
		if (fields.isEmpty()) {
			return;
		}
		List<NameRefRow> nameRefRows = nameWrapper.analyseRefRange(nameRender.getTemplateRule());
		// TODO: 2024/8/6 实现List填充
		System.out.println(nameRefRows.size());
	}
}
