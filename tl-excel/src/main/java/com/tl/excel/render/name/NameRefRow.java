package com.tl.excel.render.name;

import java.util.ArrayList;
import java.util.List;

/**
 * NameRefRow
 *
 * @author WangPanYong
 * @since 2024/07/16
 */
public class NameRefRow {
	int row;

	float heightInPoints;

	boolean zeroHeight;

	List<NameRefCol> refCols = new ArrayList<>();

}
