package com.tl.excel.xssf;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.util.List;
import java.util.Objects;

/**
 * XSSFCellWrapper
 *
 * @author WangPanYong
 * @since 2023/12/05
 */
@Getter
@Setter
public class CellWrapper {
	private final XSSFCell cell;

	/**
	 * 单元格像素宽度
	 */
	private float pixelWidth;
	/**
	 * 单元格 字符宽度
	 */
	private int characterWidth;

	private float pointsHeight;

	/**
	 * 单元格行索引
	 */
	private int rowIndex;

	/**
	 * 单元格列索引
	 */
	private int columnIndex;

	/**
	 * 是否是合并区域
	 */
	private boolean merged = false;

	/**
	 * 合并区域最后一行坐标
	 */
	private int lastRowIndex;
	/**
	 * 合并区域最后一列坐标
	 */
	private int lastColumnIndex;

	public CellWrapper(XSSFCell cell) {
		this.cell = cell;
	}

	/**
	 *
	 * @param cell
	 * @return com.tl.excel.xssf.XSSFCellWrapper
	 * @author Wesley
	 * @since 2023/12/05
	 **/
	public static CellWrapper of(XSSFCell cell) {
		CellWrapper result = new CellWrapper(cell);
		Sheet sheet = cell.getSheet();
		List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
		int startCol = cell.getColumnIndex();
		int startRow = cell.getRowIndex();
		float height = sheet.getRow(startRow).getHeightInPoints();
		float widthPixel = sheet.getColumnWidthInPixels(startCol);
		int width = sheet.getColumnWidth(startCol);

		CellRangeAddress mergedRegion = null;
		for (CellRangeAddress region : mergedRegions) {
			if (region.getFirstRow() == startRow && region.getFirstColumn() == startCol) {
				mergedRegion = region;
				break;
			}
		}
		if (Objects.nonNull(mergedRegion)) {
			int lastRowIndex = mergedRegion.getLastRow();
			int lastColumnIndex = mergedRegion.getLastColumn();
			CellReference lastCell = new CellReference(lastRowIndex, lastColumnIndex);
			for (int rowIndex = startRow + 1; rowIndex <= lastRowIndex; rowIndex++) {
				height = height + sheet.getRow(rowIndex).getHeightInPoints();
			}
			for (int rangeIndex = startCol + 1; rangeIndex <= lastColumnIndex; rangeIndex++) {
				widthPixel = widthPixel + sheet.getColumnWidthInPixels(rangeIndex);
				width = width + sheet.getColumnWidth(rangeIndex);
			}
			result.setMerged(true);
			result.setLastRowIndex(lastRowIndex);
			result.setLastColumnIndex(lastColumnIndex);
		} else {
			result.setLastRowIndex(startRow);
			result.setLastColumnIndex(startCol);
		}
		result.setRowIndex(startRow);
		result.setColumnIndex(startCol);
		result.setCharacterWidth(width);
		result.setPixelWidth(widthPixel);
		result.setPointsHeight(height);
		return result;
	}

}
