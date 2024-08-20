package com.tl.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * GroupRenderData
 *
 * @author WangPanYong
 * @since 2024/08/06
 */
public class GroupRenderData {
	final String fieldName;
	final String group;

	final List<TextRenderData> renderDataList;
	final List<PictureRenderData> picRenderData;

	final List<RenderData> allData;

	GroupRenderData(String fieldName, String group) {
		this.fieldName = fieldName;
		this.group = group;
		this.renderDataList = new ArrayList<>();
		this.picRenderData = new ArrayList<>();
		this.allData = new ArrayList<>();
	}

	void add(RenderData renderData) {
		if (renderData instanceof PictureRenderData data) {
			picRenderData.add(data);
		} else if (renderData instanceof TextRenderData data){
			renderDataList.add(data);
		}
		allData.add(renderData);
	}

	List<RenderData> all() {
		return allData;
	}
}
