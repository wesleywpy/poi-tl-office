package com.wesley.word.render;

import com.tl.core.TemplateField;
import com.tl.core.data.PictureRenderData;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

/**
 * WordPictureWriter
 *
 * @author WangPanYong
 * @since 2025/04/17
 */
public interface WordPicturePainter {

	/**
	 * add
	 *
	 * @param field 字段
	 * @param picture 图片
	 * @param runs 所在位置
	 * @author WangPanYong
	 * @since 2025/04/17
	 **/
	void add(TemplateField field, PictureRenderData picture, List<XWPFRun> runs, int width, int height);
}
