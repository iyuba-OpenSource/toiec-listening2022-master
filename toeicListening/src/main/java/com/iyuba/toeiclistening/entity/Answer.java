package com.iyuba.toeiclistening.entity;

public class Answer {
	public int TestType;
	public int PartType;
	public int TitleNum;//文章的id
	public int QuesIndex;//问题的id
	public String QuesText;
	public String QuesImage;
	public int AnswerNum;//答案的个数
	public String AnswerText;
	public String Answer;//答案 ，多选用++连接
	public String Sound;
	public int IsSingle;//是不是单选
}
