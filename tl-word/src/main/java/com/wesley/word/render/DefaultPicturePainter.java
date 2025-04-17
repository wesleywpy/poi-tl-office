package com.wesley.word.render;

import com.tl.core.TemplateField;
import com.tl.core.data.PictureRenderData;
import com.tl.core.exception.TLException;
import com.wesley.word.util.WordUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * RenderHelper
 *
 * @author WangPanYong
 * @since 2025/04/17
 */
public class DefaultPicturePainter implements WordPicturePainter{
	/**
	 * row.getHeight的单位是1/21个点 :表示设置图片高度
	 */
	public static final int POINT_TO_PICTURE = 21;

	@Override
	public void add(TemplateField field, PictureRenderData picture, List<XWPFRun> runs, int width, int height) {
		if (Objects.isNull(picture)) {
			return;
		}

		XWPFRun run = runs.get(0);
		try (ByteArrayInputStream is = new ByteArrayInputStream(picture.read())) {
			int[] weightAndHeight = parseParams(field.getParams(), is, width, height);
			is.reset();

			run.addPicture(is, picture.picType(), field.getName(), weightAndHeight[0], weightAndHeight[1]);
		} catch (Exception e) {
			String msg = "Word add picture failed! " + e.getMessage();
			throw new TLException(e.getMessage(), e);
		}
		WordUtil.clearRun(run);
	}

	/**
	 * @param width 所在位置width
	 * @param height 所在位置height
	 */
	private int[] parseParams(List<String> params, ByteArrayInputStream byteArray, int width, int height) throws IOException {
		// 原图尺寸
		BufferedImage bufferedImage = ImageIO.read(byteArray);
		if (bufferedImage == null) {
			throw new IllegalArgumentException("picture type error!");
		}
		int[] original =  new int[]{Units.pixelToEMU(bufferedImage.getWidth()), Units.pixelToEMU(bufferedImage.getHeight())};
		if (width < 1 || height < 1) {
			return original;
		} else {
			String firstP = Optional.ofNullable(params).flatMap(e -> e.stream().findFirst()).orElse("");
			if ("Orig".equalsIgnoreCase(firstP)) {
				return original;
			}
			return new int[]{Units.toEMU((double) width / POINT_TO_PICTURE), Units.toEMU((double) height / POINT_TO_PICTURE)};
		}

	}
}
