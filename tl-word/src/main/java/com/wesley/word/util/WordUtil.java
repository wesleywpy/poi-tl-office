package com.wesley.word.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.tl.core.exception.TLException;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * WordUtil
 *
 * @author WangPanYong
 * @since 2024/09/09
 */
public final class WordUtil {
	private static final String BOOKMARK_END_LABEL = "w:bookmarkEnd";

	private static final String PROOF_ERR_TAG = "w:proofErr";

	private WordUtil() {
	}

	/**
	 * copyRun
	 *
	 * @param run       原始run
	 * @param targetRun 目标Run
	 * @author Wesley
	 * @since 2024/09/09 10:50
	 **/
	public static void copyRun(XWPFRun run, XWPFRun targetRun, String text) {
		if (Objects.isNull(run) || Objects.isNull(targetRun)) {
			return;
		}
		targetRun.getCTR().setRPr(run.getCTR().getRPr());
		if (Objects.nonNull(text)) {
			targetRun.setText(text, 0);
		} else {
			targetRun.setText(run.text());
		}
		targetRun.setColor(run.getColor());
		targetRun.setFontFamily(run.getFontFamily());
		Double fontSizeAsDouble = run.getFontSizeAsDouble();
		if (Objects.nonNull(fontSizeAsDouble)) {
			targetRun.setFontSize(fontSizeAsDouble);
		}
		targetRun.setCharacterSpacing(run.getCharacterSpacing());

		List<XWPFPicture> embeddedPictures = run.getEmbeddedPictures();
		if (CollUtil.isNotEmpty(embeddedPictures)) {
			for (XWPFPicture picture : embeddedPictures) {
				XWPFPictureData pictureData = picture.getPictureData();
				try (InputStream bis = new ByteArrayInputStream(pictureData.getData())) {
					CTPicture ctPic = picture.getCTPicture();
					int width = (int) ctPic.getSpPr().getXfrm().getExt().getCx();
					int depth = (int) ctPic.getSpPr().getXfrm().getExt().getCy();
					targetRun.addPicture(bis, pictureData.getPictureType(), pictureData.getFileName(), width, depth);
				} catch (Exception e) {
					throw new TLException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * clearRun
	 * 清除Run内容
	 *
	 * @author Wesley
	 * @since 2023/03/03 16:18
	 **/
	public static void clearRun(XWPFRun run) {
		run.setText(StrUtil.EMPTY, 0);
		CTR ctr = run.getCTR();
		ctr.getTList().forEach(XmlObject::setNil);
	}

	/**
	 * findBookmarkRuns
	 * 找到书签之间的run
	 *
	 * @param paragraph 段落
	 * @param bookmarkStart 书签开始位置
	 * @author Wesley
	 * @since 2024/09/18
	 **/
	public static List<XWPFRun> findBookmarkRuns(XWPFParagraph paragraph, CTBookmark bookmarkStart) {
		List<XWPFRun> result = new ArrayList<>();

		Node bookmarkStartNode = bookmarkStart.getDomNode();
		Node nextNode = bookmarkStartNode.getNextSibling();
		List<CTMarkupRange> bookmarkEndList = paragraph.getCTP().getBookmarkEndList();
		BigInteger startId = bookmarkStart.getId();

		domBreak:
		for (XWPFRun run : paragraph.getRuns()) {
			Node runNode = run.getCTR().getDomNode();
			if (nextNode == runNode) {
				result.add(run);
				do {
					nextNode = nextNode.getNextSibling();
					if (BOOKMARK_END_LABEL.equalsIgnoreCase(nextNode.getNodeName()) && Objects.nonNull(bookmarkEndList)) {
						for (CTMarkupRange markupRange : bookmarkEndList) {
							Node domNode = markupRange.getDomNode();
							if (nextNode == domNode && markupRange.getId().equals(startId)) {
								break domBreak;
							}
						}
					}
				} while (PROOF_ERR_TAG.equalsIgnoreCase(nextNode.getNodeName())
					|| bookmarkStartNode.getNodeName().equalsIgnoreCase(nextNode.getNodeName())
					|| BOOKMARK_END_LABEL.equalsIgnoreCase(nextNode.getNodeName()));
			}
		}
		if (result.isEmpty()) {
			XWPFRun dataRun = paragraph.createRun();
			result.add(dataRun);
		}
		return result;
	}
}
