package com.iyuba.toeiclistening.manager;

import java.util.ArrayList;

import com.iyuba.toeiclistening.entity.Answer;
import com.iyuba.toeiclistening.entity.BlogContent;
import com.iyuba.toeiclistening.entity.Explain;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.User;

public class DataManager {
	private static DataManager instance;

	public static DataManager Instance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public ArrayList<Answer> anwserList = null;
	public ArrayList<Text> textList = null;
	public ArrayList<TitleInfo> titleInfoList = null;
	public ArrayList<PackInfo> packInfoList = null;
	public ArrayList<NewWord> newWordList = null;// 收藏的单词的列表
	public ArrayList<TitleInfo> favTitleInfoList = null;// 收藏的文章列表
	public ArrayList<BlogContent> blogList = new ArrayList<BlogContent>();// 资讯列表
	public User user = null;
	public Explain explain=null;
	public BlogContent blogContent = new BlogContent();
}
