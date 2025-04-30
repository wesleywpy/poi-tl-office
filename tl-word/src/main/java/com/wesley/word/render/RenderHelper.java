package com.wesley.word.render;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.wesley.word.util.WordUtil;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;

import java.util.List;

/**
 * RenderHelper
 *
 * @author WangPanYong
 * @since 2025/04/24
 */
public final class RenderHelper {

	/**
	 * setText
	 *
	 * @author Wesley
	 * @since 2025/04/24
	 **/
	static void setText(XWPFParagraph paragraph, XWPFRun run, String text){
		if (StrUtil.contains(text, StrPool.LF)) {
			List<String> values = StrUtil.split(text, StrPool.LF);
			int size = values.size();
			for (int i = 0; i < size; i++) {
				if (i == size - 1) {
					run.setText(values.get(i), 0);
					break;
				}
				XmlCursor newCursor = paragraph.getCTP().newCursor();
				XWPFParagraph newParagraph = paragraph.getBody().insertNewParagraph(newCursor);
				WordUtil.copyParagraph(paragraph, newParagraph);
				XWPFRun newRun = newParagraph.createRun();
				newRun.getCTR().setRPr(run.getCTR().getRPr());
				newRun.setText(values.get(i));
			}
		} else {
			run.setText(text, 0);
		}
	}
}
