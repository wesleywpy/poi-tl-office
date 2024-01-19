package com.tl.core.data;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * FilePictureRenderData
 *
 * @author WangPanYong
 * @since 2023/12/04
 */
public class FilePictureRenderData implements PictureRenderData {

	private final File file;

	private final String suffix;

	public FilePictureRenderData(File file) {
		this.file = file;
		this.suffix = FileUtil.getSuffix(file).toLowerCase();
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

	@Override
	public int picType() {
		int jpeg = 5;
		int png = 6;
		return suffix.equals("jpg") || suffix.equals("jpeg") ? jpeg : png;
	}
}
