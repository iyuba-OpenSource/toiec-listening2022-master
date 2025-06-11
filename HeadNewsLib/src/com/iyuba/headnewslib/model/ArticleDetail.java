package com.iyuba.headnewslib.model;

public class ArticleDetail {
	private static final String TAG = ArticleDetail.class.getSimpleName();

	private int NewsId;
	private String Timing;
	private int ParaId;
	private int IdIndex;
	private String Sentence;
	private String EndTime;
	private String ImgPath;
	private String Sentence_cn;
	
	public boolean hasNoPicture(){
		return ImgPath.equals("");
	}

	public String getTiming() {
		return Timing;
	}

	public void setTiming(String timing) {
		Timing = timing;
	}

	public int getParaId() {
		return ParaId;
	}

	public void setParaId(int paraId) {
		ParaId = paraId;
	}

	public int getIdIndex() {
		return IdIndex;
	}

	public void setIdIndex(int idIndex) {
		IdIndex = idIndex;
	}

	public String getSentence() {
		return Sentence;
	}

	public void setSentence(String sentence) {
		Sentence = sentence;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	public String getImgPath() {
		return ImgPath;
	}

	public void setImgPath(String imgPath) {
		ImgPath = imgPath;
	}

	public String getSentence_cn() {
		return Sentence_cn;
	}

	public void setSentence_cn(String sentence_cn) {
		Sentence_cn = sentence_cn;
	}

	public int getNewsId() {
		return NewsId;
	}

	public void setNewsId(int newsId) {
		NewsId = newsId;
	}

	@Override
	public String toString() {
		return "DataDetails [Timing=" + Timing + ", ParaId=" + ParaId + ", IdIndex=" + IdIndex
				+ ", Sentence=" + Sentence + ", EndTime=" + EndTime + ", ImgPath=" + ImgPath
				+ ", Sentence_cn=" + Sentence_cn + ", NewsId=" + NewsId + "]";
	}

}
