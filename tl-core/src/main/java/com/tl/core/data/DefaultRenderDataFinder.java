package com.tl.core.data;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Table;
import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;

import java.util.*;

/**
 * DefaultRenderDataFinder
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public class DefaultRenderDataFinder implements RenderDataFinder {

	private final Table<String, String, GroupRenderData> dataTable;

	private final String defaultGroupName;

	public DefaultRenderDataFinder(Table<String, String, GroupRenderData> dataTable, String defaultGroupName) {
		this.dataTable = dataTable;
		this.defaultGroupName = defaultGroupName;
	}

	@Override
	public List<TextRenderData> findTexts(TemplateField field) {
		return Optional.ofNullable(dataTable.get(StrUtil.emptyToDefault(field.getGroup(), defaultGroupName), field.getName()))
					   .map(g -> g.renderDataList).orElse(new ArrayList<>());
	}

	@Override
	public List<PictureRenderData> findPictures(TemplateField field) {
		return Optional.ofNullable(dataTable.get(StrUtil.emptyToDefault(field.getGroup(), defaultGroupName), field.getName()))
					   .map(g -> g.picRenderData).orElse(new ArrayList<>());
	}

	@Override
	public List<RenderData> findAll(TemplateField field) {
		return Optional.ofNullable(dataTable.get(StrUtil.emptyToDefault(field.getGroup(), defaultGroupName), field.getName()))
                       .map(GroupRenderData::all).orElse(new ArrayList<>());
	}

	@Override
	public int getMaxSize(String group) {
		String groupName = StrUtil.emptyToDefault(group, defaultGroupName);
		Map<String, GroupRenderData> row = dataTable.row(groupName);
		return row.values().stream().map(g -> g.all().size()).max(Integer::compareTo).orElse(0);
	}
}