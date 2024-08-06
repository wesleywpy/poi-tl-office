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

	GroupRenderData(String fieldName, String group) {
		this.fieldName = fieldName;
		this.group = group;
		this.renderDataList = new ArrayList<>();
		this.picRenderData = new ArrayList<>();
	}

	void add(RenderData renderData) {
		if (renderData instanceof PictureRenderData) {
			picRenderData.add((PictureRenderData) renderData);
		} else if (renderData instanceof TextRenderData){
			renderDataList.add((TextRenderData) renderData);
		}
	}

	List<RenderData> all() {
		List<RenderData> result = new ArrayList<>(renderDataList.size() + picRenderData.size());
		result.addAll(renderDataList);
		result.addAll(picRenderData);
		return result;
	}
}
