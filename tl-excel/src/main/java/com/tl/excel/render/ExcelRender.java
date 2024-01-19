package com.tl.excel.render;

import com.tl.core.Render;
import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.excel.resolver.ExcelField;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * ExcelRender
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public interface ExcelRender extends Render {

	/**
	 *
	 * @param workbook 工作薄
	 * @author Wesley
	 * @since 2023/07/26
	 **/
	void render(XSSFWorkbook workbook, List<TemplateField> templateFields, RenderDataFinder dataFinder);

}
