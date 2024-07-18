package com.tl.excel.render;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;
import com.tl.excel.render.name.ListNameHandler;
import com.tl.excel.render.name.NameHandler;
import com.tl.excel.util.ExcelUtil;
import com.tl.excel.render.name.NameWrapper;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

/**
 * RowExcelReader
 *
 * @author Wesley
 * @since 2024/07/05
 */
public class NameExcelRender extends AbstractExcelRender{
	private final List<NameHandler> handlers = new ArrayList<>();

	public NameExcelRender(ExcelConfig config, TemplateRule templateRule) {
		super(config, templateRule);
		handlers.add(new ListNameHandler(this));
	}

	@Override
	public void render(XSSFWorkbook workbook, List<TemplateField> templateFields, RenderDataFinder dataFinder) {
		List<XSSFName> allNames = workbook.getAllNames();
		for (NameHandler nameHandler : handlers) {
			for (XSSFName excelName : allNames) {
				if (ExcelUtil.isInvalidName(excelName)) {
					continue;
				}
				NameWrapper nameWrapper = new NameWrapper(excelName, workbook);
				if (nameHandler.support(nameWrapper)) {
					nameHandler.handle(nameWrapper, templateFields, dataFinder);
				}
			}
		}
	}



}
