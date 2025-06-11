package com.iyuba.core.iyumooc.microclass.bean;

import java.util.List;

/**
 * 作者：renzhy on 16/5/20 16:56
 * 邮箱：renzhongyigoo@gmail.com
 */
public class CoursePackListBean {

	/**
	 * result : 1
	 * uid : 0
	 * firstPage : 1
	 * lastPage : 7
	 * data : [{"price":"199","name":"四级新闻听力模拟训练","id":143,"pic":"2-143","classNum":21,"viewCount":47981,"ownerid":"2","realprice":"599","desc":"20篇四级新闻听力，分话题模拟训练"},{"price":"199","name":"四级改革必考词汇","id":140,"pic":"2-140","classNum":30,"viewCount":59166,"ownerid":"2","realprice":"299","desc":"2016.6四级听力改革之新闻类词汇，每个单词包含原汁原味地道的新闻例句。早听早过！"},{"price":"199","name":"四级词汇\u2014词根词缀速记","id":142,"pic":"2-142","classNum":42,"viewCount":46622,"ownerid":"2","realprice":"299","desc":"英语四级词汇词根词缀记忆法，一次记忆，终身不遗忘！"},{"price":"24","name":"动画电影《冰雪奇缘》精选","id":138,"pic":"2-138","classNum":8,"viewCount":24272,"ownerid":"2","realprice":"50","desc":"四级听力零基础课程-看电影学英语"},{"price":"10","name":"四级听力发音技巧","id":136,"pic":"2-136","classNum":3,"viewCount":20934,"ownerid":"2","realprice":"50","desc":"听力基础课程-突破发音障碍"},{"price":"89","name":"听力长对话技巧精讲","id":20,"pic":"2-20","classNum":14,"viewCount":57780,"ownerid":"2","realprice":"119","desc":"长对话选项的阅读有哪些法则？"},{"price":"89","name":"短文听力技巧精讲","id":21,"pic":"2-21","classNum":15,"viewCount":46369,"ownerid":"2","realprice":"119","desc":"如何在听的过程中抓短文听力的重点？"},{"price":"89","name":"听力技巧真题实战","id":23,"pic":"2-23","classNum":28,"viewCount":49604,"ownerid":"2","realprice":"119","desc":"实践检验技巧实用与否的唯一标准。"},{"price":"0","name":"写作解读与审题","id":98,"pic":"2-98","classNum":3,"viewCount":59294,"ownerid":"2","realprice":"69","desc":"如何审题才能不跑题？法宝在此。"},{"price":"139","name":"救命必背万能作文","id":100,"pic":"2-100","classNum":4,"viewCount":63846,"ownerid":"2","realprice":"169","desc":"一篇适用于19次真题的作文。"},{"price":"169","name":"图画图表保命作文","id":101,"pic":"2-101","classNum":12,"viewCount":14071,"ownerid":"2","realprice":"209","desc":"保命作文在手，四级考试不愁。"},{"price":"131","name":"半开放式作文与应用文","id":102,"pic":"2-102","classNum":9,"viewCount":8894,"ownerid":"2","realprice":"153","desc":"有备无患，怎么考都不怕。"},{"price":"269","name":"仔细阅读解题秘诀","id":125,"pic":"2-125","classNum":22,"viewCount":26327,"ownerid":"2","realprice":"360","desc":"掌握做题秘诀，通过四级so easy!"},{"price":"130","name":"翻译万能救命句式","id":108,"pic":"2-108","classNum":12,"viewCount":40319,"ownerid":"2","realprice":"250","desc":"听完立保50分，直达425！"},{"price":"110","name":"翻译万能救命素材","id":109,"pic":"2-109","classNum":8,"viewCount":12461,"ownerid":"2","realprice":"170","desc":"秒杀翻译\u201c三步法\u201d！ "},{"price":"110","name":"翻译真题逐句精讲","id":110,"pic":"2-110","classNum":9,"viewCount":9402,"ownerid":"2","realprice":"180","desc":"真题价值十倍于模拟题！"},{"price":"199","name":"28天四级听力魔音训练","id":127,"pic":"2-127","classNum":29,"viewCount":56927,"ownerid":"2","realprice":"480","desc":"4套真题，5轮训练，纠正美音，彻底击破听力！"},{"price":"49","name":"2015.6四级全套真题解析","id":116,"pic":"2-116","classNum":24,"viewCount":118551,"ownerid":"2","realprice":"99","desc":"逐题解析,自习必备。"},{"price":"49","name":"2014.12四级全套真题解析","id":119,"pic":"2-119","classNum":22,"viewCount":32130,"ownerid":"2","realprice":"99","desc":"逐题解析,自习必备。"},{"price":"49","name":"2014.6四级全套真题解析","id":120,"pic":"2-120","classNum":31,"viewCount":22837,"ownerid":"2","realprice":"99","desc":"逐题解析,自习必备。"}]
	 * nextPage : 2
	 * appid : -2
	 * prevPage : 1
	 * currentPage : 1
	 */

	private int result;
	private int uid;
	private int firstPage;
	private int lastPage;
	private int nextPage;
	private int appid;
	private int prevPage;
	private int currentPage;
	/**
	 * price : 199
	 * name : 四级新闻听力模拟训练
	 * id : 143
	 * pic : 2-143
	 * classNum : 21
	 * viewCount : 47981
	 * ownerid : 2
	 * realprice : 599
	 * desc : 20篇四级新闻听力，分话题模拟训练
	 */

	private List<CoursePackDataBean> data;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public List<CoursePackDataBean> getData() {
		return data;
	}

	public void setData(List<CoursePackDataBean> data) {
		this.data = data;
	}

	public static class CoursePackDataBean {
		private String price;
		private String name;
		private int id;
		private String pic;
		private int classNum;
		private int viewCount;
		private String ownerid;
		private String realprice;
		private String desc;

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}

		public int getClassNum() {
			return classNum;
		}

		public void setClassNum(int classNum) {
			this.classNum = classNum;
		}

		public int getViewCount() {
			return viewCount;
		}

		public void setViewCount(int viewCount) {
			this.viewCount = viewCount;
		}

		public String getOwnerid() {
			return ownerid;
		}

		public void setOwnerid(String ownerid) {
			this.ownerid = ownerid;
		}

		public String getRealprice() {
			return realprice;
		}

		public void setRealprice(String realprice) {
			this.realprice = realprice;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
