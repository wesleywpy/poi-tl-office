package com.wesley.word.resolver;

import com.tl.core.Resolver;
import com.tl.core.TemplateField;
import com.tl.core.rule.TemplateRule;
import com.wesley.word.config.WordConfig;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @since 2023/07/17
	 **/
	public List<TemplateField> resolve(XWPFDocument document) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		for (XWPFParagraph paragraph : paragraphs) {
			String paragraphText = paragraph.getText();
			Matcher matcher = pattern.matcher(paragraphText);
			if (!matcher.find()) {
				continue;
			}
			System.out.println(paragraphText);
			List<XWPFRun> runs = paragraph.getRuns();
			System.out.println(runs.size());
		}
		List<TemplateField> fields = new ArrayList<>();
		return fields;
	}

}
