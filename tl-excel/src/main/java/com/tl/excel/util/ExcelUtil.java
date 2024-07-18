package com.tl.excel.util;

import cn.hutool.core.util.StrUtil;
import com.tl.core.util.function.Try;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.functions.Function4Arg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * ExcelUtil
 *
 * @author Wesley
 * @since 2023/07/18
 */
public final class ExcelUtil {

	/**
	 *
	 * @param cell 单元格对象
	 * @return java.lang.String
	 * @author Wesley
	 * @since 2023/07/18
	 **/
	public static String getCellStringValue(Cell cell) {
		String val = StrUtil.EMPTY;
		if (Objects.isNull(cell)) {
			return val;
		}
		switch (cell.getCellType()) {
			case NUMERIC:
				String cellString = cell.toString();
				if (DateUtil.isCellDateFormatted(cell)) {
					// TODO: 2023/7/18 定制格式
					// 日期格式yyyy-mm-dd
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// 日期型
					val = fmt.format(cell.getDateCellValue());
				} else {
					if (cellString.contains("E")) {
						// 数字格式，防止长数字成为科学计数法形式，或者int变为double形式
						DecimalFormat df = new DecimalFormat("0");
						// 转换科学计数法
						val = df.format(cell.getNumericCellValue());
					} else if (cellString.indexOf(".") > 0) {
						// 去掉多余的0
						val = cellString.replaceAll("0+?$", "");
						// 如最后一位是.则去掉
						val = val.replaceAll("[.]$", "");
					}
				}
				break;
			// 文本类型 和 空白
			case STRING:
			case BLANK:
				val = cell.getStringCellValue();
				break;
			// 布尔型
			case BOOLEAN:
				val = String.valueOf(cell.getBooleanCellValue());
				break;
			// 错误
			case ERROR:
				break;
			// 公式
			case FORMULA:
				val = cell.getCellFormula();
				break;
			default:
				val = Optional.ofNullable(cell.getRichStringCellValue()).map(RichTextString::toString).orElse(StrUtil.EMPTY);
		}
		return val;
	}

	/**
	 *
	 * @param cell 单元格对象
	 * @param value 被替换的值
	 * @param searchStr 搜索的字符串
	 * @author Wesley
	 * @since 2023/07/27
	 **/
	public static void replaceCellStringValue(Cell cell, String searchStr, String value) {
		if (StrUtil.isEmpty(searchStr) || value == null) {
			return;
		}

		CellType cellType = cell.getCellType();
		if (CellType.FORMULA == cellType) {
			String formula = cell.getCellFormula();
			cell.setCellFormula(formula.replace(searchStr, value));
		} else {
			String cellValue = cell.getStringCellValue();
			// TODO: 2023/7/27 处理数字日期等格式
			if (cellValue.equals(searchStr)) {
				cell.setCellValue(value);
			}
			else {
				cell.setCellValue(cellValue.replace(searchStr, value));
			}
		}
	}

	/**
	 *
	 * @param mergedRegions 合并区域列表
	 * @param rowIndex 行坐标
	 * @param columnIndex 列坐标
	 * @author Wesley
	 * @since 2024/07/27
	 **/
	public static CellRangeAddress findFirstRegion(List<CellRangeAddress> mergedRegions, int rowIndex, int columnIndex) {
		for (CellRangeAddress mergedRegion : mergedRegions) {
			if (mergedRegion.getFirstRow() == rowIndex && mergedRegion.getFirstColumn() == columnIndex) {
				return mergedRegion;
			}
		}
		return null;
	}

	/**
	 * isInvalidName
	 * 是否为无效的名称管理器
	 * @param name 名称管理器
	 * @return boolean true：无效
	 * @author Wesley
	 * @since 2023/01/10 11:11
	 **/
	public static boolean isInvalidName(Name name) {
		return !isValidName(name);
	}

	/**
	 * 是否为有效的名称管理器
	 * @return boolean true：有效
	 * @author Wesley
	 * @since 2023/05/25 16:12
	 **/
	public static boolean isValidName(Name name) {
		if (Objects.isNull(name)) {
			return false;
		}
		return Try.ofFailed(name::getRefersToFormula)
				  .filter(e -> StrUtil.isNotEmpty(e) && !e.contains(ExcelConstant.NAME_ILLEGAL))
				  .map(e -> new AreaReference(e, SpreadsheetVersion.EXCEL2007))
				  .isSuccess();
	}

}
