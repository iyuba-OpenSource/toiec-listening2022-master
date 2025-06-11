package com.iyuba.core.microclass.sqlite.mode;

import java.io.Serializable;

public class SlideShowCourse implements Serializable {


	public int id;
	public double price;
	public String name;
	public int ownerid;
	public String pic;
	public String desc1;
	
	
	
	public SlideShowCourse() {
		super();
	}

	public SlideShowCourse(int id, double price, String name, int ownerid,
			String pic, String desc1) {
		super();
		this.id = id;
		this.price = price;
		this.name = name;
		this.ownerid = ownerid;
		this.pic = pic;
		this.desc1 = desc1;
	}
}

