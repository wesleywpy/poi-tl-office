package com.wesley.word.resolver;

import cn.hutool.core.util.StrUtil;
import com.tl.core.Resolver;
import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import com.tl.core.exception.TLException;
import com.tl.core.rule.TemplateRule;
import com.wesley.word.config.WordConfig;
import com.wesley.word.rule.WordTemplateRule;
import com.wesley.word.util.WordUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTPImpl;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * WordResolver
 *
 * @author WangPanYong
 * @since 2024/08/30
 */
public class WordResolver implements Resolver {

	private final WordConfig config;

	private final TemplateRule templateRule;

	private final Pattern pattern;
	private final Pattern paramPattern;

	public WordResolver(WordConfig config, TemplateRule templateRule) {
		this.config = config;
		this.templateRule = templateRule;
		pattern = Pattern.compile(templateRule.regexField());
		paramPattern = Pattern.compile(templateRule.regexFieldParam());
	}

	/**
	 *
	 * @param document {@link XWPFDocument}
	 * @return java.util.List<com.wesley.word.resolver.WordField>
	 * @author Wesley
	 * @since 2024/08/30
	 **/
	public List<TemplateField> resolve(XWPFDocument document) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		List<TemplateField> result = new ArrayList<>();
		int bookmarkIdOffset = 0;
		for (XWPFParagraph paragraph : paragraphs) {
			result.addAll(doResolve(paragraph, bookmarkIdOffset));
			bookmarkIdOffset = result.size();
		}

		List<XWPFTable> tables = document.getTables();
		for (XWPFTable table : tables) {
			result.addAll(resolveTable(table, bookmarkIdOffset));
			bookmarkIdOffset = result.size();
		}
		return result;
	}

	/**
	 * 处理表格
	 * @param bookmarkIdOffset 书签Id偏移量
	 */
	List<TemplateField> resolveTable(XWPFTable table, int bookmarkIdOffset) {
		List<TemplateField> result = new ArrayList<>();
		if (Objects.isNull(table)) {
			return result;
		}

		List<XWPFTableRow> rows = table.getRows();
		List<XWPFTableCell> cells;
		int offsetId = bookmarkIdOffset;
		for (XWPFTableRow row : rows) {
			cells = row.getTableCells();
			for (XWPFTableCell cell : cells) {
				for (XWPFParagraph paragraph : cell.getParagraphs()) {
					List<TemplateField> fieldList = doResolve(paragraph, offsetId);
					result.addAll(fieldList);
					offsetId = offsetId + fieldList.size();
				}
			}
		}
		return result;
	}

	/**
	 * doResolve
	 *
	 * @param paragraph 段落
	 * @param bookmarkIdOffset 书签Id偏移
	 * @return java.util.List<com.tl.core.TemplateField>
	 * @author Wesley
	 * @since 2024/09/14
	 **/
	private List<TemplateField> doResolve(XWPFParagraph paragraph, int bookmarkIdOffset) {
		String paragraphText = paragraph.getText();
		Matcher matcher = pattern.matcher(paragraphText);
		PositionInParagraph position = new PositionInParagraph();

		Map<String, TextSegment> segmentMap = new LinkedHashMap<>();
		while (matcher.find()) {
			String fieldContent = matcher.group();
			TextSegment textSegment = paragraph.searchText(fieldContent, position);
			if (textSegment != null) {
				segmentMap.put(matcher.group(1), textSegment);
				position.setRun(textSegment.getEndRun());
				position.setText(textSegment.getEndText());
				position.setChar(textSegment.getEndChar());
			}
		}

		return addBookmark(paragraph, segmentMap, bookmarkIdOffset);
	}


	/**
	 * addBookmark
	 *
	 * @param paragraph 段落
	 * @param segmentMap 文本位置
	 * @param bookmarkIdOffset 书签Id偏移
	 * @author Wesley
	 * @since 2024/09/06
	 **/
	private List<TemplateField> addBookmark(XWPFParagraph paragraph, Map<String, TextSegment> segmentMap, int bookmarkIdOffset) {
		if (segmentMap.isEmpty()) {
			return new ArrayList<>();
		}
		List<TextSegment> segments = new ArrayList<>(segmentMap.values());
		List<XWPFRun> runs = paragraph.getRuns();
		int initSize = runs.size();
		int currRunIdx = segments.get(0).getBeginRun();
		// 先添加标识符前面的run
		for (int i = 0; i < currRunIdx; i++) {
			XWPFRun targetRun = paragraph.createRun();
			WordUtil.copyRun(runs.get(i), targetRun, null);
		}

		int idOffset = bookmarkIdOffset;
		CTP ctp = paragraph.getCTP();
		try {
			List<TemplateField> result = new ArrayList<>();
			for (Map.Entry<String, TextSegment> segmentEntry : segmentMap.entrySet()) {
				TextSegment segment = segmentEntry.getValue();
				int beginRunIdx = segment.getBeginRun();
				// 两个字段中间的文本
				if (currRunIdx != beginRunIdx) {
					for (int i = currRunIdx; i < beginRunIdx; i++) {
						WordUtil.copyRun(runs.get(i), paragraph.createRun(), null);
					}
				}

				XWPFRun beginRun = runs.get(beginRunIdx);
				String bookmarkName = WordConfig.BOOKMARK_PREFIX.concat(String.valueOf(idOffset));
				if (segment.getEndRun() != beginRunIdx && segment.getBeginChar() > 0) {
					// aaa{{ 情况需要拆分run
					String beginText = beginRun.text();
					String text1 = beginText.substring(0, segment.getBeginChar());
					String text2 = beginText.substring(segment.getBeginChar(), beginText.length() - 1);
					WordUtil.copyRun(beginRun, paragraph.createRun(), text1);

					CTBookmark bookmarkStart = ctp.addNewBookmarkStart();
					bookmarkStart.setName(bookmarkName);
					bookmarkStart.setId(BigInteger.valueOf(idOffset));
					WordUtil.copyRun(beginRun, paragraph.createRun(), text2);
				} else {
					CTBookmark bookmarkStart = ctp.addNewBookmarkStart();
					bookmarkStart.setName(bookmarkName);
					bookmarkStart.setId(BigInteger.valueOf(idOffset));
					WordUtil.copyRun(beginRun, paragraph.createRun(), null);
				}

				for (int i = beginRunIdx + 1; i < segment.getEndRun(); i++) {
					WordUtil.copyRun(runs.get(i), paragraph.createRun(), null);
				}

				XWPFRun endRun = runs.get(segment.getEndRun());
				String endText = endRun.text();
				if ((endText.length() - 1) != segment.getEndChar()) {
					// }}bbb 情况需要拆分
					String text1 = endText.substring(0, segment.getEndChar());
					WordUtil.copyRun(endRun, paragraph.createRun(), text1);
					CTMarkupRange bookmarkEnd = ctp.addNewBookmarkEnd();
					bookmarkEnd.setId(BigInteger.valueOf(idOffset));

					String text2 = endText.substring(segment.getEndChar(), endText.length() - 1);
					WordUtil.copyRun(endRun, paragraph.createRun(), text2);
				}else{
					WordUtil.copyRun(endRun, paragraph.createRun(), null);
					CTMarkupRange bookmarkEnd = ctp.addNewBookmarkEnd();
					bookmarkEnd.setId(BigInteger.valueOf(idOffset));
				}

				Optional.ofNullable(extract(segmentEntry.getKey()))
						.ifPresent(f -> {
							f.setLocation(bookmarkName)
							 .setGroup(config.getFieldDefaultGroupName());
							result.add(f);
						});
				idOffset++;
				currRunIdx = segment.getEndRun() + 1;
			}
			// 添加剩余的run
			for (int i = currRunIdx; i < initSize; i++) {
				WordUtil.copyRun(runs.get(i), paragraph.createRun(), null);
			}

			// 移除多余的run
			for (int i = 0; i < initSize; i++) {
				paragraph.removeRun(0);
			}

			return result;
		}catch (Exception e) {
			throw new TLException(e.getMessage(), e);
		}
	}

	private WordField extract(String content) {
		if (StrUtil.isEmpty(content)) {
			return null;
		}
		WordField field = new WordField();
		field.setContent(content);
		Matcher paramMatcher = paramPattern.matcher(content);
		try {
			List<String> params = new ArrayList<>();
			while (paramMatcher.find()) {
				params.add(paramMatcher.group(1));
			}
			field.setParams(params);
			if (!params.isEmpty()) {
				content = content.replaceAll(templateRule.regexFieldParam(), StrUtil.EMPTY);
			}
		} catch (Exception e) {
			throw new TLException("Resolving word template param error. " + e.getMessage(), e);
		}

		String picSymbol = Optional.ofNullable(templateRule.fieldSymbols())
								   .map(m -> m.get(TLFieldType.PICTURE))
								   .orElse(WordTemplateRule.DEFAULT_PIC_SYMBOL);
		if (StrUtil.startWith(content, picSymbol)) {
			field.setFieldType(TLFieldType.PICTURE);
			content = StrUtil.subSuf(content, picSymbol.length());
		}
		field.setName(content);
		return field;
	}

}
