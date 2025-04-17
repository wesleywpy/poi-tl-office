package com.tl.core.data;

/**
 * PictureRenderData
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public interface PictureRenderData extends RenderData {

	/**
	 * 图片字节数组
	 *
	 * @return byte[]
	 * @author Wesley
	 * @since 2023/12/04
	 **/
	byte[] read();

	/**
	 *
	 * @return java.lang.String 图片名称
	 * @author Wesley
	 * @since 2023/12/05
	 **/
	String name();

	/**
	 * @return int 图片类型
	 * @author Wesley
	 * @since 2023/12/15
	 **/
	int picType();


	@Override
	default String getString() {
		return name();
	}
}
