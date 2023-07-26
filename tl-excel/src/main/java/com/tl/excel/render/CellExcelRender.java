package com.tl.excel.render;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.excel.resolver.ExcelField;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * CellExcelRender
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public class CellExcelRender implements ExcelRender{

	@Override
	public void render(XSSFWorkbook workbook, List<ExcelField> templateFields, RenderDataFinder dataFinder) {

	}

}
