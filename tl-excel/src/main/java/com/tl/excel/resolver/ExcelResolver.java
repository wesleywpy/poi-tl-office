package com.tl.excel.resolver;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tl.core.Resolver;
import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;
import com.tl.excel.exception.ExcelResolveException;
import com.tl.excel.rule.ExcelTemplateRule;
import com.tl.excel.util.ExcelConstant;
import com.tl.excel.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
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

	private final TemplateRule templateRule;

	private final Pattern pattern;
	private final Pattern paramPattern;

	public ExcelResolver(ExcelConfig config, TemplateRule templateRule) {
		this.config = config;
		this.templateRule = templateRule;
		pattern = Pattern.compile(templateRule.regexField());
		paramPattern = Pattern.compile(templateRule.regexFieldParam());
	}

	/**
	 *
	 * @param workbook {@link XSSFWorkbook}
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	public List<TemplateField> resolve(XSSFWorkbook workbook) {
		return doResolve(workbook);
	}

	/**
	 *
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	private List<TemplateField> doResolve(XSSFWorkbook workbook) {
		List<TemplateField> result = CollUtil.newLinkedList();
		int numberOfSheets = workbook.getNumberOfSheets();

		for (int i = 0; i < numberOfSheets; i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			int lastRowNum = sheet.getLastRowNum();
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
					ExcelLocator excelLocator = new ExcelLocator(i, rowIdx, colIdx);
					result.addAll(findFields(cellVal, (f) -> f.setLocation(excelLocator)));
				}
			}
			result.addAll(resolveHf(sheet));
		}
		return result;
	}

	/**
	 * 解析页眉页脚
	 * @return java.util.List<com.tl.core.TemplateField>
	 * @author Wesley
	 * @since 2024/01/25
	 **/
	private List<TemplateField> resolveHf(XSSFSheet sheet) {
		List<TemplateField> result = CollUtil.newArrayList();
		Header header = sheet.getHeader();
		Footer footer = sheet.getFooter();
		result.addAll(headerFooterFields(header.getLeft(), ExcelConstant.POSITION_HL));
		result.addAll(headerFooterFields(header.getCenter(), ExcelConstant.POSITION_HC));
		result.addAll(headerFooterFields(header.getRight(), ExcelConstant.POSITION_HR));
		result.addAll(headerFooterFields(footer.getLeft(), ExcelConstant.POSITION_FL));
		result.addAll(headerFooterFields(footer.getCenter(), ExcelConstant.POSITION_FC));
		result.addAll(headerFooterFields(footer.getRight(), ExcelConstant.POSITION_FR));
		return result;
	}

	private List<TemplateField> headerFooterFields(String content, String hfPosition) {
		if (StrUtil.isEmpty(content)) {
			return new ArrayList<>();
		}

		// 去除样式相关内容
		final String styleRegex = "&\".*?\"";
		String noStyleContent = content.replaceAll(styleRegex, StrUtil.EMPTY);
		ExcelLocator excelLocator = new ExcelLocator(hfPosition);
		return this.findFields(noStyleContent, (f) -> f.setLocation(excelLocator));
	}

	/**
	 *
	 * @param fieldContent 模板字段内容
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	private List<TemplateField> findFields(String fieldContent, Consumer<ExcelField> consumer) {
		List<TemplateField> result = new ArrayList<>();
		if (StrUtil.isEmpty(fieldContent)) {
			return result;
		}

		Matcher matcher = this.pattern.matcher(fieldContent);
		while (matcher.find()) {
			String content = matcher.group(1);
			ExcelField field = new ExcelField();
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
				throw new ExcelResolveException("Resolving excel template param error. " + e.getMessage(), e);
			}

			String picSymbol = Optional.ofNullable(templateRule.fieldSymbols())
									   .map(m -> m.get(TLFieldType.PICTURE))
									   .orElse(ExcelTemplateRule.DEFAULT_PIC_SYMBOL);
			if (StrUtil.startWith(content, picSymbol)) {
				field.setFieldType(TLFieldType.PICTURE);
				content = StrUtil.subSuf(content, picSymbol.length());
			}
			field.setName(content);

			if (Objects.nonNull(consumer)) {
				consumer.accept(field);
			}
			result.add(field);
		}
		return result;
	}

}
