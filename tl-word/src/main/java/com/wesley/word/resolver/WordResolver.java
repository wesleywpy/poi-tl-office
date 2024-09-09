package com.wesley.word.resolver;

import com.tl.core.Resolver;
import com.tl.core.TemplateField;
import com.tl.core.exception.TLException;
import com.tl.core.rule.TemplateRule;
import com.wesley.word.config.WordConfig;
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
import java.util.ArrayList;
import java.util.List;
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
			String paragraphText = paragraph.getText();
			Matcher matcher = pattern.matcher(paragraphText);
			PositionInParagraph position = new PositionInParagraph();

			List<TextSegment> segments = new ArrayList<>();
			while (matcher.find()) {
				String fieldContent = matcher.group();
				TextSegment textSegment = paragraph.searchText(fieldContent, position);
				if (textSegment != null) {
					segments.add(textSegment);
					position.setRun(textSegment.getEndRun());
					position.setText(textSegment.getEndText());
					position.setChar(textSegment.getEndChar());
				}
				System.out.println(fieldContent);
			}
			addBookmark(paragraph, segments, bookmarkIdOffset);
			bookmarkIdOffset += segments.size();
		}
		return result;
	}


	/**
	 * addBookmark
	 *
	 * @param paragraph 段落
	 * @param segments 文本位置
	 * @param bookmarkIdOffset 书签Id偏移
	 * @author Wesley
	 * @since 2024/09/06
	 **/
	private void addBookmark(XWPFParagraph paragraph, List<TextSegment> segments, int bookmarkIdOffset) {
		if (segments.isEmpty()) {
			return;
		}
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
			for (TextSegment segment : segments) {
				int beginRunIdx = segment.getBeginRun();
				// 两个字段中间的文本
				if (currRunIdx != beginRunIdx) {
					for (int i = currRunIdx; i < beginRunIdx; i++) {
						WordUtil.copyRun(runs.get(i), paragraph.createRun(), null);
					}
				}

				XWPFRun beginRun = runs.get(beginRunIdx);
				if (segment.getEndRun() != beginRunIdx && segment.getBeginChar() > 0) {
					// aaa{{ 情况需要拆分run
					String beginText = beginRun.text();
					String text1 = beginText.substring(0, segment.getBeginChar());
					String text2 = beginText.substring(segment.getBeginChar(), beginText.length() - 1);
					WordUtil.copyRun(beginRun, paragraph.createRun(), text1);

					CTBookmark bookmarkStart = ctp.addNewBookmarkStart();
					bookmarkStart.setName(WordConfig.BOOKMARK_PREFIX.concat(String.valueOf(idOffset)));
					bookmarkStart.setId(BigInteger.valueOf(idOffset));
					WordUtil.copyRun(beginRun, paragraph.createRun(), text2);
				} else {
					CTBookmark bookmarkStart = ctp.addNewBookmarkStart();
					bookmarkStart.setName(WordConfig.BOOKMARK_PREFIX.concat(String.valueOf(idOffset)));
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
		}catch (Exception e) {
			throw new TLException(e.getMessage(), e);
		}
	}

	/**
	 * getRunText
	 * @param run Text区域
	 * @return java.lang.String
	 * @author WangPanYong
	 **/
	private String getRunText(XWPFRun run) {
		List<CTText> ctTexts = run.getCTR().getInstrTextList();
		StringBuilder textBuilder = new StringBuilder();
		if (CollectionUtils.isNotEmpty(ctTexts)) {
			textBuilder.append(
				ctTexts.stream()
					   .map(XmlAnySimpleType::getStringValue)
					   .collect(Collectors.joining())
			);
		} else {
			textBuilder.append(run.text());
		}
		return textBuilder.toString();
	}
}
