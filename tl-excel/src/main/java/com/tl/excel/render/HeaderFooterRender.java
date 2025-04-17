package com.tl.excel.render;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;
import com.tl.core.rule.TemplateRule;
import com.tl.excel.config.ExcelConfig;
import com.tl.excel.resolver.ExcelField;
import com.tl.excel.resolver.ExcelLocator;
import com.tl.excel.util.ExcelConstant;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Objects;

/**
 * HeaderFooterRender
 * 页眉页脚渲染
 * @author WangPanYong
 * @since 2024/11/20
 */
public class HeaderFooterRender extends AbstractExcelRender{

	public HeaderFooterRender(ExcelConfig config, TemplateRule templateRule) {
		super(config, templateRule);
	}

	@Override
	public void render(XSSFWorkbook workbook, List<TemplateField> templateFields, RenderDataFinder dataFinder) {
		for (TemplateField templateField : templateFields) {
			if (templateField instanceof ExcelField excelField) {
				ExcelLocator locator = excelField.getLocator();
				if (Objects.nonNull(locator) && locator.isHeaderFooter()) {
					this.doRender(workbook, excelField, dataFinder);
				}
			}
		}
	}
	/**
	 * doRender
	 *
	 * @param excelField 模板字段
	 * @param dataFinder {@link RenderDataFinder}
	 * @author Wesley
	 * @since 2024/11/20
	 **/
	protected void doRender(XSSFWorkbook workbook, ExcelField excelField, RenderDataFinder dataFinder) {
		ExcelLocator locator = excelField.getLocator();
		XSSFSheet sheet = workbook.getSheetAt(locator.getSheetIndex());
		if (Objects.isNull(sheet)) {
			return;
		}
		Header header = sheet.getHeader();
		Footer footer = sheet.getFooter();
		String hfPosition = locator.getHfPosition();
		switch (hfPosition){
			case ExcelConstant.POSITION_HL:
				replaceHeaderFooterValue(header.getLeft(), excelField);
			case ExcelConstant.POSITION_HC:
				replaceHeaderFooterValue(header.getCenter(), excelField);
			case ExcelConstant.POSITION_HR:
				replaceHeaderFooterValue(header.getRight(), excelField);
			case ExcelConstant.POSITION_FL:
				replaceHeaderFooterValue(footer.getLeft(), excelField);
			case ExcelConstant.POSITION_FC:
				replaceHeaderFooterValue(footer.getCenter(), excelField);
			case ExcelConstant.POSITION_FR:
				replaceHeaderFooterValue(footer.getRight(), excelField);
		}
	}

	private void replaceHeaderFooterValue(String hfContent, ExcelField excelField) {
		if (hfContent.isBlank()) {
			return;
		}
		String fullName = getTemplateRule().generateFullName(excelField);
		System.out.println(hfContent);

	}

}
