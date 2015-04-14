package com.alvin.AccountCat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.alvin.adapter.ListViewAdapter;
import com.alvin.helper.MySQLiteOpenHelper;
import com.zxing.activity.CaptureActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    //-----------  控件 -----------
    private ListView listView_all;//显示所有 记账记录 的控件
    private TextView textView_date;//显示日期
    private TextView textView_emptyInfo;// listview 暂无记录的显示文本
    private EditText editText_money;// 输入 money
    private EditText editText_remark;// 备注
    private RadioButton radioButton_income;// 收入 选择项
    private RadioButton radioButton_expenditure;//  支出 选择项
    private Button btn_articles;// 类型选择按钮
    //-----------  控件 -----------
    //成员变量
    private MySQLiteOpenHelper helper;// 数据库处理 对象
    private ListViewAdapter adapter; // listview 的适配器
    private List<Map<String, Object>> totalList;// listview中数据集合
    private Calendar calendar;
    private int year; // 获取选择日期时的 年
    private int monthOfYear;// 月
    private int dayOfMonth;// 日
    private int week;// 星期几
    private String dateString;// 拼接日期的字符串
    private String sqlInsert = "";
    private String type = "";// 选择的 类型 字符串文本
    private boolean isOK = false;// 数据库语句执行结果  判断标记
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
        registerForContextMenu(listView_all);// 为 ListView 注册上下文菜单
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
                        // 根据年月日得到新的Calendar对象，重新获得对应的week
                        Calendar c = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        week = c.get(Calendar.WEEK_OF_MONTH);
                        textView_date.setText(dateString);
                    }
                }, year, monthOfYear, dayOfMonth);
                dateDialog.show();
                break;
            case R.id.btn_articles:
                final String[] articles = getResources().getStringArray(R.array.articles);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择类别");
                // 第二个参数 默认从0开始即第一个被选中 ，，-1 代表一个都不选
                builder.setSingleChoiceItems(articles, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                btnSetText(articles[0]);
                                break;
                            case 1:
                                btnSetText(articles[1]);
                                break;
                            case 2:
                                btnSetText(articles[2]);
                                break;
                            case 3:
                                btnSetText(articles[3]);
                                break;
                            case 4:
                                btnSetText(articles[4]);
                                break;
                            case 5:
                                btnSetText(articles[5]);
                                break;
                            case 6:
                                btnSetText(articles[6]);
                                break;
                            case 7:
                                btnSetText(articles[7]);
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
     *   设置选择类型后的 按钮文本
     * @param typeStr
     */
    public void btnSetText(String typeStr){
        btn_articles.setText(typeStr);
        type = typeStr;
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
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_detail:
                intent.setClass(this, DetailFragmentActivity.class);
                startActivity(intent);
                break;
            case R.id.action_saoYiSao:
                intent.setClass(this, CaptureActivity.class);
                startActivityForResult(intent, 2000);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.icon);
            builder.setTitle("扫描结果：");
            builder.setMessage(scanResult);
            builder.setPositiveButton("确定",null);
            builder.show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderIcon(R.drawable.icon);
        menu.setHeaderTitle("确定要删除这一条数据吗？");
        getMenuInflater().inflate(R.menu.contextmenu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final int position = info.position;
        final String id = totalList.get(position).get("_id").toString();
        switch (item.getItemId()){
            case R.id.action_context_delete:
                AlertDialog.Builder builder =new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.icon);
                builder.setTitle("确定要删除吗？");
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String delete = "delete from tb_count where _id = ?";
                        boolean flag = helper.execData(delete,new Object[]{id});
                        if(flag){
                            Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            List<Map<String, Object>> list = helper.selectList(query,null);
                            reloadListView(list);
                        }else{
                            Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                break;
            case R.id.action_context_doNothing:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
