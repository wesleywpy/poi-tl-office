package com.tl.excel.render.name;

import cn.hutool.core.util.StrUtil;
import com.tl.core.TemplateField;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.exception.ExcelResolveException;
import com.tl.excel.resolver.ExcelField;
import com.tl.excel.rule.ExcelTemplateRule;
import com.tl.excel.util.ExcelUtil;
import lombok.Getter;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NameWrapper
 * 名称管理器对象包装类
 * @author WangPanYong
 * @since 2024/07/15
 */
@Getter
public class NameWrapper {

	final XSSFName name;

	final XSSFSheet sheet;

	final String nameName;

	final int startRow;

	final int startCol;

	final int endRow;

	final int endCol;

	List<NameRefRow> refRows;

    public NameWrapper(XSSFName xssfName, XSSFWorkbook workbook) {
        this.name = xssfName;
		this.sheet = workbook.getSheet(xssfName.getSheetName());
		this.nameName = xssfName.getNameName();

		AreaReference aref = new AreaReference(xssfName.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);
		CellReference firstCell = aref.getFirstCell();
		CellReference lastCell = aref.getLastCell();
		this.startRow = firstCell.getRow();
		this.endRow = lastCell.getRow();
		this.startCol = firstCell.getCol();
		this.endCol = lastCell.getCol();
	}

	/**
	 * 获取合并区域
	 *
	 * @return java.util.List<org.apache.poi.ss.util.CellRangeAddress>
	 * @author Wesley
	 * @since 2024/08/19
	 **/
	List<CellRangeAddress> findMergedRegion() {
		List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
		List<CellRangeAddress> result = new ArrayList<>();
		for (CellRangeAddress mergedRegion : mergedRegions) {
			flag:
			for (int rowIdx = startRow; rowIdx <= this.endRow; rowIdx++) {
				for (int colIdx = this.startCol; colIdx < this.endCol; colIdx++) {
					if (mergedRegion.isInRange(rowIdx, colIdx)) {
						result.add(mergedRegion);
						break flag;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 解析Name区域
	 * @return java.util.List<com.tl.excel.render.name.NameRefRow>
	 * @author Wesley
	 * @since 2024/07/17
	 **/
	List<NameRefRow> analyseRefRange(TemplateRule templateRule) {
		if (Objects.nonNull(refRows) && !refRows.isEmpty()) {
			return refRows;
		}
		this.refRows = new ArrayList<>(this.endRow - this.startRow + 1);
		Pattern pattern = Pattern.compile(templateRule.regexField());
		Pattern paramPattern = Pattern.compile(templateRule.regexFieldParam());
		List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
		for (int rowIndex = this.startRow; rowIndex <= this.endRow; rowIndex++) {
			XSSFRow row = sheet.getRow(rowIndex);
			if (Objects.isNull(row)) {
				continue;
			}

			NameRefRow refRow = new NameRefRow();
			refRow.row = rowIndex;
			refRow.zeroHeight = row.getZeroHeight();
			refRow.heightInPoints = row.getHeightInPoints();

			List<NameRefCol> refCols = analyseRefCol(row, mergedRegions, templateRule, pattern, paramPattern);
			refRow.refCols.addAll(refCols);
			refRows.add(refRow);
		}
		return refRows;
	}

	/**
	 *
	 * @param mergedRegions 合并区域
	 * @author Wesley
	 * @since 2024/07/17
	 **/
	private List<NameRefCol> analyseRefCol(XSSFRow row, List<CellRangeAddress> mergedRegions
		, TemplateRule templateRule, Pattern pattern, Pattern paramPattern) {
		int rowIdx = row.getRowNum();
		List<NameRefCol> result = new ArrayList<>();
		for (int colIdx = this.startCol; colIdx <= this.endCol; colIdx++) {
			XSSFCell cell = row.getCell(colIdx);
			if (Objects.isNull(cell)) {
				continue;
			}

			CellRangeAddress mergedRegion = ExcelUtil.findFirstRegion(mergedRegions, rowIdx, colIdx);
			List<NameRefCol> refCols = new ArrayList<>();
			switch (cell.getCellType()) {
				case STRING:
					refCols = findFields(cell.getStringCellValue(), templateRule, pattern, paramPattern);
					break;
				case FORMULA:
					refCols = findFields(cell.getCellFormula(), templateRule, pattern, paramPattern);
					break;
				case NUMERIC:
					NameRefCol refCol = new NameRefCol();
					refCol.cellValue = ExcelUtil.getCellStringValue(cell);
					refCols.add(refCol);
					break;
				case BLANK:
					// 空白单元格，使用样式
					refCols.add(new NameRefCol());
					break;
				default:
					break;
			}
			for (NameRefCol refCol : refCols) {
				refCol.colIdx = colIdx;
				refCol.cellType = cell.getCellType();
				refCol.cellStyle = cell.getCellStyle();
				Optional.ofNullable(mergedRegion).ifPresent(e -> refCol.merged = true);
			}
			result.addAll(refCols);
		}
		return result;
	}

	private List<NameRefCol> findFields(String fieldContent, TemplateRule templateRule, Pattern pattern, Pattern paramPattern) {
		List<NameRefCol> result = new ArrayList<>();
		NameRefCol refCol = new NameRefCol();
		refCol.cellValue = fieldContent;
		if (StrUtil.isBlank(fieldContent)) {
			result.add(refCol);
			return result;
		}

		Matcher matcher = pattern.matcher(fieldContent);
		while (matcher.find()) {
			String content = matcher.group(1);
			NameRefCol field = new NameRefCol();
			field.cellValue = fieldContent;
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
			field.fullName = templateRule.generateFullName(field);
			result.add(field);
		}
		if (result.isEmpty()) {
			result.add(refCol);
		}
		return result;
	}


}
