package com.tl.excel;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tl.core.RenderDataFinder;
import com.tl.core.data.FilePictureRenderData;
import com.tl.core.data.GroupRenderData;
import com.tl.core.data.RenderDataBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ExcelTemplateTest
 *
 * @author WangPanYong
 * @since 2023/07/19
 */
public class ExcelTemplateTest {

	@Test
	public void resolve() {
		ExcelTemplate template = ExcelTemplate.compile("src/test/resources/resolve.xlsx");
		template.resolve().forEach(System.out::println);
	}

	@Test
	public void render() throws IOException {
		ExcelTemplate template = ExcelTemplate.compile("src/test/resources/resolve.xlsx");
		Map<String, Object> model = new HashMap<>();
		model.put("字符串", RandomUtil.randomString(8));
		model.put("字符串2", RandomUtil.randomString(8));
		File file = new File("src/test/resources/img/sign_1.png");
		FilePictureRenderData pictureRenderData = new FilePictureRenderData(file);
		model.put("图片", pictureRenderData);

		FileOutputStream outputStream = new FileOutputStream("out.xlsx");

		template.render(model).write(outputStream);
		outputStream.close();
	}

	@Test
	public void renderList() throws IOException {
		ExcelTemplate template = ExcelTemplate.compile("src/test/resources/resolve.xlsx");

		File image = new File("src/test/resources/img/admin.png");
		FilePictureRenderData pictureRenderData = new FilePictureRenderData(image);

		RenderDataBuilder builder = RenderDataBuilder.of(null);
		builder.add("group1", "字符串", RandomUtil.randomString(8))
//			   .add("group1", "字符串", RandomUtil.randomString(8))
			   .add("group1", "图片", pictureRenderData)
			   .add("group1", "图片", pictureRenderData);

		builder.add("group1",
					"序号", "1")
			   .add("group1", "序号", "2");
		String t = """
			I wonder how the baseball came out in the grand leagues today, he thought. It would be wonderful to do this with a radio.\s
			Then he thought, think of it always. Think of what you are doing. You must do nothing stupid.
			""";

		RenderDataFinder renderDataFinder = builder.buildFinder();
		FileOutputStream outputStream = new FileOutputStream("out.xlsx");
		template.render(renderDataFinder).write(outputStream);
		outputStream.close();
	}

}
