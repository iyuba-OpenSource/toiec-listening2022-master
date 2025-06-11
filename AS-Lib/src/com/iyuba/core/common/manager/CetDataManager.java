package com.iyuba.core.common.manager;

import java.util.ArrayList;

import com.iyuba.core.common.sqlite.mode.test.CetAnswer;
import com.iyuba.core.common.sqlite.mode.test.CetExplain;
import com.iyuba.core.common.sqlite.mode.test.CetFillInBlank;
import com.iyuba.core.common.sqlite.mode.test.CetText;

/**
 * cet数据管理
 * 
 * @author chentong
 * @version 1.0
 */
public class CetDataManager {
	public ArrayList<CetAnswer> answerList = new ArrayList<CetAnswer>();
	public ArrayList<CetExplain> explainList = new ArrayList<CetExplain>();
	public ArrayList<CetText> textList = new ArrayList<CetText>();
	public ArrayList<CetFillInBlank> blankList = new ArrayList<CetFillInBlank>();
	private static CetDataManager instance;

	private CetDataManager() {
	}

	public static synchronized CetDataManager Instace() {
		if (instance == null) {
			instance = new CetDataManager();
		}
		return instance;
	}
}
