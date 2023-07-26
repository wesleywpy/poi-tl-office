package com.tl.core.data;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;

import java.util.List;
import java.util.Map;

/**
 * DefaultRenderDataFinder
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public class DefaultRenderDataFinder implements RenderDataFinder {
	private final Map<String, List<TextRenderData>> textRenderDataMap;
	private final Map<String, List<PictureRenderData>> picRenderDataMap;

	public DefaultRenderDataFinder(Map<String, List<TextRenderData>> textRenderDataMap,
								   Map<String, List<PictureRenderData>> picRenderDataMap) {
		this.textRenderDataMap = textRenderDataMap;
		this.picRenderDataMap = picRenderDataMap;
	}

	@Override
	public RenderData find(TemplateField field) {
		return null;
	}

	@Override
	public PictureRenderData findPicture(TemplateField field) {
		return null;
	}

	@Override
	public List<TextRenderData> findTexts(TemplateField field) {
		return null;
	}

	@Override
	public List<PictureRenderData> findPictures(TemplateField field) {
		return null;
	}


}
