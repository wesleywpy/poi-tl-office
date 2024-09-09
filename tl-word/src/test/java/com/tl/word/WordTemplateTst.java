package com.tl.word;

import com.wesley.word.WordTemplate;
import com.wesley.word.config.WordConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * WordTemplateTst
 *
 * @author WangPanYong
 * @since 2024/08/30
 */
public class WordTemplateTst {


	@Test
	public void resolve() throws IOException {
		File file = new File("src/test/resources/resolve.docx");
		WordTemplate template = WordTemplate.compile(file, new WordConfig());
		template.resolve().forEach(System.out::println);
		FileOutputStream outputStream = new FileOutputStream("out.docx");
		template.write(outputStream);
		outputStream.close();
	}

}
