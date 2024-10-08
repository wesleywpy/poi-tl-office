package com.tl.core;

import com.tl.core.data.PictureRenderData;
import com.tl.core.data.RenderData;
import com.tl.core.data.StringRenderData;
import com.tl.core.data.TextRenderData;

import java.util.List;

/**
 * RenderDataFinder
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public interface RenderDataFinder {

	List<RenderData> findAll(TemplateField field);

	/**
	 *
	 * @param field {@link TemplateField}
	 * @return com.tl.core.data.TextRenderData
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	default TextRenderData findText(TemplateField field) {
		List<TextRenderData> texts = findTexts(field);
		if (texts == null || texts.isEmpty()) {
			return new TextRenderData();
		}
		return texts.get(0);
	}

	/**
	 *
	 * @param field {@link TemplateField}
	 * @return com.tl.core.data.PictureRenderData
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	default PictureRenderData findPicture(TemplateField field) {
		List<PictureRenderData> pictures = findPictures(field);
		if (pictures == null || pictures.isEmpty()) {
			return null;
		}
		return pictures.get(0);
	}

	/**
	 *
	 * @param field {@link TemplateField}
	 * @return java.util.List<com.tl.core.data.TextRenderData>
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	List<TextRenderData> findTexts(TemplateField field);

	/**
	 *
	 * @param field {@link TemplateField}
	 * @return java.util.List<com.tl.core.data.PictureRenderData>
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	List<PictureRenderData> findPictures(TemplateField field);

	/**
	 * 最大行数
	 * @param group 分组名称
	 * @return int
	 * @author Wesley
	 * @since 2024/08/07
	 **/
	int getMaxSize(String group);

}
