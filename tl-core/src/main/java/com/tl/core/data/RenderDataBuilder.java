package com.tl.core.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tl.core.RenderDataFinder;
import com.tl.core.TLConfig;
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

	private final Table<String, String, GroupRenderData> dataTable = HashBasedTable.create();
	@Getter
	private final TLConfig tlConfig = new TLConfig();

	public RenderDataBuilder map(Map<String, Object> mapModel){
		mapModel.forEach((key, val) -> this.add(tlConfig.getFieldDefaultGroupName(), key, val));
		return this;
	}

	public RenderDataBuilder add(String key, Object model) {
		return this.add(tlConfig.getFieldDefaultGroupName(), key, model);
	}

	public RenderDataBuilder add(String group, String key, Object model) {
		if (model == null) {
			return this;
		}
		group = Objects.isNull(group) ? tlConfig.getFieldDefaultGroupName() : group;
		GroupRenderData groupData = Optional.ofNullable(dataTable.get(group, key)).orElse(new GroupRenderData(group, key));
		if (model instanceof RenderData renderData) {
			groupData.add(renderData);
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
		return new DefaultRenderDataFinder(dataTable, tlConfig.getFieldDefaultGroupName());
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
