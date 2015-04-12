package com.alvin.AccountCat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.alvin.adapter.ListViewAdapter;
import com.alvin.helper.MySQLiteOpenHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    //-----------  控件 -----------
    private ListView listView_all;
    private TextView textView_date;
    private TextView textView_emptyInfo;
    private EditText editText_money;
    private EditText editText_remark;
    private RadioButton radioButton_income;
    private RadioButton radioButton_expenditure;
    private Button btn_articles;
    //-----------  控件 -----------
    //成员变量
    private MySQLiteOpenHelper helper;
    private ListViewAdapter adapter;
    private List<Map<String, Object>> totalList;
    private Calendar calendar;
    private int year; // 获取选择日期时的 年
    private int monthOfYear;// 月
    private int dayOfMonth;// 日
    private int week;// 星期几
    private String dateString;// 拼接日期的字符串
    private String sqlInsert = "";
    private String type = "";
    private boolean isOK = false;
    private final String query = "select * from tb_count";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }
    /**
     * 通用的初始化方法
     */
    private void init() {
        helper = new MySQLiteOpenHelper(this);
        sqlInsert = "insert into tb_count (type,money,remark,year,month,day,week,article) values(?,?,?,?,?,?,?,?)";
        findViewId();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.WEEK_OF_MONTH);
        dateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        textView_date.setText(dateString);

        totalList = helper.selectList(query, null);
        if (totalList != null) {
            adapter = new ListViewAdapter(totalList, this);
            listView_all.setAdapter(adapter);
        } else {
            listView_all.setEmptyView(textView_emptyInfo);
        }
    }
    /**
     *  View 控件的 findViewById
     */
    private void findViewId() {
        textView_date = (TextView) findViewById(R.id.textView_date);
        textView_emptyInfo = (TextView) findViewById(R.id.textView_emptyInfo);
        editText_money = (EditText) findViewById(R.id.editText_money);
        editText_remark = (EditText) findViewById(R.id.editText_remark);
        listView_all = (ListView) findViewById(R.id.listView_all);
        radioButton_income = (RadioButton) findViewById(R.id.radioButton_income);
        radioButton_expenditure = (RadioButton) findViewById(R.id.radioButton_expenditure);
        btn_articles = (Button) findViewById(R.id.btn_articles);
    }

    /**
     *   刷新 列表
     * @param list
     */
    public void reloadListView(List<Map<String, Object>> list){
        totalList.clear();
        totalList.addAll(list);
        adapter.notifyDataSetChanged();
    }
    /**
     * 按钮事件
     *
     * @param view
     */
    public void clickButton(View view) {
        switch (view.getId()) {
            case R.id.textView_date:
                DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m,
                                          int d) {
                        dateString = y + "-" + (m + 1) + "-" + d;
                        year = y;
                        monthOfYear = m;
                        dayOfMonth = d;
                        textView_date.setText(dateString);
                    }
                }, year, monthOfYear, dayOfMonth);
                dateDialog.show();
                break;
            case R.id.btn_articles:
                final String[] articles = getResources().getStringArray(R.array.articles);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择类别");
                builder.setSingleChoiceItems(articles, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                btn_articles.setText(articles[0]);
                                type = articles[0];
                                break;
                            case 1:
                                btn_articles.setText(articles[1]);
                                type = articles[1];
                                break;
                            case 2:
                                btn_articles.setText(articles[2]);
                                type = articles[2];
                                break;
                            case 3:
                                btn_articles.setText(articles[3]);
                                type = articles[3];
                                break;
                            case 4:
                                btn_articles.setText(articles[4]);
                                type = articles[4];
                                break;
                            case 5:
                                btn_articles.setText(articles[5]);
                                type = articles[5];
                                break;
                            case 6:
                                btn_articles.setText(articles[6]);
                                type = articles[6];
                                break;
                            case 7:
                                btn_articles.setText(articles[7]);
                                type = articles[7];
                                break;
                        }
                    }
                });
                builder.setPositiveButton("确认", null);
                builder.show();
                break;
            case R.id.btn_save:
                if (insertData(type)) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    List<Map<String, Object>> list = helper.selectList(query, null);
                    reloadListView(list);
                    clearEditText();
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                    clearEditText();
                }
                break;
        }
    }

    /**
     *   清空 输入框
     */
    public void clearEditText(){
        editText_money.setText("");
        editText_remark.setText("");
    }
    /**
     * 执行保存数据
     * @return
     */
    public boolean insertData(String type) {
        // 数据库操作
        String money = editText_money.getText().toString();
        String remark = editText_remark.getText().toString();
        String article = "";
        if(radioButton_income.isChecked()){
            article = "收入";
        }else if(radioButton_expenditure.isChecked()){
            article = "支出";
        }else{
            Toast.makeText(this, "请选则是收入还是支出", Toast.LENGTH_SHORT).show();
            return isOK;
        }
        isOK = helper.execData(sqlInsert, new Object[]{type, money, remark, year, monthOfYear + 1, dayOfMonth, week,article});
        return isOK;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail:
                Intent intent = new Intent();
                intent.setClass(this, DetailFragmentActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
