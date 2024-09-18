package com.wesley.word.render;

import com.tl.core.rule.TemplateRule;
import com.wesley.word.config.WordConfig;

/**
 * AbstractWordRender
 *
 * @author WangPanYong
 * @since 2024/09/18
 */
public abstract class AbstractWordRender implements WordRender{
	protected final WordConfig wordConfig;
	protected final TemplateRule templateRule;

	protected AbstractWordRender(WordConfig wordConfig, TemplateRule templateRule) {
		this.wordConfig = wordConfig;
		this.templateRule = templateRule;
	}
}
