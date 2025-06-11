package com.iyuba.toeiclistening.entity;

import com.iyuba.core.entity.EvaluateBean;

import java.util.List;

/**
 * 
 * Text表的数据库模式，文章每一句话的表模式 ,一个Text是一句
 * 
 */
public class Text {
	public int TestType;
	public int PartType;
	public int TitleNum;//文章的id 文章唯一  9105
	public String TitleName;//文章的名称
	public int SenIndex;//句子id
	public String Sentence=""; //句子原文
	public int Timing;//语音的开始时间
	public String Sound;//同一篇文章用同一段语音
	public boolean isChoose;// 评测列表是否选择
    public String shuoShuoId;
	public List<EvalWord> words;  // 句子拆分成的单词list，包含每个单词评测得分


	public boolean isEvaluate() {
		if (isEvaluateDB==0){
			return false;
		}else {
			return true;
		}
	}

	public void setEvaluate(boolean evaluate) {
		if (evaluate){
			isEvaluateDB=1;
		}else {
			isEvaluateDB=0;
		}
	}

	public int isEvaluateDB;//是否评测过

	public boolean isEvaluating;//是否评测中
	public boolean isPlay;//是否播放中

	public EvaluateBean getBean() {
		return bean;
	}

	public void setBean(EvaluateBean bean) {
		this.bean = bean;
	}

	public EvaluateBean bean;
}