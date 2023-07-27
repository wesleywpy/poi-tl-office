package com.tl.core.data;

import lombok.Getter;
import lombok.Setter;

/**
 * TextRenderData
 *
 * @author WangPanYong
 * @since 2023/07/26
 */
public abstract class TextRenderData implements RenderData{

	@Setter
	@Getter
	protected String text = "";

}
