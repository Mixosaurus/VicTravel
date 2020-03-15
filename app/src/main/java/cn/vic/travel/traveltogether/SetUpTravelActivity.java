package cn.vic.travel.traveltogether;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.vic.travel.R;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.Utils.getTimeInString;

/**
 * Snake 创建于 2018/8/10.
 * 发起旅行页面
 */

public class SetUpTravelActivity extends AppCompatActivity {
    private EditText etDeparture;           //出发地
    private EditText etDestination;         //目的地
    private TextView etStartDate;           //开始时间
    private TextView etEndDate;             //结束时间
    private EditText etPeopleNumber;        //人数
    private EditText etRemarkers;           //备注
    private Button btnConfirm;              //确认按钮
    private Date startDate =new Date();     //开始日期
    private Date endDate=new Date();        //结束日期
    private EditText etTitle;//标题
    final Calendar calendar=Calendar.getInstance();
    private final int TYPE_START_TIME = 0;      //开始时间类型ID
    private final int TYPE_END_TIME=1;          //结束时间类型ID
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_travel);
        etDeparture=(EditText) findViewById(R.id.et_departure);
        etDestination=(EditText) findViewById(R.id.et_destination);
        etStartDate =(TextView) findViewById(R.id.tv_start_date);
        etEndDate =(TextView) findViewById(R.id.tv_end_date);
        etPeopleNumber=(EditText) findViewById(R.id.et_people_number);
        etRemarkers=(EditText) findViewById(R.id.et_remarks);
        btnConfirm=(Button)findViewById(R.id.btn_confirm);
        etTitle=(EditText )findViewById ( R.id.et_title );
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(SetUpTravelActivity.this, 0, etStartDate,calendar,TYPE_START_TIME);
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(SetUpTravelActivity.this, 0, etEndDate,calendar,TYPE_END_TIME);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupTravel();      //发起旅行
            }
        });

        //addInfo();

    }


    void setupTravel(){
        if(isDataLegal()){
            String title=etTitle.getText ().toString ();
            String departure=etDeparture.getText().toString();
            String destination=etDestination.getText().toString();
            int maxPeopleOfNumber= Integer.parseInt(etPeopleNumber.getText().toString());
            String remarkers=etRemarkers.getText().toString();
            NetUser netUser= BmobUser.getCurrentUser ( NetUser.class );
            String username=netUser.getUsername ();
            TravelInfo travelInfo=new TravelInfo (title, departure, destination, new BmobDate ( startDate ), new BmobDate ( endDate), 0,maxPeopleOfNumber, remarkers, username, netUser);
            //travelInfo.setNumberOfPeople(0);
            int startTime=Integer.parseInt (getTimeInString(new BmobDate ( startDate ),false) );
            int endTime=Integer.parseInt ( getTimeInString( new BmobDate ( endDate ) ,false) );

            if(startTime<=endTime){
                checkRepeatedInfo(travelInfo,title);
            }
            else if(startTime>endTime){
                Toast.makeText (VicApplication.getInstance(),"您的时间设置不正确",Toast.LENGTH_LONG ).show ();debugToast ( "" );
            }
        }
        else {
            Toast.makeText (VicApplication.getInstance(),"请输入完整信息",Toast.LENGTH_LONG ).show ();
        }
    }

    void addInfo(){
        for(int i=1;i<30;i++){
            NetUser netUser= BmobUser.getCurrentUser ( NetUser.class );
            int maxPeopleNumber=10;
            Date start=new Date();
            Date end=new Date();
            start.setYear(2018);
            start.setMonth(9);
            start.setDate(23);
            end.setYear(2018);
            end.setMonth(10);
            end.setDate(25);
            TravelInfo travelInfo=new TravelInfo ("第"+String.valueOf(i)+"条数据",
                    "青岛",
                    "上海",
                    new BmobDate ( start ),
                    new BmobDate ( end ),
                    0,
                    maxPeopleNumber,
                    "\t\t我认为最完美的旅行，第一是和自己的知己或者爱人前往，第二是自己独自上路。自己独自上路，" +
                    "你可以完全遵照自己的意愿，天南海北地四处游玩，只是相对于第一种稍显落寞，但人这一生，能" +
                    "遇到投缘的知己谈何容易，但又不能亏待自己，时间不等人，日子不等人，如果那个知己永远不出" +
                    "现，是否要永远呆在原处呢？对于我来说，我不愿意，所以我会选择后者。\n" +
                    "\t\t旅行不是盲目跟风，旅行不是炫耀财富，旅行是一种生活，是一次挖掘自身的过程。人也许并" +
                    "不能完全认识自己，在一次次旅行的过程中，我们有机会好好思考，在面对壮丽的美景时，我们的" +
                    "心胸会变得无比开阔，当我们了解到自身的渺小时，当我们知道生命的短暂时，那个尘封已久的真" +
                    "实，就会渐渐浮出水面。人的心需要刺激，人的心需要激活，当你重复着每天单调的日子时，你的" +
                    "心会变得迟钝，你会很难发现美，很难发现好，当你苦闷时，犒赏自己一场旅行吧，让沉睡的心" +
                    "灵，重新起飞，在一次次激活心灵的过程中，你就会渐渐领悟，生命那斑斓的颜色。",
                    "snake",
                    netUser);
            uploadData(travelInfo);
        }
    }

    /**
     * 查询是否有重复信息
     * @param title 标题
     */
    public void checkRepeatedInfo(final TravelInfo travelInfo,final String title) {
        BmobQuery<TravelInfo> travelInfoBmobQuery = new BmobQuery<TravelInfo> ();
        travelInfoBmobQuery.addWhereEqualTo ( "title", title );
        travelInfoBmobQuery.findObjects ( new FindListener<TravelInfo>() {
            @Override
            public void done(List<TravelInfo> list, BmobException e) {
                if(e==null){
                    if(list.size()!=0){
                        TravelInfo travel=list.get(0);
                        String startTime=getTimeInString ( travel.getStartTime (),false );
                        String StartTime=getTimeInString ( new BmobDate ( startDate ),false);
                        String endTime=getTimeInString ( travel.getEndTime (),false );
                        String EndTime=getTimeInString ( new BmobDate ( endDate ),false );
                        if((startTime.compareTo (StartTime)==0)&&(endTime.compareTo ( EndTime)==0)) {
                            debugToast ( "您已发布过相同信息" );
                        }
                    }
                    else {
                        uploadData ( travelInfo );      //上传信息
                    }
                }
                else{
                    debugToast(e.toString());
                }
            }
        } );
    }


    void uploadData(TravelInfo travelInfo){
        travelInfo.save ( new SaveListener<String> () {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    debugToast ( "旅行发起成功！" );
                    VicApplication.shutActivity( SetUpTravelActivity.this );
                }
                else{
                    debugToast ( "旅行发起失败！" +e.toString ());
                }
            }
        } );
    }

    /**
     * 显示日期选择框
     * @param activity 要显示日期选择框的Acyivity
     * @param themeResId 主题ID
     * @param textView TextView 显示已选择好的日期的TextView
     * @param calendar calendar
     */
    public void showDatePickerDialog(Activity activity, int themeResId, final TextView textView, Calendar calendar,final int dateType) {
        new DatePickerDialog(activity , themeResId,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,  int monthOfYear, int dayOfMonth) {
                textView.setText(year + "年" + (monthOfYear+1)+ "月" + dayOfMonth + "日");
                switch (dateType) {
                    case 0:
                        startDate.setYear(year);
                        startDate.setMonth(monthOfYear+1);
                        startDate.setDate(dayOfMonth);
                        break;
                    case 1:
                        endDate.setYear(year);
                        endDate.setMonth(monthOfYear+1);
                        endDate.setDate(dayOfMonth);
                        break;
                }
            }
        }       // 设置初始日期
                ,calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 判断输入合法性
     */
    private boolean isDataLegal(){
        if( !TextUtils.isEmpty(etTitle.getText ().toString ())
                && !TextUtils.isEmpty( etDeparture.getText().toString() )
                && !TextUtils.isEmpty( etDestination.getText().toString() )
                && !TextUtils.isEmpty( startDate.toString())
                && !TextUtils.isEmpty( endDate.toString() )
                && !TextUtils.isEmpty( etPeopleNumber.getText().toString() )
                && !TextUtils.isEmpty( etRemarkers.getText().toString() )
                ){
            return true;
        }
        else {
            return false;
        }
    }



}
