package com.wesley.word.render;

import com.tl.core.TemplateField;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

/**
 * WordFragment
 * 描述Word中一个模板字段片段
 *
 * @author WangPanYong
 * @since 2025/04/28
 */
public class WordFragment {
	final TemplateField field;

	final XWPFParagraph paragraph;

	final List<XWPFRun> runs;

	public WordFragment(TemplateField field, XWPFParagraph paragraph, List<XWPFRun> runs) {
		this.field = field;
		this.paragraph = paragraph;
		this.runs = runs;
	}

	/**
	 * 宽度, 表格中才有
	 */
	int width;
	/**
	 * 高度, 表格中才有
	 */
	int height;


}
