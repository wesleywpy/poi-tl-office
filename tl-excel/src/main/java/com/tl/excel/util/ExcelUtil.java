package com.tl.excel.util;

import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;

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
public class ExcelUtil {

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

}
