package com.tl.excel.render.name;

import com.tl.excel.resolver.ExcelField;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

/**
 * NameRefCol
 *
 * @author WangPanYong
 * @since 2024/07/16
 */
public class NameRefCol extends ExcelField {

	String cellValue;

	int colIdx;
	boolean merged;
	CellType cellType;
	CellStyle cellStyle;

}
