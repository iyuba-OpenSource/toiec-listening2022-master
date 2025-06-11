package com.iyuba.core.common.listener;

import android.view.KeyEvent;

/**
 * 用于解决ActivityGroup中集中解决KeyDown事件的处理
 * 
 * @author 陈彤
 * 
 */
public interface OnActivityGroupKeyDown {
	public boolean onSubActivityKeyDown(int keyCode, KeyEvent event);
}
