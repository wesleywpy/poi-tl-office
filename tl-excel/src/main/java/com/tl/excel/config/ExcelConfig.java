package com.tl.excel.config;

import com.tl.core.TLConfig;
import com.tl.excel.util.ExcelConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * XSSFConfig
 *
 * @author Wesley
 * @since 2023/07/12
 */
@Getter
@Setter
public class ExcelConfig extends TLConfig {

	Set<String> groupNamePrefixSet;

	public static ExcelConfig createDefault() {
		ExcelConfig excelConfig = new ExcelConfig();
		excelConfig.groupNamePrefixSet = new HashSet<>();
		excelConfig.groupNamePrefixSet.add(ExcelConstant.NAME_PREFIX_LIST);
		return excelConfig;
	}

}
