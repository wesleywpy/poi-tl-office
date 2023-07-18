package com.tl.excel.render;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tl.core.Resolver;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;
import com.tl.excel.exception.ExcelResolveException;
import com.tl.excel.resolver.ExcelField;
import com.tl.excel.rule.ExcelTemplateRule;
import com.tl.excel.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ExcelResolver
 *
 * @author Wesley
 * @since 2023/07/17
 */
public class ExcelResolver implements Resolver {

	private final ExcelConfig config;

	private final TemplateRule templateRule = new ExcelTemplateRule();

	public ExcelResolver(ExcelConfig config) {
		this.config = config;
	}

	/**
	 *
	 * @param workbook {@link XSSFWorkbook}
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	public List<ExcelField> resolve(XSSFWorkbook workbook) {
		List<ExcelField> excelFields = doResolve(workbook);
		// TODO: 2023/7/18 提取页眉页脚字段
		return excelFields;
	}

	/**
	 *
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	private List<ExcelField> doResolve(XSSFWorkbook workbook) {
		List<ExcelField> result = CollUtil.newArrayList();
		int numberOfSheets = workbook.getNumberOfSheets();

		Pattern pattern = Pattern.compile(templateRule.regexField());
		Pattern paramPattern = Pattern.compile(templateRule.regexFieldParam());
		for (int i = 0; i < numberOfSheets; i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			int lastRowNum = sheet.getLastRowNum();
			// 读取数据
			for (int rowIdx = 0; rowIdx < lastRowNum; rowIdx++) {
				XSSFRow row = sheet.getRow(rowIdx);
				if (Objects.isNull(row)) {
					continue;
				}
				int lastCellNum = row.getLastCellNum();
				for (int colIdx = 0; colIdx < lastCellNum; colIdx++) {
					XSSFCell cell = row.getCell(colIdx);
					if (Objects.isNull(cell)) {
						continue;
					}
					String cellVal = ExcelUtil.getCellStringValue(cell);
					if (StrUtil.isEmpty(cellVal)) {
						continue;
					}
					Matcher matcher = pattern.matcher(cellVal);
					while (matcher.find()) {
						String content = matcher.group(1);
						ExcelField excelField = build(content, paramPattern);

					}
				}
			}
		}
		return result;
	}

	/**
	 *
	 * @param fieldContent 模板字段内容
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	private ExcelField build(String fieldContent, Pattern paramPattern) {
		ExcelField field = new ExcelField();
		field.setContent(fieldContent);

		String content = fieldContent;
		Matcher matcher = paramPattern.matcher(fieldContent);
		try {
			List<String> params = new ArrayList<>();
			while (matcher.find()) {
				params.add(matcher.group(1));
			}
			field.setParams(params);
			content = content.replaceAll(templateRule.regexFieldParam(), StrUtil.EMPTY);
		} catch (Exception e) {
			throw new ExcelResolveException("Resolving excel template param error. "+ e.getMessage(), e);
		}

		String picSymbol = Optional.ofNullable(templateRule.picSymbol()).orElse(ExcelTemplateRule.DEFAULT_PIC_SYMBOL);
		if (StrUtil.startWith(content, picSymbol)) {
			field.setFieldType(TLFieldType.PICTURE);
			content = StrUtil.subSuf(content, picSymbol.length());
		}
		field.setName(content);
		return field;
	}

}
