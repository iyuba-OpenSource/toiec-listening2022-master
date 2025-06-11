package com.iyuba.toeiclistening.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.entity.User;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.listener.OnResultListener;
import com.iyuba.toeiclistening.payment.struct_user;
import com.iyuba.toeiclistening.protocol.CheckAmountRequest;
import com.iyuba.toeiclistening.protocol.CheckAmountResponse;
import com.iyuba.toeiclistening.protocol.GetVipInfoRequest;
import com.iyuba.toeiclistening.protocol.GetVipInfoResponse;
import com.iyuba.toeiclistening.protocol.PayRecordRequest;
import com.iyuba.toeiclistening.protocol.PayRecordResponse;
import com.iyuba.toeiclistening.protocol.PayRequest;
import com.iyuba.toeiclistening.protocol.PayResponse;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;

/**
 * 鐖辫甯佺鐞?
 * 
 * @author chentong
 * 
 */
public class PayManager {
	private static PayManager instance;
	private static Context mContext;
	private int pay_amount;
	private Date lasttime;

	public static PayManager Instance(Context mContext) {
		if (instance == null) {
			instance = new PayManager(mContext);
		}
		PayManager.mContext = mContext;
		return instance;
	}

	private PayManager(Context mContext) {
	}

	public void checkPaidRecord(final String userId,
			final OnResultListener resultListener) {
		ClientSession.Instace().asynGetResponse(new PayRecordRequest(userId),
				new IResponseReceiver() {

					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
						PayRecordResponse payRecordResponse = (PayRecordResponse) response;
						if (payRecordResponse.result.equals("1"))// 宸蹭粯璐癸紝鎶婅褰曟彃鍏ユ暟鎹簱
						{
							resultListener.OnSuccessListener(null);
						} else {// 鏈粯璐癸紝鍒颁粯璐硅姹?
							resultListener.OnFailureListener(null);
						}

					}
				}, null, null);
	}
	/**
	 * 
	 * 检查产品的vip信息
	 */
	public void recoverAppVip(final String userId,final String productId
			,final OnResultListener resultListener){
		ClientSession.Instace().asynGetResponse(new GetVipInfoRequest(userId, productId),
			new IResponseReceiver(){

				@Override
				public void onResponse(BaseHttpResponse response,
						BaseHttpRequest request, int rspCookie) {
					// TODO Auto-generated method stub
					GetVipInfoResponse res=(GetVipInfoResponse)response;
					if(res.result.equals("1")){
						if(res.vipFlg.equals("1")){
							resultListener.OnSuccessListener(null);
							return;
						}
					}
					resultListener.OnFailureListener(null);
				}
		});
	}

	/**
	 * 
	 * @param userId
	 * @param resultListener
	 *            检查用户的爱语币
	 */
	public void checkAmount(final String userId,
			final OnResultListener resultListener) {
		ClientSession.Instace().asynGetResponse(new CheckAmountRequest(userId),
				new IResponseReceiver() {
					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
						CheckAmountResponse checkAmountResponse = (CheckAmountResponse) response;
						if (checkAmountResponse.result.equals("1"))// 鏌ヨ澶辫触锛屾彁绀洪敊璇俊鎭?
						{
							resultListener.OnSuccessListener(checkAmountResponse.amount);
						}
					}
				}, null, null);
	}

	/**
	 * 
	 * @param userId
	 *            AccountManagerLib.Instace(mContext).userId
	 * @param amount
	 *            10
	 * @param resultListener
	 *            鍔熻兘锛?
	 */
	public void payAmount(final String userId, final String amount,
			final OnResultListener resultListener) {
		ClientSession.Instace().asynGetResponse(new PayRequest(userId, amount),
				new IResponseReceiver() {
					@Override
					public void onResponse(BaseHttpResponse response,
							BaseHttpRequest request, int rspCookie) {
						PayResponse payResponse = (PayResponse) response;

						if (payResponse.result.equals("1"))// 鏀粯鎴愬姛
						{
							pay_amount = Integer.parseInt(amount);
							//getRecord();
							resultListener
									.OnSuccessListener(payResponse.amount);
						} else {// 鏀粯澶辫触
							if (Integer.parseInt(payResponse.amount) < Integer
									.parseInt(amount))
							{
								resultListener
										.OnFailureListener(payResponse.amount);
							} else {
								resultListener
										.OnFailureListener(payResponse.msg);
							}

						}
					}
				}, null, null);
	}

	private void getRecord() {
		String userName = AccountManagerLib.Instace(mContext).userName;
		User user;
		ZDBHelper zHelper = new ZDBHelper(mContext);
		user = zHelper.getUser(userName);
		if (user == null) {
			user = new User(userName);
			zHelper.addUser(user);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			lasttime = sdf.parse(user.deadline);
		} catch (ParseException e) {
		}
		Calendar c = Calendar.getInstance();
		c.setTime(lasttime);
		if (lasttime.before(new User().getNetTime()) && user.isvip >0) {//isvip == 1
			struct_user.restruct(mContext, pay_amount);
		} else {
			struct_user.struct_by_deadline(mContext, pay_amount, c);
		}
	}
}
