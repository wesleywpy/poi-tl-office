package com.wesley.word.render;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.rule.TemplateRule;
import com.wesley.word.config.WordConfig;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.ArrayList;
import java.util.List;

/**
 * TableRender
 *
 * @author WangPanYong
 * @since 2024/11/08
 */
public class TableRender extends AbstractWordRender{

	public TableRender(WordConfig wordConfig, TemplateRule templateRule) {
		super(wordConfig, templateRule);
	}

	@Override
	public void render(XWPFDocument document, List<TemplateField> templateFields, RenderDataFinder dataFinder) {


	}



}
