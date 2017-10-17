package com.m520it.skipview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class SkipView extends View {

    public static final int TEXT_SIZE = 30;//文字的大小
    public static final int TEXT_MARGIN = 10;//文字距离中间圆边框的间距
    public static final int ARC_WIDTH = 8;//外部圆弧的笔画宽度
    private float mMeasureTextWidth;
    private float mCircleDoubleRadius;
    private float mArcDoubleRadius;
    private RectF mRectF;
    private Paint mArcPaint;
    private Paint mCirclePaint;
    private Paint mTextPaint;

    public SkipView(Context context) {
        super(context);
    }

    public SkipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);

        //中间的圆的画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.GRAY);
        mCirclePaint.setAntiAlias(true);

        //外部圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setColor(Color.RED);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(ARC_WIDTH);

        //1 文字的宽度   2 文字距离中间圆边框的间距  3 外部圆弧的笔画宽度
        mMeasureTextWidth = mTextPaint.measureText("跳过");

        //计算中间的圆的直径
        mCircleDoubleRadius = mMeasureTextWidth + TEXT_MARGIN * 2;
        //计算外部的圆狐的直径
        mArcDoubleRadius = mCircleDoubleRadius + 2 * ARC_WIDTH;

        //准备一个矩形出来,待会画外部圆弧时用得到
        mRectF = new RectF(0+ARC_WIDTH/2, 0+ARC_WIDTH/2,
                mArcDoubleRadius-ARC_WIDTH/2, mArcDoubleRadius-ARC_WIDTH/2);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //大小通过计算外部圆弧的直径
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode==MeasureSpec.AT_MOST){
            widthSize = (int) mArcDoubleRadius;
        }
        if(heightMode==MeasureSpec.AT_MOST){
            heightSize = (int) mArcDoubleRadius;
        }
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制3个东西
        //外部圆弧
        canvas.drawArc(mRectF,0,360,false,mArcPaint);
        //中间的圆
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,mCircleDoubleRadius/2,mCirclePaint);
        //内部的文字
        canvas.drawText("跳过",getMeasuredWidth()/2-mMeasureTextWidth/2
                ,getMeasuredHeight()/2,mTextPaint);

    }
}
