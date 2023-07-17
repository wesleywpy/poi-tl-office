package com.tl.excel.render;

import com.tl.core.Resolver;
import com.tl.excel.resolver.ExcelField;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * ExcelResolver
 *
 * @author WangPanYong
 * @since 2023/07/17 17:39
 */
public class ExcelResolver implements Resolver {

	/**
	 *
	 * @param workbook {@link XSSFWorkbook}
	 * @return java.util.List<com.tl.excel.resolver.ExcelField>
	 * @author Wesley
	 * @since 2023/07/17
	 **/
	public List<ExcelField> resolve(XSSFWorkbook workbook) {
		return null;
	}

}
