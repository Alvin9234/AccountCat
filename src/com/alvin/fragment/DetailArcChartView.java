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
        // 参数  left  top   right  bottom
        arcRect = new RectF(150,100,360,310);
        data = new float[]{10000,5000};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //清除内容
        canvas.drawColor(Color.WHITE);

        switch (tabIndex){
            case 0:
                //画绿色的半圆
                arcPaint.setColor(Color.GREEN);
                float a = (float)((data[0]/(data[0]+data[1]))*360);
                canvas.drawArc(arcRect,0,a,true,arcPaint);
                //画蓝色的半圆
                arcPaint.setColor(Color.BLUE);
                float b = (float)((data[1]/(data[0]+data[1]))*360);
                canvas.drawArc(arcRect,a,360-a,true,arcPaint);
                break;
            case 1:
                //画绿色的半圆
                arcPaint.setColor(Color.GREEN);
                float q = (float)((data[0]/(data[0]+data[1]+data[2]+data[3]))*360);
                canvas.drawArc(arcRect,0,q,true,arcPaint);
                //画蓝色的半圆
                arcPaint.setColor(Color.BLUE);
                float w = (float)((data[1]/(data[0]+data[1]+data[2]+data[3]))*360);
                canvas.drawArc(arcRect,q,w,true,arcPaint);
                //画蓝色的半圆
                arcPaint.setColor(Color.RED);
                float e = (float)((data[2]/(data[0]+data[1]+data[2]+data[3]))*360);
                canvas.drawArc(arcRect,q+w,e,true,arcPaint);
                //画蓝色的半圆
                arcPaint.setColor(Color.GRAY);
                float r = (float)((data[3]/(data[0]+data[1]+data[2]+data[3]))*360);
                canvas.drawArc(arcRect,q+w+e,r,true,arcPaint);
        }

    }
}
