package com.tl.core.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TextRenderData
 *
 * @author WangPanYong
 * @since 2023/07/26
 */

@Setter
@Getter
@NoArgsConstructor
public class TextRenderData implements RenderData{
	protected String text = "";

	public TextRenderData(String text) {
		this.text = text;
	}

	@Override
	public String getString() {
		return text;
	}
}
