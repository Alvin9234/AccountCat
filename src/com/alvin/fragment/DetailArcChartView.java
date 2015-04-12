package com.alvin.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Alvin on 2015/4/9.
 */
public class DetailArcChartView extends View{
    private float[] data;
    public DetailArcChartView(Context context) {
        this(context, null);
    }

    public DetailArcChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailArcChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }
    private Paint arcPaint;
    // 弧线的尺寸
    private RectF arcRect;
    /////////////////////////////////////////////
    //动态设置数据显示
    private int tabIndex;

    /**
     * 此方法用于更新数据，在主线程中调用
     * @param data
     * @param tabIndex
     */
    public void setData(float[] data,int tabIndex){
        this.data = data;
        this.tabIndex = tabIndex;
        if(data!=null) {
            //所有的控件都有这个方法，进行刷新操作
            invalidate();
        }
    }
    ////////////////////////////////////////////
    private float width;
    private float height;
    private float sum;
    /**
     * 通用的初始化方法
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        arcPaint = new Paint();
        arcPaint.setColor(Color.BLACK);
        arcPaint.setStyle(Paint.Style.FILL);//填充
        arcPaint.setAntiAlias(true);

        width = getWidth();
        height = getHeight();
        // 参数  left  top   right  bottom
        arcRect = new RectF(0,0,100,100);
        data = new float[]{10000,5000};
    }
    private void getSum(float[] data){
        for (int i = 0; i < data.length; i++) {
            sum+=data[i];
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //清除内容
        canvas.drawColor(Color.WHITE);
        switch (tabIndex){
            case 0:
                getSum(data);
                //画绿色的半圆
                arcPaint.setColor(Color.GREEN);
                float a = (data[0]/sum)*360;
                canvas.drawArc(arcRect,0,a,true,arcPaint);
                //画蓝色的半圆
                arcPaint.setColor(Color.BLUE);
                float b = (data[1]/sum)*360;
                canvas.drawArc(arcRect,a,360-a,true,arcPaint);
                sum=0;
                break;
            case 1:
                getSum(data);
                //画 橙色 的半圆   #FFA500
                arcPaint.setColor(Color.rgb(255,165,0));
                float entertainment = (data[0]/sum)*360;
                canvas.drawArc(arcRect,0,entertainment,true,arcPaint);
                //画 热粉红色 的半圆   #FF69B4
                arcPaint.setColor(Color.rgb(255,105,180));
                float repast = (data[1]/sum)*360;
                canvas.drawArc(arcRect,entertainment,repast,true,arcPaint);
                //画 红色 的半圆  #FF0000
                arcPaint.setColor(Color.rgb(255,0,0));
                float rent = (data[2]/sum)*360;
                canvas.drawArc(arcRect,entertainment+repast,rent,true,arcPaint);
                //画 秘鲁色 的半圆  #CD853F
                arcPaint.setColor(Color.rgb(205,133,63));
                float traffic = (data[3]/sum)*360;
                canvas.drawArc(arcRect,entertainment+repast+rent,traffic,true,arcPaint);
                //画 中粉紫色 的半圆  #BA55D3
                arcPaint.setColor(Color.rgb(186,85,211));
                float shopping = (data[4]/sum)*360;
                canvas.drawArc(arcRect,entertainment+repast+rent+traffic,shopping,true,arcPaint);
                //画 苍宝石绿 的半圆   #AFEEEE
                arcPaint.setColor(Color.rgb(175,238,238));
                float others = (data[5]/sum)*360;
                canvas.drawArc(arcRect,entertainment+repast+rent+traffic+shopping,others,true,arcPaint);
                sum=0;
                break;
        }

    }
}
