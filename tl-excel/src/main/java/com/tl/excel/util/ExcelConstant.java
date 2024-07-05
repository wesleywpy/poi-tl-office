package com.tl.excel.util;

/**
 *
 * @author Wesley
 * @since 2024/01/29
 **/
public final class ExcelConstant {
    /**
     * 页眉左边
     */
    public static final String POSITION_HL = "HL";
    /**
     * 页眉中间
     */
    public static final String POSITION_HC = "HC";
    /**
     * 页眉右边
     */
    public static final String POSITION_HR = "HR";
    /**
     * 页脚左边
     */
    public static final String POSITION_FL = "FL";
    /**
     * 页脚中间
     */
    public static final String POSITION_FC = "FC";
    /**
     * 页脚右边
     */
    public static final String POSITION_FR = "FR";

    /**
     * 不合法的名称管理器
     */
    public static final String NAME_ILLEGAL = "#REF!";
	/**
	 * 名称管理器前缀：list渲染
	 */
    public static final String NAME_PREFIX_LIST = "TL_list_";

    private ExcelConstant() {
        super();
    }
}
