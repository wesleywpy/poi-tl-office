package com.tl.word;

import cn.hutool.core.util.RandomUtil;
import com.tl.core.data.FilePictureRenderData;
import com.tl.core.data.RenderDataBuilder;
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

	@Test
	public void render() throws IOException {
		File file = new File("src/test/resources/resolve.docx");
		WordTemplate template = WordTemplate.compile(file, new WordConfig());

		File image = new File("src/test/resources/img/sign_1.png");
		FilePictureRenderData pictureRenderData = new FilePictureRenderData(image);

		RenderDataBuilder builder = RenderDataBuilder.of(null);
		builder.add(null, "文本1", RandomUtil.randomString(8))
			   .add(null, "文本2", RandomUtil.randomString(8));
		builder.add(null, "表格文本", RandomUtil.randomString(8))
			   .add(null, "表格图片", pictureRenderData);

		template.render(builder.buildFinder());
		FileOutputStream outputStream = new FileOutputStream("out.docx");
		template.write(outputStream);
		outputStream.close();
	}

}
