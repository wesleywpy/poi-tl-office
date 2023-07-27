package com.tl.excel;

import com.tl.core.RenderDataFinder;
import com.tl.core.data.RenderDataBuilder;
import com.tl.core.exception.TLException;
import com.tl.excel.config.ExcelConfig;
import com.tl.excel.render.CellExcelRender;
import com.tl.excel.render.ExcelRender;
import com.tl.excel.resolver.ExcelField;
import com.tl.excel.resolver.ExcelResolver;
import com.tl.excel.rule.ExcelTemplateRule;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ExcelTemplate
 * 模板对象
 * @author Wesley
 * @since 2023/07/10
 */
public class ExcelTemplate {

	private ExcelConfig config;
	private final XSSFWorkbook workbook;

	private ExcelResolver resolver;

	private List<ExcelField> excelFields;
	private List<ExcelRender> excelRenders = new ArrayList<>();

	public ExcelTemplate(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	/**
	 *
	 * @param path 文件路径
	 * @return com.tl.excel.ExcelTemplate
	 * @author WangPanYong
	 * @since 2023/07/12 09:49
	 **/
	public static ExcelTemplate compile(String path) {
		return compile(new File(path));
	}

	/**
	 *
	 * @param templateFile 模板文件
	 * @return com.tl.excel.ExcelTemplate
	 * @author Wesley
	 * @since 2023/07/12
	 **/
	public static ExcelTemplate compile(File templateFile) {
		return compile(templateFile, ExcelConfig.createDefault());
	}

	/**
	 *
	 * @param templateFile 模板文件
	 * @param config 配置参数
	 * @return com.tl.excel.ExcelTemplate
	 * @author Wesley
	 * @since 2023/07/12
	 **/
	public static ExcelTemplate compile(File templateFile, ExcelConfig config) {
		try {
			Workbook workbook = WorkbookFactory.create(templateFile);
			if (workbook instanceof XSSFWorkbook) {
				XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
				ExcelTemplate template = new ExcelTemplate(xssfWorkbook);
				template.config = config;
				ExcelTemplateRule excelTemplateRule = new ExcelTemplateRule();
				template.resolver = new ExcelResolver(config, excelTemplateRule);
				template.excelRenders.add(new CellExcelRender(config, excelTemplateRule));
				return template;
			}
			throw new TLException("ExcelTemplate only supports .xlsx or .xlsm format");
		} catch (IOException e) {
			throw new TLException(e.getMessage(), e);
		}
	}

	/**
	 * 解析出模板中配置的字段
	 *
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	public List<ExcelField> resolve() {
		this.excelFields = this.resolver.resolve(this.workbook);
		return excelFields;
	}

	/**
	 *
	 * @param model 数据模型
	 * @author Wesley
	 * @since 2023/07/20
	 **/
	public ExcelTemplate render(Object model) {
		if (this.excelFields == null) {
			this.resolve();
		}
		RenderDataFinder renderDataFinder = RenderDataBuilder.of(model).buildFinder();
		for (ExcelRender excelRender : excelRenders) {
			excelRender.render(workbook, this.excelFields, renderDataFinder);
		}
		return this;
	}

	/**
	 *
	 * @param out {@link OutputStream}
	 * @author Wesley
	 * @since 2023/07/27
	 **/
	public void write(OutputStream out) throws IOException {
		this.workbook.write(out);
		out.flush();
	}

}
