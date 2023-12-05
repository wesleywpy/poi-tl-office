package com.tl.excel.util;

import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

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

}
