package com.wesley.word;

import com.tl.core.TemplateField;
import com.tl.core.exception.TLException;
import com.wesley.word.config.WordConfig;
import com.wesley.word.resolver.WordResolver;
import com.wesley.word.rule.WordTemplateRule;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * WordTemplate
 *
 * @author WangPanYong
 * @since 2024/08/20
 */
public class WordTemplate {

	private final XWPFDocument document;

	private List<TemplateField> templateFields;

	private WordResolver resolver;

	public WordTemplate(XWPFDocument document) {
		this.document = document;
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
			wordTemplate.resolver = new WordResolver(config, new WordTemplateRule());
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
