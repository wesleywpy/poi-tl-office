package com.tl.core.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * FilePictureRenderData
 *
 * @author WangPanYong
 * @since 2023/12/04
 */
public class FilePictureRenderData extends PictureRenderData{

	private final File file;

	public FilePictureRenderData(File file) {
		this.file = file;
	}

	@Override
	public byte[] read() {
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			// TODO: 2023/12/4 输出警告日志
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String name() {
		return file.getName();
	}
}
