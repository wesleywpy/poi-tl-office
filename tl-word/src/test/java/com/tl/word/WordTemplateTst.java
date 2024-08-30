package com.tl.word;

import com.wesley.word.WordTemplate;
import com.wesley.word.config.WordConfig;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * WordTemplateTst
 *
 * @author WangPanYong
 * @since 2024/08/30
 */
public class WordTemplateTst {


	@Test
	public void resolve() {
		File file = new File("src/test/resources/resolve.docx");
		WordTemplate template = WordTemplate.compile(file, new WordConfig());
		template.resolve().forEach(System.out::println);
	}

}
