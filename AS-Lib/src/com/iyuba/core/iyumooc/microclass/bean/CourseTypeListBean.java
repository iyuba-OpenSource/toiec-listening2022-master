package com.iyuba.core.iyumooc.microclass.bean;

import java.util.List;

/**
 * 作者：renzhy on 16/5/23 15:04
 * 邮箱：renzhongyigoo@gmail.com
 */
public class CourseTypeListBean {

	/**
	 * result : 1
	 * total : 13
	 * data : [{"condition":"全部课程","name":"全部课程","destination":"全部课程","id":-2,"type":-2,"desc":"class.all"},{"condition":"最新课程","name":"最新课程","destination":"最新课程","id":-1,"type":-1,"desc":"class.new"},{"condition":"","name":"英语四级","destination":"此处添加教学目标","id":2,"type":2,"desc":"class.cet4"},{"condition":"1.喜欢不断挑战自己英语水平的你；\n2.没有英语语言环境，但想保持高水准的我；\n3.痴迷VOA新闻需要理由吗？就是这么任性的他。","name":"VOA英语","destination":"语速有点儿快，词汇量有点儿大，但充满挑战！你\u2014\u2014准备好了吗？","id":3,"type":2,"desc":"class.voa"},{"condition":"1. 想一次性斩断和六级听力\u201c瓜葛\u201d的人群；2. 想利用最短时间，解决六级听力困扰的学生；3. 对听力这一题型始终找不到技巧和方法的学生；4. 在听力上付出了很多努力，但仍效果甚微的学员。","name":"英语六级","destination":"此处添加教学目标","id":4,"type":2,"desc":"class.cet6"},{"condition":"1. 心早已飞跃大洋彼岸，却只因听力绊住了双脚的你；\n2. 想利用最短时间，和托福听力永不再见的俊男靓女；\n3. 对听力这一题型始终找不到技巧和方法的萌货；\n4. 在听力上付出了很多努力，但仍效果甚微的烤鸭。","name":"托福TOEFL","destination":"你有哪些问题，我就讲哪些问题，我们的目标是短期内提分！","id":7,"type":2,"desc":"class.toefl"},{"condition":"1.初试科目为英语（一）的考生；\n2.希望考研英语取得高分的考生；\n3.打算早作准备却不知如何下手的考生。","name":"考研英语一","destination":"此处添加教学目标","id":8,"type":2,"desc":"class.kaoyan"},{"condition":"1.英语基础差；\n2.想提高英语却无从下手","name":"新概念英语","destination":"此处添加教学目标","id":21,"type":2,"desc":"class.jichu"},{"condition":"1.英语基础差；2.想通过看电影或美剧等提高英语","name":"走遍美国","destination":"此处添加教学目标","id":22,"type":2,"desc":"class.zoubianmeiguo"},{"condition":"","name":"学位英语","destination":"此处添加教学目标","id":28,"type":2,"desc":"class.xuewei"},{"condition":"1.初试科目为英语（二）的考生；\n2.希望考前冲刺能取得高分的考生；\n3.临近考试却不知如何下手的考生。","name":"考研英语二","destination":"此处添加教学目标","id":52,"type":2,"desc":"class.kaoyan"},{"condition":"1.自己复习毫无章法的童鞋；2. 真题练习没有效果的童鞋；3. 基础薄弱、雅思屡考不过的童鞋","name":"雅思","destination":"认真学习此课程，与雅思痛快分手！","id":61,"type":2,"desc":"class.ielts"},{"condition":"1.参加日语N1能力考试的学员；\n","name":"日语N1","destination":"此处添加教学目标","id":1,"type":3,"desc":"class.jlpt1"},{"condition":"1.想参加日语N2能力考试的学员；\n2.有一定基础，希望通过听力专项冲刺提高听力单项分数的N2级学员。","name":"日语N2","destination":"此处添加教学目标","id":5,"type":3,"desc":"class.jlpt2"},{"condition":"1、日语零基础或稍有基础，想要系统学习日语的同学；\n2、爱好日语、喜欢动漫、日剧、日本音乐，想独立看懂、听懂日语的同学；\n3、想去日本旅游、留学、工作，希望可以用日语交流沟通的同学；","name":"日语N3","destination":"此处添加教学目标","id":6,"type":3,"desc":"class.jlpt3"}]
	 */

	private int result;
	private int total;
	/**
	 * condition : 全部课程
	 * name : 全部课程
	 * destination : 全部课程
	 * id : -2
	 * type : -2
	 * desc : class.all
	 */

	private List<CourseTypeDataBean> data;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<CourseTypeDataBean> getData() {
		return data;
	}

	public void setData(List<CourseTypeDataBean> data) {
		this.data = data;
	}

	public static class CourseTypeDataBean {
		private String condition;
		private String name;
		private String destination;
		private int id;
		private int type;
		private String desc;

		public String getCondition() {
			return condition;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
