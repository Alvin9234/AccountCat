package com.alvin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alvin.AccountCat.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 2015/4/10.
 */
public class ListViewAdapter extends BaseAdapter {
    private List<Map<String, Object>> list=null;
    private Context context;
    public ListViewAdapter(List<Map<String, Object>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_listview, null);
            viewHolder.textView_inOrOut = (TextView) view.findViewById(R.id.textView_inOrOut);
            viewHolder.textView_money = (TextView) view.findViewById(R.id.textView_money);
            viewHolder.textView_article = (TextView) view.findViewById(R.id.textView_article);
            viewHolder.textView_remark = (TextView) view.findViewById(R.id.textView_remark);
            viewHolder.textView_date = (TextView) view.findViewById(R.id.textView_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
            viewHolder.textView_inOrOut.setText(list.get(i).get("article").toString()+":");

            viewHolder.textView_money.setText(list.get(i).get("money").toString());
            viewHolder.textView_article.setText(list.get(i).get("type").toString());
            if(list.get(i).get("remark")!=null){
                viewHolder.textView_remark.setText(list.get(i).get("remark").toString());
            }else {
                viewHolder.textView_remark.setText("无备注");
            }
            String date = list.get(i).get("year").toString()+"-"+list.get(i).get("month").toString()+"-"+list.get(i).get("day").toString();
            viewHolder.textView_date.setText(date);
        return view;
    }

    class ViewHolder {
        TextView textView_inOrOut;
        TextView textView_money;
        TextView textView_article;
        TextView textView_remark;
        TextView textView_date;
    }
}
