package com.tl.core.data;

import com.tl.core.RenderDataFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RenderDataBuilder
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public class RenderDataBuilder {

	Map<String, List<TextRenderData>> textRenderDataMap = new HashMap<>();
	Map<String, List<PictureRenderData>> picRenderDataMap = new HashMap<>();


	public RenderDataBuilder map(Map<String, Object> mapModel){
		mapModel.forEach(this::object);
		return this;
	}

	RenderDataBuilder object(String key, Object model) {
		if (model == null) {
			return this;
		}

		List<TextRenderData> textList = textRenderDataMap.getOrDefault(key, new ArrayList<>());
		List<PictureRenderData> picList = picRenderDataMap.getOrDefault(key, new ArrayList<>());

		if (model instanceof TextRenderData) {
			textList.add((TextRenderData)model);
			textRenderDataMap.put(key, textList);
		} else if (model instanceof PictureRenderData) {
			picList.add((PictureRenderData)model);
			picRenderDataMap.put(key, picList);
		}
		// TODO: 2023/7/26 其它数据类型
		else {
			textList.add(new StringRenderData(model.toString()));
			textRenderDataMap.put(key, textList);
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
		return new DefaultRenderDataFinder(this.textRenderDataMap, this.picRenderDataMap);
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
