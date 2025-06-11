package com.iyuba.core.me.activity;

/**
 * 编辑个人信息界面
 *
 * @author chentong
 * @version 1.0
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.LocationRequest;
import com.iyuba.core.common.protocol.base.LocationResponse;
import com.iyuba.core.common.protocol.message.RequestEditUserInfo;
import com.iyuba.core.common.protocol.message.RequestUserDetailInfo;
import com.iyuba.core.common.protocol.message.ResponseEditUserInfo;
import com.iyuba.core.common.protocol.message.ResponseUserDetailInfo;
import com.iyuba.core.common.sqlite.mode.UserInfo;
import com.iyuba.core.common.sqlite.op.SchoolOp;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.GetLocation;
import com.iyuba.core.common.util.JudgeZodicaAndConstellation;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.adapter.SchoolListAdapter;
import com.iyuba.core.me.protocol.ModifyUserNameRequest;
import com.iyuba.core.me.protocol.ModifyUserNameResponse;
import com.iyuba.core.me.sqlite.mode.EditUserInfo;
import com.iyuba.core.me.sqlite.mode.School;
import com.iyuba.core.R;

public class EditUserInfoActivity extends BasisActivity implements OnWheelChangedListener {
    private Context mContext;
    private TextView gender, birthday, zodiac, constellation, occupation, education, school, location;
    private EditText etAffectivestatus, etLookingfor, etInterest, etIntro;
    //	private EditText location;
    private LinearLayout changeImageLayout;
    private ImageView userImage;
    private Button back, save;
    private final static int GENDER_DIALOG = 1;// 性别选择
    private final static int DATE_DIALOG = 2;// 日期选择
    private final static int SCHOOL_DIALOG = 3;// 学校选择
    private final static int LOCATION_DIALOG = 4;// 学校选择
    private final static int OCCUPATION_DIALOG = 5;//身份选择
    private final static int EDUCATION_DIALOG = 6;//学历选择
    private Calendar calendar = null;
    private EditUserInfo editUserInfo = new EditUserInfo();
    private CustomDialog waitingDialog;
    private String cityName;
    // school
    private View schoolDialog;
    // location
    private View locationDialog;

    private EditText searchText;
    private Button sure;
    private View clear;
    private ListView schoolList;
    private ArrayList<School> schools = new ArrayList<School>();
    private SchoolListAdapter schoolListAdapter;
    private StringBuffer tempSchool;

    /**
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
     */
    private JSONObject mJsonObj;

    private Button btnShowChoose;

    /**
     * 省的WheelView控件
     */
    private WheelView mProvince;
    /**
     * 市的WheelView控件
     */
    private WheelView mCity;
    /*** 区的WheelView控件*/
    private WheelView mArea;

    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * key - 省 value - 市s
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /*** key - 市 values - 区s*/
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

    /*** 当前省的名称*/
    private String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    private String mCurrentCityName;
    /*** 当前区的名称*/
    private String mCurrentAreaName = "";
    private EditText editName;
    private String userName;
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edituserinfo);
        mContext = this;
         CrashApplication.addActivity(this);
        waitingDialog = WaittingDialog.showDialog(mContext);
        initWidget();
        LoadInfo();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewChange();
    }

    private void viewChange() {
        userInfo = AccountManagerLib.Instace(mContext).userInfo;
        if (userInfo != null) {
            editName.setText(userInfo.username);
        }
    }

    private void initWidget() {
        // TODO Auto-generated method stub
        editName = (EditText) findViewById(R.id.editName);
        userImage = (ImageView) findViewById(R.id.iveditPortrait);
        gender = (TextView) findViewById(R.id.editGender);
        birthday = (TextView) findViewById(R.id.editBirthday);
        location = (TextView) findViewById(R.id.editResideLocation);
        etAffectivestatus = (EditText) findViewById(R.id.editAffectivestatus);
        etLookingfor = (EditText) findViewById(R.id.editLookingfor);
        etIntro = (EditText) findViewById(R.id.editIntro);
        etInterest = (EditText) findViewById(R.id.editInterest);

        zodiac = (TextView) findViewById(R.id.editZodiac);
        constellation = (TextView) findViewById(R.id.editConstellation);
        changeImageLayout = (LinearLayout) findViewById(R.id.editPortrait);
        back = (Button) findViewById(R.id.button_back);
        save = (Button) findViewById(R.id.editinfo_save_btn);
        occupation = (TextView) findViewById(R.id.tvOccupation);
        education = (TextView) findViewById(R.id.tvEducation);
        school = (TextView) findViewById(R.id.editSchool);
    }

    private void setText() {
        // TODO Auto-generated method stub
        if (editUserInfo.getEdGender() != null) {
            if (editUserInfo.getEdGender().equals("1")) {
                gender.setText(getResources().getStringArray(R.array.gender)[0]);
            } else if (editUserInfo.getEdGender().equals("2")) {
                gender.setText(getResources().getStringArray(R.array.gender)[1]);
            }
        } else {
            gender.setText(getResources().getStringArray(R.array.gender)[0]);
        }
        birthday.setText(editUserInfo.getBirthday());
        zodiac.setText(editUserInfo.getEdZodiac());
        constellation.setText(editUserInfo.getEdConstellation());
        location.setText(editUserInfo.getEdResideCity());
        etAffectivestatus.setText(editUserInfo.getEdAffectivestatus());
        etLookingfor.setText(editUserInfo.getEdLookingfor());
        etIntro.setText(editUserInfo.getEdIntro());
        etInterest.setText(editUserInfo.getEdInterest());
        occupation.setText(editUserInfo.getEdOccupation());
        education.setText(editUserInfo.getEdEducation());
        school.setText(editUserInfo.getUniversity());
        GitHubImageLoader.Instace(mContext).setCirclePic(
                AccountManagerLib.Instace(mContext).userId, userImage);

    }

    private void LoadInfo() {
        // TODO Auto-generated method stub
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    waitingDialog.show();
//				GetAddr();
                    handler.sendEmptyMessage(1);
                    break;
                case 1:
                    ExeProtocol.exe(
                            new RequestUserDetailInfo(AccountManagerLib
                                    .Instace(mContext).userId),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    // TODO Auto-generated method stub
                                    ResponseUserDetailInfo responseUserDetailInfo = (ResponseUserDetailInfo) bhr;
                                    if (responseUserDetailInfo.result.equals("211")) {
                                        editUserInfo = responseUserDetailInfo.editUserInfo;
                                    }
                                    handler.sendEmptyMessage(2);
                                }

                                @Override
                                public void error() {
                                    // TODO Auto-generated method stub
                                    handler.sendEmptyMessage(2);
                                    handler.sendEmptyMessage(6);
                                }
                            });
                    break;
                case 2:
                    waitingDialog.dismiss();
                    setText();
                    break;
                case 3:
                    save.setClickable(true);
                    CustomToast.showToast(mContext, R.string.person_info_success);
                    break;
                case 4:
                    save.setClickable(true);
                    CustomToast.showToast(mContext, R.string.person_info_fail);
                    break;
                case 5:
                    setText();
                    break;
                case 6:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 7:
                    CustomToast.showToast(mContext, R.string.check_gps);
                    break;
                case 8:
                    schools = new SchoolOp(mContext).findDataByFuzzy(tempSchool
                            .toString());
                    schoolListAdapter = new SchoolListAdapter(mContext, schools);
                    schoolList.setAdapter(schoolListAdapter);
                    schoolListAdapter.notifyDataSetChanged();
                    break;
                case 9:
                    schools = new SchoolOp(mContext).findDataByFuzzy(tempSchool
                            .toString());
                    schoolListAdapter.setData(schools);
                    schoolListAdapter.notifyDataSetChanged();
                    break;
                case 10:
                    location.setText(cityName);
                    break;
                case 11:
                    waitingDialog.show();
                    String id = AccountManagerLib.Instace(mContext).userId;
                    ExeProtocol.exe(new ModifyUserNameRequest(id, userInfo.username, userName),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {
                                    ModifyUserNameResponse response = (ModifyUserNameResponse) bhr;
                                    Log.e("result", response.result);
                                    if (response.result.equals("121")) {
                                        handler.sendEmptyMessage(12);
                                    } else if (response.result.equals("000")) {
                                        handler.sendEmptyMessage(13);
                                    } else {
                                        handler.sendEmptyMessage(14);
                                    }
                                }

                                @Override
                                public void error() {
                                    // TODO Auto-generated method stub
                                }
                            });
                    break;
                case 12:
                    Toast.makeText(mContext, "修改成功！", Toast.LENGTH_SHORT).show();
                    AccountManagerLib.Instace(mContext).userName = userName;
                    UserInfo userInfo = AccountManagerLib.Instace(mContext).userInfo;
                    userInfo.username = userName;
                    AccountManagerLib.Instace(mContext).userInfo = userInfo;
                    ConfigManager.Instance().putString("userName", userName);
                    waitingDialog.dismiss();
                    break;
                case 13:
                    Toast.makeText(mContext, "该用户名已经被使用！", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                    break;
                case 14:
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void setListener() {
        // TODO Auto-generated method stub
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        changeImageLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, UpLoadImageActivity.class);
                startActivity(intent);
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createDialog(GENDER_DIALOG);
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createDialog(DATE_DIALOG);
            }
        });
        occupation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createDialog(OCCUPATION_DIALOG);
            }
        });
        education.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createDialog(EDUCATION_DIALOG);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                save.setClickable(false);
                String city = location.getText().toString();

                String editAffectivestatus = etAffectivestatus.getText().toString();
                String editLookingfor = etLookingfor.getText().toString();
                String editIntro = etIntro.getText().toString();
                String editInterest = etInterest.getText().toString();

                city = city.trim();
                String value = "", key = "";
                StringBuffer sb = new StringBuffer("");
                int i;
                if (city.contains(" ")) {
                    String[] area = city.split(" ");
                    sb.append(editUserInfo.getEdGender()).append(",");
                    sb.append(editUserInfo.getEdBirthYear()).append(",");
                    sb.append(editUserInfo.getEdBirthMonth()).append(",");
                    sb.append(editUserInfo.getEdBirthDay()).append(",");
                    sb.append(editUserInfo.getEdConstellation()).append(",");
                    sb.append(editUserInfo.getEdZodiac()).append(",");
                    sb.append(editAffectivestatus).append(",");
                    sb.append(editLookingfor).append(",");
                    sb.append(editIntro).append(",");
                    sb.append(editInterest).append(",");
                    sb.append(occupation.getText()).append(",");
                    sb.append(education.getText()).append(",");
                    sb.append(school.getText()).append(",");
                    for (i = 0; i < area.length; i++) {
                        sb.append(area[i]).append(",");
                    }
                    if (area.length == 2) {
                        sb.append(" ").append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    value = sb.toString();
//					if (i == 3) {
                    key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,affectivestatus,lookingfor,bio,interest,"
                            + "occupation,education,graduateschool,resideprovince,residecity,residedist";
//					} else {
//						key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,graduateschool,resideprovince,residecity";
//					}
                } else {
                    sb.append(editUserInfo.getEdGender()).append(",");
                    sb.append(editUserInfo.getEdBirthYear()).append(",");
                    sb.append(editUserInfo.getEdBirthMonth()).append(",");
                    sb.append(editUserInfo.getEdBirthDay()).append(",");
                    sb.append(editUserInfo.getEdConstellation()).append(",");
                    sb.append(editUserInfo.getEdZodiac()).append(",");
                    sb.append(editAffectivestatus).append(",");
                    sb.append(editLookingfor).append(",");
                    sb.append(editIntro).append(",");
                    sb.append(editInterest).append(",");
                    sb.append(occupation.getText()).append(",");
                    sb.append(education.getText()).append(",");
                    sb.append(school.getText()).append(",");
                    sb.append(city);
                    value = sb.toString();
                    key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,affectivestatus,lookingfor,bio,interest,"
                            + "occupation,education,graduateschool,residecity";
                }
                ExeProtocol.exe(
                        new RequestEditUserInfo(AccountManagerLib
                                .Instace(mContext).userId, key, value),
                        new ProtocolResponse() {

                            @Override
                            public void finish(BaseHttpResponse bhr) {
                                // TODO Auto-generated method stub
                                ResponseEditUserInfo responseEditUserInfo = (ResponseEditUserInfo) bhr;
                                if (responseEditUserInfo.result.equals("221")) {
                                    handler.sendEmptyMessage(3);
                                } else {
                                    handler.sendEmptyMessage(4);
                                }
                            }

                            @Override
                            public void error() {
                                // TODO Auto-generated method stub
                                handler.sendEmptyMessage(6);
                            }
                        });
                userName = editName.getText().toString().trim();
                if (userInfo != null && !userName.equals(userInfo.username)) {
                    if (userName.length() < 4) {
                        Toast.makeText(mContext, "您的昵称太短啦", Toast.LENGTH_SHORT).show();
                    } else {
                        handler.sendEmptyMessage(11);
                    }
                }

            }
        });
        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                createDialog(LOCATION_DIALOG);
            }
        });
        school.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                createDialog(SCHOOL_DIALOG);
            }
        });
    }

    private void initSchoolDialog(final Dialog dialog) {
        searchText = (EditText) schoolDialog.findViewById(R.id.search_text);
        sure = (Button) schoolDialog.findViewById(R.id.search);
        clear = schoolDialog.findViewById(R.id.clear);
        schoolList = (ListView) schoolDialog.findViewById(R.id.school_list);
        searchText.requestFocus();
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                tempSchool = new StringBuffer("");
                int size = arg0.length();
                for (int i = 0; i < size; i++) {
                    tempSchool.append(arg0.charAt(i));
                    tempSchool.append('%');
                }
                handler.sendEmptyMessage(9);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                searchText.setText("");
                schools.clear();
                tempSchool = new StringBuffer("");
                handler.sendEmptyMessage(8);
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                school.setText(searchText.getText().toString());
                dialog.dismiss();
            }
        });
        schoolList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                school.setText(schools.get(arg2).school_name);
                dialog.dismiss();
            }
        });
        tempSchool = new StringBuffer("");
        handler.sendEmptyMessage(8);
    }

    private void initLocationDialog(final Dialog dialog) {
        initJsonData();

        mProvince = (WheelView) locationDialog.findViewById(R.id.id_province);
        mCity = (WheelView) locationDialog.findViewById(R.id.id_city);
        mArea = (WheelView) locationDialog.findViewById(R.id.id_area);
        btnShowChoose = (Button) locationDialog.findViewById(R.id.btn_showchoose);

        initDatas();

        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        // 添加change事件
        mProvince.addChangingListener(this);
        // 添加change事件
        mCity.addChangingListener(this);
        // 添加change事件
        mArea.addChangingListener(this);

        btnShowChoose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//				Toast.makeText(mContext, mCurrentProviceName + mCurrentCityName + mCurrentAreaName, 1).show();

                location.setText(mCurrentProviceName + " " + mCurrentCityName + " " + mCurrentAreaName);
                dialog.dismiss();
            }
        });

        mProvince.setVisibleItems(5);
        mCity.setVisibleItems(5);
        mArea.setVisibleItems(5);
        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mAreaDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
            mCurrentAreaName = "";
        } else {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[0];
        }
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mArea.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mCity.setCurrentItem(0);
        updateAreas();
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

                mProvinceDatas[i] = province;

                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetAddr() {
        String locationPos[] = GetLocation.getInstance(mContext).getLocation();
        String latitude = locationPos[0];
        String longitude = locationPos[1];
        if (latitude.equals("0.0") && longitude.equals("0.0")) {
            handler.sendEmptyMessage(7);
        }
        ExeProtocol.exe(new LocationRequest(latitude, longitude),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        // TODO Auto-generated method stub

                        LocationResponse lcr = (LocationResponse) bhr;
                        StringBuffer builder = new StringBuffer();
                        builder.append(lcr.province).append(" ");
                        builder.append(lcr.locality).append(" ");
                        builder.append(lcr.subLocality);
                        cityName = builder.toString();
                        handler.sendEmptyMessage(10);
                    }

                    @Override
                    public void error() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void createDialog(int id) {
        Dialog dialog = null;
        Builder builder = new AlertDialog.Builder(mContext);
        switch (id) {
            case GENDER_DIALOG:
                builder.setTitle(R.string.person_info_gender);
                builder.setSingleChoiceItems(R.array.gender, 0,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stud
                                if (which == 1) {
                                    editUserInfo.setEdGender("2");
                                } else if (which == 0) {
                                    editUserInfo.setEdGender("1");
                                }
                                handler.sendEmptyMessage(5);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                dialog = builder.create();
                break;
            case OCCUPATION_DIALOG:
                builder.setTitle(R.string.person_info_occupation);
                builder.setSingleChoiceItems(R.array.occupation, 0,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stud
                                if (which == 0) {
                                    editUserInfo.setEdOccupation("学生");
                                } else if (which == 1) {
                                    editUserInfo.setEdOccupation("上班族");
                                }
                                handler.sendEmptyMessage(5);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                dialog = builder.create();
                break;
            case EDUCATION_DIALOG:
                builder.setTitle(R.string.person_info_education);
                builder.setSingleChoiceItems(R.array.education, 0,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stud
                                if (which == 0) {
                                    editUserInfo.setEdEducation("小学");
                                } else if (which == 1) {
                                    editUserInfo.setEdEducation("初中");
                                } else if (which == 2) {
                                    editUserInfo.setEdEducation("高中");
                                } else if (which == 3) {
                                    editUserInfo.setEdEducation("中等专业学校");
                                } else if (which == 4) {
                                    editUserInfo.setEdEducation("大学专科");
                                } else if (which == 5) {
                                    editUserInfo.setEdEducation("本科");
                                } else if (which == 6) {
                                    editUserInfo.setEdEducation("硕士");
                                } else if (which == 7) {
                                    editUserInfo.setEdEducation("博士");
                                }
                                handler.sendEmptyMessage(5);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                dialog = builder.create();
                break;
            case DATE_DIALOG:
                calendar = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                editUserInfo.setEdBirthDay(dayOfMonth);
                                editUserInfo.setEdBirthYear(year);
                                editUserInfo.setEdBirthMonth(month + 1);
                                Calendar cal = new GregorianCalendar(year, month,
                                        dayOfMonth);
                                String constellation = JudgeZodicaAndConstellation
                                        .date2Constellation(cal);
                                String zodiac = JudgeZodicaAndConstellation
                                        .date2Zodica(cal);
                                editUserInfo.setEdZodiac(zodiac);
                                editUserInfo.setEdConstellation(constellation);
                                editUserInfo.setBirthday(year + "-" + (month + 1)
                                        + "-" + dayOfMonth);
                                handler.sendEmptyMessage(5);
                            }
                        }, calendar.get(Calendar.YEAR), // 传入年份
                        calendar.get(Calendar.MONTH), // 传入月份
                        calendar.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                dialog.setTitle(R.string.person_info_birth);
                break;
            case SCHOOL_DIALOG:
                builder.setTitle(R.string.person_info_school);
                LayoutInflater vi = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                schoolDialog = vi.inflate(R.layout.school_dialog, null);
                builder.setView(schoolDialog);
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                dialog = builder.create();
                dialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {
                        // TODO Auto-generated method stub
                        schoolListAdapter = null;
                    }
                });
                initSchoolDialog(dialog);
                break;
            case LOCATION_DIALOG:
                builder.setTitle(R.string.person_info_local_now);
                LayoutInflater vInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                locationDialog = vInflater.inflate(R.layout.location_dialog, null);
                builder.setView(locationDialog);
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                dialog = builder.create();
                dialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {
                        // TODO Auto-generated method stub


                    }
                });
                initLocationDialog(dialog);
                break;
            default:
                break;
        }
        dialog.show();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mProvince) {
            updateCities();
        } else if (wheel == mCity) {
            updateAreas();
        } else if (wheel == mArea) {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
        }
    }

}
