package com.tl.excel.util;

import com.tl.excel.render.name.NameWrapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTOneCellAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ExcelRowUtil
 *
 * @author WangPanYong
 * @since 2024/08/07
 */
public final class ExcelRowUtil {

	/**
	 *
	 * @param nameWrapper {@link NameWrapper}
	 * @param shiftRowAmount 扩展次数
	 * @author WangPanYong
	 * @since 2024/08/07 09:40
	 **/
	public static void shiftRows(NameWrapper nameWrapper, int shiftRowAmount) {
		int startRow = nameWrapper.getStartRow();
		int endRow = nameWrapper.getEndRow();
		XSSFSheet sheet = nameWrapper.getSheet();
		if (shiftRowAmount <= 0 || endRow >= sheet.getLastRowNum()) {
			return;
		}

		sheet.shiftRows(endRow + 1, sheet.getLastRowNum(), shiftRowAmount, true, true);
		// 设置分页符
		int[] oldRowBreaks = sheet.getRowBreaks();
		List<Integer> newRowBreaks = new ArrayList<>();
		for (int rowBreak : oldRowBreaks) {
			if (startRow > rowBreak) {
				newRowBreaks.add(rowBreak);
			} else {
				newRowBreaks.add(rowBreak + shiftRowAmount);
			}
			sheet.removeRowBreak(rowBreak);
		}
		for (int rowBreak : newRowBreaks) {
			sheet.setRowBreak(rowBreak);
		}

		XSSFDrawing xssfDrawing = sheet.getDrawingPatriarch();
		if (null != xssfDrawing) {
			CTDrawing ctDrawing = xssfDrawing.getCTDrawing();
			List<CTOneCellAnchor> oneCellAnchors = ctDrawing.getOneCellAnchorList();
			for (CTOneCellAnchor anchor : oneCellAnchors) {
				CTMarker anchorFrom = anchor.getFrom();
				if (anchorFrom.getRow() >= startRow) {
					anchorFrom.setRow(anchorFrom.getRow() + shiftRowAmount);
				}
			}
			List<CTTwoCellAnchor> twoCellAnchors = ctDrawing.getTwoCellAnchorList();
			for (CTTwoCellAnchor anchor : twoCellAnchors) {
				CTMarker anchorFrom = anchor.getFrom();
				CTMarker anchorTo = anchor.getTo();
				if (anchorFrom.getRow() >= startRow) {
					anchorFrom.setRow(anchorFrom.getRow() + shiftRowAmount);
				}
				if (anchorTo.getRow() >= startRow) {
					anchorTo.setRow(anchorTo.getRow() + shiftRowAmount);
				}
			}
		}

		// TODO: 2024/8/7 平移组件
	}

	/**
	 * 获取或创建行
	 */
	public static XSSFRow getOrCreateRow(XSSFSheet sheet, int rowIndex) {
		XSSFRow row = sheet.getRow(rowIndex);
		return Optional.ofNullable(row).orElseGet(() -> sheet.createRow(rowIndex));
	}
}
