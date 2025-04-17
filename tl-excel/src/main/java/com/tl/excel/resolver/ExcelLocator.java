package com.tl.excel.resolver;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * ExcelLocator
 *
 * @author WangPanYong
 * @since 2024/01/25
 */
@Getter
@EqualsAndHashCode
public class ExcelLocator {

	int sheetIndex;
	int rowIndex;
	int cellIndex;

	/** 是否为页眉页脚 */
	boolean headerFooter = false;

	String hfPosition;


	public ExcelLocator(int sheetIndex, int rowIndex, int cellIndex) {
		this.sheetIndex = sheetIndex;
		this.rowIndex = rowIndex;
		this.cellIndex = cellIndex;
	}

	public ExcelLocator(int sheetIndex, String hfPosition) {
		this.headerFooter = true;
		this.sheetIndex = sheetIndex;
		this.hfPosition = hfPosition;
	}

	@Override
	public String toString() {
		return this.headerFooter ? sheetIndex + "_" + hfPosition : sheetIndex + "_" + rowIndex + "_" + cellIndex;
	}
}
