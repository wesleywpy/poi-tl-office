package com.tl.excel.render.name;

import com.tl.core.RenderDataFinder;
import com.tl.core.TemplateField;

import java.util.List;

/**
 * NameHandler
 *
 * @author WangPanYong
 * @since 2024/07/15
 */
public interface NameHandler {


	/**
	 *
	 * @param nameWrapper 名称管理器对象包装类
	 * @return boolean
	 * @author Wesley
	 * @since 2024/07/15
	 **/
	boolean support(NameWrapper nameWrapper);


	/**
	 *
	 * @param nameWrapper {@link NameWrapper}
	 * @param templateFields {@link TemplateField}
	 * @param dataFinder {@link RenderDataFinder}
	 * @author Wesley
	 * @since 2024/07/15
	 **/
	void handle(NameWrapper nameWrapper, List<TemplateField> templateFields, RenderDataFinder dataFinder);
}
