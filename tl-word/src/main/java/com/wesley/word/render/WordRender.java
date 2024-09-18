package com.wesley.word.render;

import com.tl.core.Render;
import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;

/**
 * WordRender
 *
 * @author Wesley
 * @since 2024/09/18
 */
public interface WordRender extends Render {

	/**
	 * render
	 *
	 * @author Wesley
	 * @since 2024/09/18
	 **/
	void render(XWPFDocument document, List<TemplateField> templateFields, RenderDataFinder dataFinder);
}
