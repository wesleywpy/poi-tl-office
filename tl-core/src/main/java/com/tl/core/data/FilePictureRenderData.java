package com.tl.core.data;

import cn.hutool.core.io.FileUtil;
import com.tl.core.exception.TLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * FilePictureRenderData
 *
 * @author WangPanYong
 * @since 2023/12/04
 */
public class FilePictureRenderData implements PictureRenderData {

	private final File file;

	private final String suffix;

	private byte[] cache;

	public FilePictureRenderData(File file) {
		this.file = file;
		this.suffix = FileUtil.getSuffix(file).toLowerCase();
	}

	@Override
	public byte[] read() {
		if (Objects.nonNull(cache)) {
			return cache;
		}
		try {
			cache = Files.readAllBytes(file.toPath());
			return cache;
		} catch (IOException e) {
			throw new TLException(e.getMessage(), e);
		}
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
