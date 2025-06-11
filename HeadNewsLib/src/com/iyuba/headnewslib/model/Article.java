package com.iyuba.headnewslib.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
private static final String TAG = Article.class.getSimpleName();
	
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat ownSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private int TopicId;
	private int WordCount; // 新闻字数
	private String DescCn;
	private int Flag;
	private String Title_cn; // 中文标题
	private double HardWeight; // 难度
	private String Title; // 英文标题
	private int NewsId; // 新闻id
	private String CreatTime;
	private String Source;
	private int Category; // 类别标识
	private String Sound;
	private String Pic; // 配图文件名(38332.jpg)
	private int ReadCount; // 被阅读次数
	
	public Article(){
		this.NewsId = 0;
	}

	@Override
	public String toString() {
		return "Article [TopicId=" + TopicId + ", WordCount=" + WordCount + ", DescCn=" + DescCn
				+ ", Flag=" + Flag + ", Title_cn=" + Title_cn + ", HardWeight=" + HardWeight + ", Title="
				+ Title + ", NewsId=" + NewsId + ", CreatTime=" + CreatTime + ", Source=" + Source
				+ ", Category=" + Category + ", Sound=" + Sound + ", Pic=" + Pic + ", ReadCount="
				+ ReadCount + "]";
	}
	
	public boolean isNullObject(){
		return this.NewsId == 0;
	}

	public boolean isNewerThan(Article b) {
		return this.NewsId > b.getNewsId();
	}

	public String getSource() {
		return this.Source;
	}

	public void setSource(String source) {
		this.Source = source;
	}

	public int getTopicId() {
		return TopicId;
	}

	public void setTopicId(int topicId) {
		TopicId = topicId;
	}

	public int getWordCount() {
		return WordCount;
	}

	public void setWordCount(int wordCount) {
		WordCount = wordCount;
	}

	public String getDescCn() {
		return DescCn;
	}

	public void setDescCn(String descCn) {
		DescCn = descCn;
	}

	public int getFlag() {
		return Flag;
	}

	public void setFlag(int flag) {
		Flag = flag;
	}

	public String getTitle_cn() {
		return Title_cn;
	}

	public void setTitle_cn(String title_cn) {
		Title_cn = title_cn;
	}

	public double getHardWeight() {
		return HardWeight;
	}

	public void setHardWeight(double hardWeight) {
		HardWeight = hardWeight;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public int getNewsId() {
		return NewsId;
	}

	public void setNewsId(int newsId) {
		NewsId = newsId;
	}

	public String getCreatTime() {
		return CreatTime;
	}

	/**
	 * 
	 * @param today
	 *          DateFormat for today
	 * @param before
	 *          DateFormat for before
	 */
	public String getCreatTimeInFormat(SimpleDateFormat today, SimpleDateFormat before) {
		String result = "";
		try {
			Date date = ownSDF.parse(CreatTime);
			result = isToday(date) ? today.format(date) : before.format(date);
		} catch (ParseException e) {
			result = CreatTime;
		}
		return result;
	}

	private boolean isToday(Date date) {
		Date now = new Date();
		return now.getDate() == date.getDate() && (now.getTime() - date.getTime()) < 86400000;
	}

	public void setCreatTime(String creatTime) {
		CreatTime = creatTime;
	}

	public int getCategory() {
		return Category;
	}

	public void setCategory(int category) {
		Category = category;
	}

	public String getSound() {
		return Sound;
	}

	public void setSound(String sound) {
		Sound = sound;
	}

	public String getPic() {
		return Pic;
	}
	
	public String getSmallPic(){
		StringBuilder sb = new StringBuilder();
		String[] xx = Pic.split("\\.");
		if(xx.length == 2){
			sb.append(xx[0]).append("_s.").append(xx[1]);
		} else {
			sb.append(Pic);
		}
		return sb.toString();
	}

	public void setPic(String pic) {
		Pic = pic;
	}

	public int getReadCount() {
		return ReadCount;
	}

	public void setReadCount(int readCount) {
		ReadCount = readCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(TopicId);
		dest.writeInt(WordCount);
		dest.writeString(DescCn);
		dest.writeInt(Flag);
		dest.writeString(Title_cn);
		dest.writeDouble(HardWeight);
		dest.writeString(Title);
		dest.writeInt(NewsId);
		dest.writeString(CreatTime);
		dest.writeString(Source);
		dest.writeInt(Category);
		dest.writeString(Sound);
		dest.writeString(Pic);
		dest.writeInt(ReadCount);
	}

	public static final Parcelable.Creator<Article> CREATOR = new Creator<Article>() {

		@Override
		public Article createFromParcel(Parcel source) {
			Article article = new Article();
			article.TopicId = source.readInt();
			article.WordCount = source.readInt();
			article.DescCn = source.readString();
			article.Flag = source.readInt();
			article.Title_cn = source.readString();
			article.HardWeight = source.readDouble();
			article.Title = source.readString();
			article.NewsId = source.readInt();
			article.CreatTime = source.readString();
			article.Source = source.readString();
			article.Category = source.readInt();
			article.Sound = source.readString();
			article.Pic = source.readString();
			article.ReadCount = source.readInt();
			return article;
		}

		@Override
		public Article[] newArray(int size) {
			return new Article[size];
		}
	};

}
