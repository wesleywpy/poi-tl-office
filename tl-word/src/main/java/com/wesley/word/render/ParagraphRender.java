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
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
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

		var paragraphConsumer = defaultRunConsumer(dataFinder, 0, 0);
		paragraphs.forEach(paragraph -> this.doRender(paragraph, fieldMap, paragraphConsumer));
		document.getTables().forEach(table -> this.renderTable(table, fieldMap, dataFinder));

		// 页眉页脚表格
		List<XWPFHeaderFooter> headerFooters = new ArrayList<>();
		headerFooters.addAll(document.getHeaderList());
		headerFooters.addAll(document.getFooterList());
		for (XWPFHeaderFooter headerFooter : headerFooters) {
			headerFooter.getParagraphs().forEach(p -> this.doRender(p, fieldMap, paragraphConsumer));
			headerFooter.getTables().forEach(t -> renderTable(t, fieldMap, dataFinder));
		}
	}

	void renderTable(XWPFTable table, Map<String, TemplateField> fieldMap, RenderDataFinder dataFinder) {
		List<XWPFTableRow> rows = table.getRows();
		for (XWPFTableRow row : rows) {
			List<XWPFTableCell> tableCells = row.getTableCells();
			for (XWPFTableCell tableCell : tableCells) {
				var consumer = defaultRunConsumer(dataFinder, tableCell.getWidth(), row.getHeight());
				for (XWPFParagraph paragraph : tableCell.getParagraphs()) {
					doRender(paragraph, fieldMap, consumer);
				}
			}
		}
	}

	BiConsumer<TemplateField, List<XWPFRun>> defaultRunConsumer(RenderDataFinder dataFinder, int width, int height){
		return (field, runs) -> {
			TLFieldType fieldType = field.getType();
			if (TLFieldType.PICTURE.equals(fieldType)) {
				PictureRenderData picture = dataFinder.findPicture(field);
				if (Objects.nonNull(picture)) {
					XWPFRun run = runs.get(0);
					try (ByteArrayInputStream is = new ByteArrayInputStream(picture.read())) {
						// TODO: 2024/11/8 图片大小配置
						run.addPicture(is, picture.picType(), field.getName(), Units.toEMU((double) width / 21), Units.toEMU((double) height / 21));
					} catch (IOException | InvalidFormatException e) {
						String msg = "Word添加图片失败! " + e.getMessage();
						// TODO: 2024/11/8 异常处理
						e.printStackTrace();
					}
					WordUtil.clearRun(run);
				}
			}else {
				TextRenderData text = dataFinder.findText(field);
				XWPFRun run = runs.get(0);
				run.setText(text.getText(), 0);
				// TODO: 2024/9/18 处理换行符
			}
			// 清除其它run中的值
			runs.stream().skip(1).forEach(WordUtil::clearRun);
		};
	}

	void doRender(XWPFParagraph paragraph, Map<String, TemplateField> fieldMap, BiConsumer<TemplateField, List<XWPFRun>> consumer) {
		if (consumer == null) {
			return;
		}
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
				continue;
			}
			consumer.accept(field, bookmarkRuns);
		}
	}


}
