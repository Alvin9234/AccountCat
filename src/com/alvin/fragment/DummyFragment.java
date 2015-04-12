package com.alvin.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.alvin.AccountCat.R;
import com.alvin.adapter.ListViewAdapter;
import com.alvin.helper.MySQLiteOpenHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 2015/4/10.
 */
public class DummyFragment extends Fragment {
    private MySQLiteOpenHelper helper;
    private ListView listView_inOrOutLog;
    //private Button btn_year;

    private DetailArcChartView charView;
    private TextView emptyInfo;
    private ListViewAdapter adapter;
    private List<Map<String, Object>> totalList;// 数据库查询结果
    private int tabIndex;//  保存导航标签的下标，默认从0开始
    private String query = "";

    private Calendar calendar;
    private int year; // 获取选择日期时的 年
    private int monthOfYear;// 月
    private int dayOfMonth;// 日
//    private int week;// 星期几
    private String dateString="";// 拼接日期的字符串
    private String[] arrTabTitles = null;
    private TextView txt_date;//日期
    // -------   比例，文本控件
    private TextView ratio_salary;//工资
    private TextView ratio_windfall;//外快
    private TextView ratio_entertainment;//娱乐
    private TextView ratio_repast;//餐饮
    private TextView ratio_rent;//房租
    private TextView ratio_traffic;//交通
    private TextView ratio_shopping;//购物
    private TextView ratio_others;//其他
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //week = calendar.get(Calendar.WEEK_OF_MONTH);
        dateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

        Bundle bundle = getArguments();
        tabIndex = bundle.getInt("tabIndex");
        helper = new MySQLiteOpenHelper(getActivity());
        totalList = new ArrayList<Map<String, Object>>();
        arrTabTitles = new String[]{"收入","支出"};
        switch (tabIndex){
            case 0:
                query = "select * from tb_count where article = ?";
                totalList = helper.selectList(query,new String[]{arrTabTitles[0]});
                break;
            case 1:
                query = "select * from tb_count where article = ?";
                totalList = helper.selectList(query,new String[]{arrTabTitles[1]});
                break;
        }
        adapter = new ListViewAdapter(totalList,getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
        findView(view);
        listView_inOrOutLog.setAdapter(adapter);
        listView_inOrOutLog.setEmptyView(emptyInfo);
        txt_date.setText(dateString);
        chooseDate();// 选择日期
        float[] totalData = setChartData(totalList);
        drawChart(totalData); // 画图
        setRatioText(totalData);// 设置 比例示意文本
        return view;
    }
    private float getSum(float[] data){
        float sum=0;
        for (int i = 0; i < data.length; i++) {
            sum+=data[i];
        }
        return sum;
    }
    private void setRatioText(float[] data) {
        float sum = getSum(data);
        switch (tabIndex){
            case 0:
                ratio_salary.setText("工资:"+new DecimalFormat("#.00").format((data[0]/sum)*100)+"%");// 保留两位小数
                ratio_windfall.setText("外快:"+new DecimalFormat("#.00").format((data[1]/sum)*100)+"%");
                //sum=0;
                break;
            case 1:
                ratio_entertainment.setText("娱乐:"+new DecimalFormat("#.00").format((data[0]/sum)*100)+"%");
                ratio_repast.setText("餐饮:"+new DecimalFormat("#.00").format((data[1]/sum)*100)+"%");
                ratio_rent.setText("房租:"+new DecimalFormat("#.00").format((data[2]/sum)*100)+"%");
                ratio_traffic.setText("交通:"+new DecimalFormat("#.00").format((data[3]/sum)*100)+"%");
                ratio_shopping.setText("购物:"+new DecimalFormat("#.00").format((data[4]/sum)*100)+"%");
                ratio_others.setText("其他:"+new DecimalFormat("#.00").format((data[5]/sum)*100)+"%");
                break;
        }
    }

    private float[] setChartData(List<Map<String, Object>> list) {
        float[] totalData = null;
        switch (tabIndex){
            case 0://  设置只有 收入 的饼图数据
                float salary = 0;
                float sum = 0;
                for (Map<String, Object> map : list) {
                    String money = map.get("money").toString();
                    String type = map.get("type").toString();
                    if(type.equals("工资")){
                        salary += Float.parseFloat(money);
                    }
                    sum += Float.parseFloat(money);
                }
                totalData = new float[]{salary,sum-salary};
                break;
            case 1:// 设置只有 支出 的饼图数据
                float entertainment=0;
                float repast=0;
                float rent=0;
                float traffic=0;
                float shopping=0;
                float others=0;
                for (Map<String, Object> map : list) {
                    String money = map.get("money").toString();
                    String type = map.get("type").toString();
                    if(type.equals("娱乐")){
                        entertainment += Float.parseFloat(money);
                    }else if(type.equals("餐饮")){
                        repast += Float.parseFloat(money);
                    }
                    else if(type.equals("房租")){
                        rent += Float.parseFloat(money);
                    }
                    else if(type.equals("交通")){
                        traffic += Float.parseFloat(money);
                    }else if(type.equals("购物")){
                        shopping += Float.parseFloat(money);
                    }else if(type.equals("其他")){
                        others += Float.parseFloat(money);
                    }
                }
                totalData = new float[]{entertainment,repast,rent,traffic,shopping,others};
                break;
        }
        return totalData;
    }

    private void chooseDate() {
        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m,
                                          int d) {
                        dateString = y + "-" + (m + 1) + "-" + d;
                        txt_date.setText(dateString);
                        String str = "select * from tb_count where day = ? and month = ? and year = ? and article = ?";
                        List<Map<String, Object>> list = helper.selectList(str,
                                                new String[]{String.valueOf(d),
                                                              String.valueOf(m+1),
                                                             String.valueOf(y),
                                                            arrTabTitles[tabIndex]});
                        reloadListView(list);
                        // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                        float[] data = setChartData(list);
                        drawChart(data); // 画图
                        setRatioText(data);// 设置 比例示意文本
                    }
                }, year, monthOfYear, dayOfMonth);
                dateDialog.show();
            }
        });
    }

    private void drawChart(float[] data) {
        switch (tabIndex){
            case 0:
                charView.setData(data,tabIndex);
                break;
            case 1:
                charView.setData(data,tabIndex);
                break;
        }
    }

    private void reloadListView( List<Map<String, Object>> list) {
        totalList.clear();
        totalList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void findView(View view) {
        charView = (DetailArcChartView) view.findViewById(R.id.charView);
        listView_inOrOutLog = (ListView) view.findViewById(R.id.listView_inOrOutLog);
        //btn_year = (Button) view.findViewById(R.id.btn_year);
        emptyInfo = (TextView) view.findViewById(R.id.emptyInfo);

        txt_date = (TextView) view.findViewById(R.id.txt_date);
        ratio_salary = (TextView) view.findViewById(R.id.ratio_salary);
        ratio_windfall = (TextView) view.findViewById(R.id.ratio_windfall);
        ratio_entertainment = (TextView) view.findViewById(R.id.ratio_entertainment);
        ratio_repast = (TextView) view.findViewById(R.id.ratio_repast);
        ratio_rent = (TextView) view.findViewById(R.id.ratio_rent);
        ratio_traffic = (TextView) view.findViewById(R.id.ratio_traffic);
        ratio_shopping = (TextView) view.findViewById(R.id.ratio_shopping);
        ratio_others = (TextView) view.findViewById(R.id.ratio_others);
    }
}