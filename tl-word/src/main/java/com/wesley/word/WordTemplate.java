package com.wesley.word;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.exception.TLException;
import com.wesley.word.config.WordConfig;
import com.wesley.word.render.ParagraphRender;
import com.wesley.word.render.WordRender;
import com.wesley.word.resolver.WordResolver;
import com.wesley.word.rule.WordTemplateRule;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * WordTemplate
 *
 * @author WangPanYong
 * @since 2024/08/20
 */
public class WordTemplate {

	private final XWPFDocument document;

	private List<TemplateField> templateFields;
	private final List<WordRender> renders;
	private WordResolver resolver;

	public WordTemplate(XWPFDocument document) {
		this.document = document;
		this.renders = new ArrayList<>();
	}

	/**
	 * 解析出模板中配置的字段
	 *
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	public List<TemplateField> resolve() {
		this.templateFields = this.resolver.resolve(this.document);
		return templateFields;
	}

	/**
	 *
	 * @param renderDataFinder {@link RenderDataFinder}
	 * @author Wesley
	 * @since 2024/09/20
	 **/
	public WordTemplate render(RenderDataFinder renderDataFinder) {
		if (Objects.isNull(renderDataFinder)) {
			return this;
		}
		if (this.templateFields == null) {
			this.resolve();
		}
		for (WordRender excelRender : renders) {
			excelRender.render(document, this.templateFields, renderDataFinder);
		}
		return this;
	}

	/**
	 *
	 * @param templateFile 模板文件
	 * @param config 配置参数
	 * @return com.tl.excel.ExcelTemplate
	 * @author Wesley
	 * @since 2023/07/12
	 **/
	public static WordTemplate compile(File templateFile, WordConfig config) {
		try (FileInputStream fileInputStream = new FileInputStream(templateFile)){
			XWPFDocument document = new XWPFDocument(fileInputStream);
			WordTemplate wordTemplate = new WordTemplate(document);
			WordTemplateRule wordTemplateRule = new WordTemplateRule();
			wordTemplate.resolver = new WordResolver(config, wordTemplateRule);
			wordTemplate.renders.add(new ParagraphRender(config, wordTemplateRule));
			return wordTemplate;
		} catch (IOException e) {
			throw new TLException(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param out {@link OutputStream}
	 * @author Wesley
	 * @since 2024/09/06
	 **/
	public void write(OutputStream out) throws IOException {
		this.document.write(out);
		out.flush();
	}
}
