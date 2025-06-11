package com.iyuba.core.iyumooc.microclass.bean;

import java.util.List;

/**
 * 作者：renzhy on 16/5/17 17:29
 * 邮箱：renzhongyigoo@gmail.com
 */
public class SlideShowListBean {

	/**
	 * result : 1
	 * data : [{"id":"26","price":"8600","name":"托福听力","pic":"upload/1430274693593.png","ownerid":"7","desc1":"听得好才能记得好"},{"id":"901","price":"0","name":"词汇语法双进阶《新概念2》","pic":"upload/1463120559348.png","ownerid":"21","desc1":"词汇语法双进阶《新概念2》"},{"id":"143","price":"0","name":"四级新闻听力模拟训练","pic":"upload/1461831433494.png","ownerid":"2","desc1":"四级新闻听力模拟训练"}]
	 */

	private String result;
	/**
	 * id : 26
	 * price : 8600
	 * name : 托福听力
	 * pic : upload/1430274693593.png
	 * ownerid : 7
	 * desc1 : 听得好才能记得好
	 */

	private List<SlideShowDataBean> data;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<SlideShowDataBean> getData() {
		return data;
	}

	public void setData(List<SlideShowDataBean> data) {
		this.data = data;
	}

	public static class SlideShowDataBean {
		private String id;
		private String price;
		private String name;
		private String pic;
		private String ownerid;
		private String desc1;

		public SlideShowDataBean(String id, String price, String name, String pic, String ownerid, String desc1) {
			this.id = id;
			this.price = price;
			this.name = name;
			this.pic = pic;
			this.ownerid = ownerid;
			this.desc1 = desc1;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

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

		public String getPic() {
			return pic;
		}

		public void setPic(String pic) {
			this.pic = pic;
		}

		public String getOwnerid() {
			return ownerid;
		}

		public void setOwnerid(String ownerid) {
			this.ownerid = ownerid;
		}

		public String getDesc1() {
			return desc1;
		}

		public void setDesc1(String desc1) {
			this.desc1 = desc1;
		}
	}
}
