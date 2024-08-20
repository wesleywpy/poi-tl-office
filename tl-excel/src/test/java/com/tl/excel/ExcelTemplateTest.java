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

		builder.add("group1", "序号", "1")
			   .add("group1", "序号", "2");

		String t = """
			The bird went higher in the air and circled again, his wings motionless. Then he dove\s
			suddenly and the old man saw flying fish spurt out of the water and sail desperately over\s
			the surface.
			“Dolphin,” the old man said aloud. “Big dolphin.” \s
			He shipped his oars and brought a small line from under the bow. It had a wire\s
			leader and a medium-sized hook and he baited it with one of the sardines. He let it go\s
			over the side and then made it fast to a ring bolt in the stern. Then he baited another line\s
			and left it coiled in the shade of the bow. He went back to rowing and to watching the\s
			long-winged black bird who was working, now, low over the water. \s
			""";

		RenderDataFinder renderDataFinder = builder.buildFinder();
		FileOutputStream outputStream = new FileOutputStream("out.xlsx");
		template.render(renderDataFinder).write(outputStream);
		outputStream.close();
	}

}
