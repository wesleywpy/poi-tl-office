package com.tl.excel.resolver;

import lombok.Getter;

/**
 * ExcelLocator
 *
 * @author WangPanYong
 * @since 2024/01/25
 */
@Getter
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

	public ExcelLocator(String hfPosition) {
		this.headerFooter = true;
		this.hfPosition = hfPosition;
	}

	@Override
	public String toString() {
		return this.headerFooter ? hfPosition : sheetIndex + "_" + rowIndex + "_" + cellIndex;
	}
}
