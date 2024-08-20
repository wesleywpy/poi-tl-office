package com.tl.excel.render.name;

import cn.hutool.core.util.StrUtil;
import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.data.PictureRenderData;
import com.tl.core.data.RenderData;
import com.tl.excel.render.NameExcelRender;
import com.tl.excel.util.ExcelPicUtil;
import com.tl.excel.util.ExcelRowUtil;
import com.tl.excel.util.ExcelUtil;
import com.tl.excel.xssf.CellWrapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.*;

/**
 * ListNameHandler
 *
 * @author WangPanYong
 * @since 2024/07/15
 */
public class ListNameHandler implements NameHandler{

	private final NameExcelRender nameRender;

	/**
	 * 名称管理器前缀：list渲染
	 */
	public static final String NAME_PREFIX = "TL_list_";

    public ListNameHandler(NameExcelRender nameRender) {
        this.nameRender = nameRender;
    }

    @Override
	public boolean support(NameWrapper nameWrapper) {
		return nameWrapper.getNameName().startsWith(NAME_PREFIX);
	}

	@Override
	public void handle(NameWrapper nameWrapper, List<TemplateField> templateFields, RenderDataFinder dataFinder) {
		String groupName = nameWrapper.getNameName().substring(NAME_PREFIX.length());
		List<TemplateField> fields = templateFields.stream()
												   .filter(f -> groupName.equals(f.getGroup()))
												   .toList();
		if (fields.isEmpty()) {
			return;
		}
		List<NameRefRow> nameRefRows = nameWrapper.analyseRefRange(nameRender.getTemplateRule());
		int dataAmount = dataFinder.getMaxSize(groupName);
		// 存在模板行需要-1
		int shiftRowAmount = (dataAmount - 1) * nameRefRows.size();
		ExcelRowUtil.shiftRows(nameWrapper, shiftRowAmount);

		List<NameRefRow> refRows = Optional.ofNullable(nameWrapper.refRows).orElse(new ArrayList<>());
		// 一条数据所占行数
		int templateRowAmount = nameWrapper.endRow - nameWrapper.startRow + 1;
		List<CellRangeAddress> mergedRegions = nameWrapper.findMergedRegion();
		Map<TemplateField, List<RenderData>> fieldDataMap = new HashMap<>();
		for (int dataIndex = 0; dataIndex < dataAmount; dataIndex++) {
			int rowIndex = 0;
            for (NameRefRow refRow : refRows) {
                rowIndex = dataIndex * templateRowAmount + refRow.row;
                // copy row
                XSSFRow row = ExcelRowUtil.getOrCreateRow(nameWrapper.sheet, rowIndex);
                row.setHeightInPoints(refRow.heightInPoints);
                row.setZeroHeight(refRow.zeroHeight);
                for (NameRefCol refCol : refRow.refCols) {
                    List<RenderData> dataList = fieldDataMap.get(refCol);
                    if (Objects.isNull(dataList)) {
                        refCol.setGroup(groupName);
                        dataList = dataFinder.findAll(refCol);
                        fieldDataMap.put(refCol, dataList);
                    }
                    if (dataIndex < dataList.size()) {
                        fill(row, refCol, dataList.get(dataIndex));
                    } else {
                        XSSFCell cell = row.getCell(refCol.colIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellStyle(refCol.cellStyle);
                    }
                }
            }
			if (dataIndex > 0) {
				// 每组数据的起始行
				int dataStartRow = rowIndex - templateRowAmount + 1;
				for (CellRangeAddress mergedRegion : mergedRegions) {
					// 数据行需要合并单元格
					if (dataStartRow > mergedRegion.getFirstRow()) {
						int firstRow = dataStartRow + (mergedRegion.getFirstRow() - nameWrapper.startRow);
						int lastRow = dataStartRow + (mergedRegion.getLastRow()  - nameWrapper.startRow);
						nameWrapper.sheet.addMergedRegion(
							new CellRangeAddress(firstRow, lastRow, mergedRegion.getFirstColumn(), mergedRegion.getLastColumn())
						);
					}
				}
			}
		}
	}

	private void fill(XSSFRow row, NameRefCol refCol, RenderData renderData) {
		XSSFCell cell = row.getCell(refCol.colIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cell.setCellStyle(refCol.cellStyle);
		cell.setCellValue(refCol.cellValue);

		switch (refCol.cellType){
			case NUMERIC,STRING :
				if (renderData instanceof PictureRenderData pictureRenderData) {
					ExcelPicUtil.addPicture(CellWrapper.of(cell), pictureRenderData, refCol);
					ExcelUtil.replaceCellStringValue(cell, refCol.fullName, StrUtil.EMPTY);
				} else {
					String text = renderData.getString();
					ExcelUtil.replaceCellStringValue(cell, refCol.fullName, text);
				}
				break;
			case FORMULA:
				// TODO: 2024/8/20 公式单元格
			case BLANK :
			default :
				break;
		};

	}
}
