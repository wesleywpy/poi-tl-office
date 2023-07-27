package com.tl.excel;

import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;

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

		FileOutputStream outputStream = new FileOutputStream("out.xlsx");
		template.render(model).write(outputStream);
		outputStream.close();
	}


}
