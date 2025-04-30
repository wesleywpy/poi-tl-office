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
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ParagraphRender
 *
 * @author WangPanYong
 * @since 2024/09/18
 */
public class ParagraphRender extends AbstractWordRender{
	private final WordDataWriter wordPicturePainter = new DefaultPictureWriter();

	public ParagraphRender(WordConfig wordConfig, TemplateRule templateRule) {
		super(wordConfig, templateRule);
	}

	@Override
	public void render(XWPFDocument document, List<TemplateField> templateFields, RenderDataFinder dataFinder) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		Map<String, TemplateField> fieldMap = templateFields.stream().collect(Collectors.toMap(TemplateField::getLocation, e -> e));

		paragraphs.stream().map(paragraph -> this.findFragments(paragraph, fieldMap, 0, 0)).forEach(f -> doRender(dataFinder, f));
		document.getTables().forEach(table -> this.renderTable(table, fieldMap, dataFinder));

		// 页眉页脚表格
		List<XWPFHeaderFooter> headerFooters = new ArrayList<>();
		headerFooters.addAll(document.getHeaderList());
		headerFooters.addAll(document.getFooterList());
		for (XWPFHeaderFooter headerFooter : headerFooters) {
			headerFooter.getParagraphs().stream().map(p -> this.findFragments(p, fieldMap, 0, 0)).forEach(f -> doRender(dataFinder, f));
			headerFooter.getTables().forEach(t -> renderTable(t, fieldMap, dataFinder));
		}
	}

	void renderTable(XWPFTable table, Map<String, TemplateField> fieldMap, RenderDataFinder dataFinder) {
		List<XWPFTableRow> rows = table.getRows();
		for (XWPFTableRow row : rows) {
			List<XWPFTableCell> tableCells = row.getTableCells();
			for (XWPFTableCell tableCell : tableCells) {
				for (XWPFParagraph paragraph : tableCell.getParagraphs()) {
					List<WordFragment> fragments = findFragments(paragraph, fieldMap, table.getWidth(), row.getHeight());
					doRender(dataFinder, fragments);
				}
			}
		}
	}

	void doRender(RenderDataFinder dataFinder, List<WordFragment> fragments){
		for (WordFragment fragment : fragments) {
			TemplateField field = fragment.field;
			List<XWPFRun> runs = fragment.runs;
			TLFieldType fieldType = field.getType();

			if (TLFieldType.PICTURE.equals(fieldType)) {
				PictureRenderData picture = dataFinder.findPicture(field);
				wordPicturePainter.write(field, picture, fragment);
			}else {
				TextRenderData text = dataFinder.findText(field);
				XWPFRun run = runs.get(0);
				run.setText(text.getText(), 0);
				// TODO: 2024/9/18 处理换行符
			}
			// 清除其它run中的值
			runs.stream().skip(1).forEach(WordUtil::clearRun);
		}
	}

	List<WordFragment> findFragments(XWPFParagraph paragraph, Map<String, TemplateField> fieldMap, int width, int height) {
		CTP ctp = paragraph.getCTP();
		int bookmarkSize = ctp.sizeOfBookmarkStartArray();
		List<WordFragment> result = new ArrayList<>();
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
				continue;
			}
			WordFragment fragment = new WordFragment(field, paragraph, bookmarkRuns);
			fragment.width = width;
			fragment.height = height;
			result.add(fragment);
		}
		return result;
	}


}
