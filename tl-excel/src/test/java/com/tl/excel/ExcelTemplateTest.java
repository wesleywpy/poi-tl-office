package com.tl.excel;

import org.junit.jupiter.api.Test;

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


}
