package com.tl.core.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tl.core.RenderDataFinder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * RenderDataBuilder
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
@Getter
public class RenderDataBuilder {

	private Table<String, String, GroupRenderData> dataTable = HashBasedTable.create();;

	@Setter
	String defaultGroupName = "TLGroup";

	public RenderDataBuilder map(Map<String, Object> mapModel){
		mapModel.forEach((key, val) -> this.object(defaultGroupName, key, val));
		return this;
	}

	RenderDataBuilder object(String group, String key, Object model) {
		if (model == null) {
			return this;
		}

		GroupRenderData groupData = Optional.ofNullable(dataTable.get(group, key)).orElse(new GroupRenderData(group, key));
		Class<?> modelClass = model.getClass();
		if (model instanceof RenderData) {
			groupData.add((RenderData) model);
			dataTable.put(group, key, groupData);
		} else {
			groupData.add(new TextRenderData(model.toString()));
			dataTable.put(group, key, groupData);
		}
		return this;
	}

	/**
	 *
	 * @return com.tl.core.RenderDataFinder
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	public RenderDataFinder buildFinder() {
		return new DefaultRenderDataFinder(dataTable, defaultGroupName);
	}

	/**
	 *
	 * @param model 数据模型
	 * @return com.tl.core.data.RenderDataBuilder
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	public static RenderDataBuilder of(Object model) {
		RenderDataBuilder builder = new RenderDataBuilder();
		if (model instanceof Map) {
			builder.map((Map<String,Object>)model);
		}
		return builder;
	}
}
