package com.wesley.word.render;

import cn.hutool.core.util.StrUtil;
import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.data.PictureRenderData;
import com.tl.core.data.TextRenderData;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;
import com.wesley.word.config.WordConfig;
import com.wesley.word.util.WordUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ParagraphRender
 *
 * @author WangPanYong
 * @since 2024/09/18
 */
public class ParagraphRender extends AbstractWordRender{

	public ParagraphRender(WordConfig wordConfig, TemplateRule templateRule) {
		super(wordConfig, templateRule);
	}

	@Override
	public void render(XWPFDocument document, List<TemplateField> templateFields, RenderDataFinder dataFinder) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		Map<String, TemplateField> fieldMap = templateFields.stream().collect(Collectors.toMap(TemplateField::getLocation, e -> e));
		paragraphs.forEach(paragraph -> this.doRender(paragraph, fieldMap, dataFinder));
	}

	void doRender(XWPFParagraph paragraph, Map<String, TemplateField> fieldMap, RenderDataFinder dataFinder) {
		CTP ctp = paragraph.getCTP();
		int bookmarkSize = ctp.sizeOfBookmarkStartArray();
		for (int i = 0; i < bookmarkSize; i++) {
			CTBookmark bookmarkStart = ctp.getBookmarkStartArray(i);
			String name = bookmarkStart.getName();
			if (!StrUtil.startWith(name, WordConfig.BOOKMARK_PREFIX)) {
				continue;
			}
			TemplateField field = fieldMap.get(name);
			if (field == null) {
				continue;
			}
			List<XWPFRun> bookmarkRuns = WordUtil.findBookmarkRuns(paragraph, bookmarkStart);
			if (bookmarkRuns.isEmpty()) {
				return;
			}
			TLFieldType fieldType = field.getType();
			if (TLFieldType.PICTURE.equals(fieldType)) {
				PictureRenderData picture = dataFinder.findPicture(field);
				if (Objects.nonNull(picture)) {
					// TODO: 2024/9/18 添加图片
				}
			}else {
				TextRenderData text = dataFinder.findText(field);
				XWPFRun run = bookmarkRuns.get(0);
				run.setText(text.getText(), 0);
				// TODO: 2024/9/18 处理换行符
			}
			// 清除其它run中的值
			bookmarkRuns.stream().skip(1).forEach(WordUtil::clearRun);
		}
	}


}
