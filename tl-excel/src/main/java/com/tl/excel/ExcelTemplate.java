package com.tl.excel;

import com.tl.excel.config.ExcelConfig;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;

/**
 * ExcelTemplate
 * 模板对象
 * @author Wesley
 * @since 2023/07/10 10:28
 */
public class ExcelTemplate {

	private ExcelConfig config;
	private final XSSFWorkbook workbook;

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
				return template;
			}
			throw new IllegalArgumentException();
		} catch (IOException e) {
			throw new IllegalArgumentException();
		}
//		try {
//			return compile(new FileInputStream(templateFile), config);
//		} catch (FileNotFoundException e) {
//			throw new ResolverException("Cannot find the file [" + templateFile.getPath() + "]", e);
//		}
	}
}
