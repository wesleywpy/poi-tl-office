package com.tl.excel;

import cn.hutool.core.util.RandomUtil;
import com.tl.core.data.FilePictureRenderData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
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


}
