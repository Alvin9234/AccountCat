package com.alvin.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private int week;// 在当月是第几周
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
    // --------按条件查看 按钮控件
    private Button btn_year;
    private Button btn_month;
    private Button btn_week;
    private Button btn_day;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.WEEK_OF_MONTH);
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
        setBtnClick();
        return view;
    }

    /**
     *  求 数据 总和
     * @param data
     * @return
     */
    private float getSum(float[] data){
        float sum=0;
        for (int i = 0; i < data.length; i++) {
            sum+=data[i];
        }
        return sum;
    }

    /**
     *  设置 饼图 图例
     * @param data
     */
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

    /**
     *  设置 饼图 数据
     * @param list
     * @return
     */
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

    /**
     *    选择 日期
     */
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
                        year = y;
                        monthOfYear = m;
                        dayOfMonth = d;

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

    /**
     *  调用 画饼图 的方法
     * @param data
     */
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

    /**
     *   刷新 列表
     * @param list
     */
    private void reloadListView( List<Map<String, Object>> list) {
        totalList.clear();
        totalList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    /**
     *   控件的  findViewById
     * @param view
     */
    private void findView(View view) {
        charView = (DetailArcChartView) view.findViewById(R.id.charView);
        listView_inOrOutLog = (ListView) view.findViewById(R.id.listView_inOrOutLog);
        emptyInfo = (TextView) view.findViewById(R.id.emptyInfo);

        btn_year = (Button) view.findViewById(R.id.btn_year);
        btn_month = (Button) view.findViewById(R.id.btn_month);
        btn_week = (Button) view.findViewById(R.id.btn_week);
        btn_day = (Button) view.findViewById(R.id.btn_day);

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

    /**
     *   按钮 事件处理
     */
    private void setBtnClick(){
        //  按年查看
        btn_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "select * from tb_count where year = ? and article = ?";
                List<Map<String, Object>> list = helper.selectList(str,
                        new String[]{String.valueOf(year),
                                arrTabTitles[tabIndex]});
                reloadListView(list);
                // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                float[] data = setChartData(list);
                drawChart(data); // 画图
                setRatioText(data);// 设置 比例示意文本
            }
        });
        // 按月查看
        btn_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "select * from tb_count where month = ? and year = ? and article = ?";
                List<Map<String, Object>> list = helper.selectList(str,
                        new String[]{ String.valueOf(monthOfYear+1),
                                String.valueOf(year),
                                arrTabTitles[tabIndex]});
                reloadListView(list);
                // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                float[] data = setChartData(list);
                drawChart(data); // 画图
                setRatioText(data);// 设置 比例示意文本
            }
        });
        //  按周 查看
        btn_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------  先根据当前选择的 天，查询数据库，获得相对应的 周 是多少，然后在根据 周 查询数据库，按周查看
                String str_day = "select * from tb_count where day = ? and month = ? and year = ? and article = ?";
                List<Map<String, Object>> list_day = helper.selectList(str_day,
                        new String[]{String.valueOf(dayOfMonth),
                                String.valueOf(monthOfYear+1),
                                String.valueOf(year),
                                arrTabTitles[tabIndex]});
                Log.i("list_day","list_day---->>>>"+list_day);
                week = Integer.parseInt(list_day.get(0).get("week").toString());

                String str_week = "select * from tb_count where week = ? and month = ? and year = ? and article = ?";
                List<Map<String, Object>> list_week = helper.selectList(str_week,
                        new String[]{String.valueOf(week),
                                String.valueOf(monthOfYear+1),
                                String.valueOf(year),
                                arrTabTitles[tabIndex]});
                Log.i("list_week","list_week---->>>>"+list_week);

                reloadListView(list_week);
                // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                float[] data = setChartData(list_week);
                drawChart(data); // 画图
                setRatioText(data);// 设置 比例示意文本
            }
        });
        //   按天查看
        btn_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_day = "select * from tb_count where day = ? and month = ? and year = ? and article = ?";
                List<Map<String, Object>> list_day = helper.selectList(str_day,
                        new String[]{String.valueOf(dayOfMonth),
                                String.valueOf(monthOfYear+1),
                                String.valueOf(year),
                                arrTabTitles[tabIndex]});
                reloadListView(list_day);
                // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                float[] data = setChartData(list_day);
                drawChart(data); // 画图
                setRatioText(data);// 设置 比例示意文本
            }
        });
    }
}