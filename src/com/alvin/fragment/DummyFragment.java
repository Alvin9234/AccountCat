package com.alvin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import com.alvin.AccountCat.R;
import com.alvin.adapter.ListViewAdapter;
import com.alvin.helper.MySQLiteOpenHelper;

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
    private List<Map<String, Object>> list;// 数据库查询结果
    private int tabIndex;
    private String query = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tabIndex = bundle.getInt("tabIndex");
        helper = new MySQLiteOpenHelper(getActivity());
        switch (tabIndex){
            case 0:
                query = "select * from tb_count where article = ?";
                list = helper.selectList(query,new String[]{"收入"});
                break;
            case 1:
                query = "select * from tb_count where article = ?";
                list = helper.selectList(query,new String[]{"支出"});
                break;
        }
        adapter = new ListViewAdapter(list,getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
        charView = (DetailArcChartView) view.findViewById(R.id.charView);
        listView_inOrOutLog = (ListView) view.findViewById(R.id.listView_inOrOutLog);
        emptyInfo = (TextView) view.findViewById(R.id.emptyInfo);

        listView_inOrOutLog.setAdapter(adapter);
        listView_inOrOutLog.setEmptyView(emptyInfo);
        adapter.notifyDataSetChanged();
        switch (tabIndex){
            case 0:
                float[] data = new float[]{12000,1000};
                charView.setData(data,tabIndex);
                break;
            case 1:
                float[] data1 = new float[]{100,200,300,400};
                charView.setData(data1,tabIndex);
                break;
        }
        return view;
    }
    private void clickBtn(View view){
        switch (view.getId()){
            case R.id.btn_year:
                break;
            case R.id.btn_month:
                break;
            case R.id.btn_week:
                break;
            case R.id.btn_day:
                break;
            case R.id.btn_check:
                break;
        }
    }
}