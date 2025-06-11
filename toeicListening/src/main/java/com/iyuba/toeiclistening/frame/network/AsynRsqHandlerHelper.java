package com.iyuba.toeiclistening.frame.network;


import com.iyuba.toeiclistening.frame.components.Cache;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseResponse;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;


/**
 * 寮傛璇锋眰澶勭悊鍣?
 * 
 * @author wuwei
 * 
 *         姣忎竴娆¤姹傞兘鏄?杩囦竴涓嚎绋嬫潵瀹屾垚锛岃繖鏍峰彲瀹炵幇寮傛閫氳锛屼絾锛岀嚎绋嬪悓姝ャ?涓柇绛夊鍙兘浼氬彂鐢熷紓甯?
 * 
 */
public class AsynRsqHandlerHelper extends Thread {

	private int rspCookie = -1;
	private BaseHttpRequest request;
	private IResponseReceiver rspReceiver;
	private IErrorReceiver errorReceiver;
	private INetStateReceiver stateReceiver;
	private boolean isWorking = false;
	private boolean isBad = false;
	private boolean isCanceled = false;

	// // 鏈湴鍥炲缂撳瓨,鏈?ぇ缂撳瓨鏁伴噺涓?0
	// static private Cache rspCache = new Cache(10);

	public final boolean commitRequest(int rspCookie, BaseHttpRequest request,
			IResponseReceiver rspReceiver, IErrorReceiver errorReceiver,
			INetStateReceiver stateReceiver) {
		setCancelflag(false);
		setWorkflag(true);
		reset(rspCookie, request, rspReceiver, errorReceiver, stateReceiver);

		if (!isAlive()) {
			try {
				start();
			} catch (IllegalThreadStateException e) {
				setBadflag(true);
				setWorkflag(false);
				reset(-1, null, null, null, null);
				return false;
			}

		}

		synchronized (this) {
			notifyAll();
		}

		return true;
	}

	public final void cancel() {
		setCancelflag(true);
		if (isAlive()) {
			interrupt();
		}
	}

	public final boolean isWorking() {
		return isWorking;
	}

	public final boolean isBad() {
		return isBad;
	}

	private void reset(int rspCookie, BaseHttpRequest request,
			IResponseReceiver rspReceiver, IErrorReceiver errorReceiver,
			INetStateReceiver stateReceiver) {
		this.rspCookie = rspCookie;
		this.request = request;
		this.rspReceiver = rspReceiver;
		this.errorReceiver = errorReceiver;
		this.stateReceiver = stateReceiver;
	}

	private void setWorkflag(boolean isWorking) {
		this.isWorking = isWorking;
	}

	private void setBadflag(boolean isBad) {
		this.isBad = isBad;
	}

	private boolean isCanceled() {
		return isCanceled;
	}

	private void setCancelflag(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	synchronized static BaseHttpResponse getCacheResponse(
			BaseHttpRequest request) {
		int a = request.hashCode();
		Object aObject = Cache.Instance().get(a);
		return (BaseHttpResponse) aObject;
	}

	synchronized static void addCacheResponse(BaseHttpRequest request,
			BaseHttpResponse response) {
		Cache.Instance().add(new Integer(request.hashCode()), response);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		BaseHttpRequest request = null;
		IResponseReceiver rspReceiver = null;
		IErrorReceiver errorReceiver = null;
		INetStateReceiver stateReceiver = null;
		int rspCookie = 0;

		try {
			while (!isWorking()) {
				synchronized (this) {
					wait();
				}
			}

			request = this.request;
			rspReceiver = this.rspReceiver;
			errorReceiver = this.errorReceiver;
			stateReceiver = this.stateReceiver;
			rspCookie = this.rspCookie;
			reset(-1, null, null, null, null);

			if (isCanceled()) {
				setWorkflag(false);
				if (stateReceiver != null) {
					stateReceiver.onCancel(request, rspCookie);
				}
				// continue;
			}

			// // 浼樺厛浠庣紦瀛樹腑璇诲彇
			// BaseHttpResponse cacheRsp = getCacheResponse(request);
			// if (cacheRsp != null && request.isGetCache) {
			// if (rspReceiver != null) {
			//
			// rspReceiver.onResponse(cacheRsp, request, rspCookie);
			//
			// // 璁╂帴鏀跺櫒鏈夋満浼氱煡閬撳鐞嗗畬鍏∣K,null琛ㄧず鎴愬姛
			// if (errorReceiver != null) {
			// errorReceiver.onError(null, request, rspCookie);
			// }
			// }
			// }
			// // 缂撳瓨鏈懡涓紝浠庢湇鍔″櫒璇诲彇
			// else {
			BaseResponse rsp = RsqHandleHelper.getResponseImpl(rspCookie,
					request, stateReceiver);

			if (isCanceled()) {
				setWorkflag(false);
				if (stateReceiver != null) {
					stateReceiver.onCancel(request, rspCookie);
				}
				// continue;
			}
			if (rsp instanceof ErrorResponse) {
				if (errorReceiver != null) {

					errorReceiver.onError((ErrorResponse) rsp, request,
							rspCookie);
				}
			} else {
				// if (request.needCacheResponse()) {
				// addCacheResponse(request, (BaseHttpResponse) rsp);
				// }
				if (rspReceiver != null) {

					rspReceiver.onResponse((BaseHttpResponse) rsp, request,
							rspCookie);

				}

				// 璁╂帴鏀跺櫒鏈夋満浼氱煡閬撳鐞嗗畬鍏∣K,null琛ㄧず鎴愬姛
				if (errorReceiver != null) {
					errorReceiver.onError(null, request, rspCookie);
				}
			}
			// }

			setWorkflag(false);
		} catch (InterruptedException e) {// 瀵逛簬绾跨▼涓柇寮傚父鐨勫鐞嗭紝杩欓噷鍙兘浼氭湁闂锛岀洿鎺ュ悶鎺夊紓甯告槸鍚︽纭?
			e.printStackTrace();
			setWorkflag(false);

			if (errorReceiver != null) {
				ErrorResponse rsp = new ErrorResponse(
						ErrorResponse.ERROR_INTERRUPT);
				errorReceiver.onError(rsp, request, rspCookie);
			}
			if (stateReceiver != null) {
				stateReceiver.onCancel(request, rspCookie);
			}
		} catch (Exception e) {
			// 鍑虹幇鍏朵粬浠讳綍寮傚父锛岀疆鏈鐞嗗櫒閿欒鏍囧織锛?
			// 鍛婅瘔浣跨敤鑰呰嚜宸辨棤娉曞啀浣跨敤銆?
			e.printStackTrace();
			setBadflag(true);
			setWorkflag(false);
			if (stateReceiver != null) {
				stateReceiver.onCancel(request, rspCookie);
			}
			reset(-1, null, null, null, null);
			if (errorReceiver != null) {
				errorReceiver.onError(new ErrorResponse(
						ErrorResponse.ERROR_THREAD, "work thread error!"),
						request, rspCookie);
			}
		}
		super.run();
	}

}
