package com.iyuba.toeiclistening.payment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;

import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.entity.User;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

public class struct_user {
	private static User user;
	private static ZDBHelper helper;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static void struct_by_login(Context context, String deadline) {
		helper = new ZDBHelper(context);
		String username = AccountManagerLib.Instace(context).userName;
		User user1=helper.getUser(username);
		user = new User(username, deadline, context);
		if(user1==null){
			helper.addUser(user);
		}else{
			helper.updateUser(user);
		}		
	}

	public static void struct_by_deadline(Context context, int type, Calendar c) {
		helper = new ZDBHelper(context);
		String userName = AccountManagerLib.Instace(context).userName;
		String validity = ConfigManagerMain.Instance().loadString("validity");
		user=helper.getUser(userName);
		switch (type) {
		case 50:
			c.add(Calendar.DATE, 90);
			try {
				if (sdf.parse(validity).getTime() <= c.getTime().getTime())
					user = new User(userName, sdf.format(c.getTime()), context);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 100:
		case 110:
			c.add(Calendar.DATE, 180);
			try {
				if (sdf.parse(validity).getTime() <= c.getTime().getTime())
					user = new User(userName, sdf.format(c.getTime()), context);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 200:
		case 220:
			c.add(Calendar.YEAR, 1);
			try {
				if (sdf.parse(validity).getTime() <= c.getTime().getTime())
					user = new User(userName, sdf.format(c.getTime()), context);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 600:
			user = new User(userName, "终身VIP", context);
			break;
		}
		helper.updateUser(user);
	}

	public static void restruct(Context context, int type) {
		helper = new ZDBHelper(context);
		String userName = AccountManagerLib.Instace(context).userName;
		switch (type) {
		case 50:
			user = new User(userName, 1, context);
			break;
		case 100:
		case 110:
			user = new User(userName, 2, context);
			break;
		case 200:
		case 220:
			user = new User(userName, 3, context);
			break;
		case 600:
			user = new User(userName, 4, context);
			break;
		default:
			user = new User(userName, 1, context);
			break;
		}
		helper.updateUser(user);
	}
}
