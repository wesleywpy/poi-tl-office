package com.tl.excel.render;

import cn.hutool.core.util.StrUtil;
import com.tl.core.RenderDataFinder;
import com.tl.core.data.PictureRenderData;
import com.tl.core.data.TextRenderData;
import com.tl.core.enums.TLFieldType;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;
import com.tl.excel.resolver.ExcelField;
import com.tl.excel.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Objects;

/**
 * CellExcelRender
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public class CellExcelRender implements ExcelRender{
	private final ExcelConfig config;

	private final TemplateRule templateRule;

	public CellExcelRender(ExcelConfig config, TemplateRule templateRule) {
		this.config = config;
		this.templateRule = templateRule;
	}

	@Override
	public void render(XSSFWorkbook workbook, List<ExcelField> templateFields, RenderDataFinder dataFinder) {
		templateFields.stream()
					  .filter(e -> StrUtil.isAllNotEmpty(e.getName(), e.getLocation()))
					  .forEach(e -> this.doRender(workbook, e, dataFinder));
	}

	/**
	 * @author Wesley
	 * @since 2023/07/27
	 **/
	private void doRender(XSSFWorkbook workbook, ExcelField templateField, RenderDataFinder dataFinder) {
		String[] locations = templateField.getLocation().split("_");
		if (locations.length < 3) {
			return;
		}
		int sheetIdx = Integer.parseInt(locations[0]);
		int rowIdx = Integer.parseInt(locations[1]);
		int colIdx = Integer.parseInt(locations[2]);
		XSSFSheet sheet = workbook.getSheetAt(sheetIdx);
		if (Objects.isNull(sheet)) {
			return;
		}
		XSSFRow row = sheet.getRow(rowIdx);
		if (Objects.isNull(row)) {
			return;
		}
		XSSFCell cell = row.getCell(colIdx);
		if (Objects.isNull(cell)) {
			return;
		}

		String filedFullName = templateRule.generateFullName(templateField);
		if (TLFieldType.TEXT.equals(templateField.getType())) {
			TextRenderData text = dataFinder.findText(templateField);
			ExcelUtil.replaceCellStringValue(cell, filedFullName, text.getText());
		} else if (TLFieldType.PICTURE.equals(templateField.getType())) {
			PictureRenderData picture = dataFinder.findPicture(templateField);
			// TODO: 2023/12/4 添加图片工具
		}

	}


}
