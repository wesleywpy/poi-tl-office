package com.wesley.word.render;

import com.tl.core.TemplateField;
import com.tl.core.data.RenderData;

/**
 * WordTextPainter
 *
 * @author WangPanYong
 * @since 2025/04/28
 */
public interface WordDataWriter {

	/**
	 * write
	 *
	 * @param field 模板字段
	 * @param renderData 渲染数据
	 * @param fragment 内容片段
	 * @author Wesley
	 * @since 2025/04/28
	 **/
	void write(TemplateField field, RenderData renderData, WordFragment fragment);
}
