/**
 * 
 */
package com.iyuba.core.common.manager;

import java.util.ArrayList;
import java.util.List;

import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.me.sqlite.mode.Attention;
import com.iyuba.core.me.sqlite.mode.DoingsCommentInfo;
import com.iyuba.core.me.sqlite.mode.DoingsInfo;
import com.iyuba.core.me.sqlite.mode.Fans;
import com.iyuba.core.me.sqlite.mode.FindFriends;
import com.iyuba.core.me.sqlite.mode.MessageLetter;
import com.iyuba.core.me.sqlite.mode.MutualAttention;
import com.iyuba.core.me.sqlite.mode.NearFriendInfo;
import com.iyuba.core.me.sqlite.mode.SearchItem;

/**
 * 社交信息管理
 * 
 * @author chentong
 * @version 1.0
 */
public class SocialDataManager {

	private static SocialDataManager instance;

	public SocialDataManager() {
		// TODO Auto-generated constructor stub
	}

	public static synchronized SocialDataManager Instance() {
		if (instance == null) {
			instance = new SocialDataManager();
		}
		return instance;
	}

	public String userid;
	public String userName;
	public DoingsInfo doingsInfo;
	public DoingsCommentInfo doingsCommentInfo;
	public Fans fans;
	public Attention attention;
	public MutualAttention mutualAttention;
	public List<DoingsInfo> doingsList = new ArrayList<DoingsInfo>();
	public List<DoingsCommentInfo> doingsCommentInfoList = new ArrayList<DoingsCommentInfo>();
	public MessageLetter letter = new MessageLetter();
	public List<MessageLetter> letterlist = new ArrayList<MessageLetter>();
	public UserInfo userInfo = new UserInfo();
	public FindFriends friendInfo = new FindFriends();
	public SearchItem searchItem = new SearchItem();
	public boolean nearDataRefreshed = false;// 判断是否刷新过了
	public List<NearFriendInfo> nearFriendInfos = new ArrayList<NearFriendInfo>();
	public List<Attention> attentions = new ArrayList<Attention>();

}
