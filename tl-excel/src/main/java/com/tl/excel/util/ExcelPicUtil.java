package com.tl.excel.util;

import cn.hutool.core.util.ArrayUtil;
import com.tl.core.TemplateField;
import com.tl.core.data.PictureRenderData;
import com.tl.excel.xssf.CellWrapper;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.*;

/**
 * ExcelPicUtil
 *
 * @author WangPanYong
 * @since 2023/12/05
 */
public final class ExcelPicUtil {

	/**
	 *
	 * @param wrapper Cell包装类
	 * @param renderData 渲染数据
	 * @param field 模板字段
	 * @author Wesley
	 * @since 2023/12/05
	 **/
	public static void addPicture(CellWrapper wrapper, PictureRenderData renderData, TemplateField field) {
		byte[] picBytes = renderData.read();
		if (ArrayUtil.isEmpty(picBytes)) {
			return;
		}
		int firstRowIndex = wrapper.getRowIndex();
		int firstColumnIndex = wrapper.getColumnIndex();
		int lastRowIndex = wrapper.getLastRowIndex();
		int lastColumnIndex = wrapper.getLastColumnIndex();
		XSSFSheet sheet = wrapper.getCell().getSheet();
		XSSFWorkbook workbook = sheet.getWorkbook();
		// TODO: 2023/12/5 支持其它类型
		int picIdx = workbook.addPicture(picBytes, Workbook.PICTURE_TYPE_PNG);

		XSSFClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
		anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
		anchor.setRow1(firstRowIndex);
		anchor.setCol1(firstColumnIndex);
		anchor.setRow2(lastRowIndex + 1);
		anchor.setCol2(lastColumnIndex + 1);
		anchor.setDy1(Units.pixelToEMU(2));
		anchor.setDx1(Units.pixelToEMU(2));
		anchor.setDy2(Units.pixelToEMU(-2));
		anchor.setDx2(Units.pixelToEMU(-2));

		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		XSSFPicture picture = drawing.createPicture(anchor, picIdx);
		picture.getCTPicture().getNvPicPr().getCNvPr().setName(renderData.name());
	}
}
