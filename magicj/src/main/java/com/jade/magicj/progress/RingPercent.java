package com.jade.magicj.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.jade.magicj.R;

import java.io.UncheckedIOException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * @author jadeshi
 * @date 2018/3/22
 */

public class RingPercent extends View {

    private boolean mAutoGrowth = false;

    /**
     * 要画的弧度
     */
    private int mEndAngle;

    /**
     * 圆的半径
     */
    private float mRadius;
    /**
     * 色带的宽度
     */
    private float mStripeWidth;

    /**
     * 实际百分比进度
     */
    private int mPercent;

    /**
     * 动画位置百分比进度
     */
    private int mCurPercent;

    /**
     * 圆心横坐标
     */
    private float mCenterX;

    /**
     * 圆心纵坐标
     */
    private float mCenterY;

    /**
     * 圆环的颜色
     */
    private int mRingColor;

    /**
     * 进度颜色
     */
    private int mProgressColor;

    /**
     * 中文百分比文字大小
     */
    private float mCenterTextSize;

    /**
     * 是否显示中间文字
     */
    private boolean mCenterTextIsDisplay;

    /**
     * 底部字体颜色
     */
    private int mBottomTextColor;

    private long mMax = 100;


    /**
     * 底部文字
     */
    private String mBottomText;

    public String getmBottomText() {
        return mBottomText;
    }

    public void setmBottomText(String mBottomText) {
        this.mBottomText = mBottomText;
    }

    /**
     * 是否显示底部文字
     */
    private boolean mBootomTextisDisplay;

    public boolean ismAutoGrowth() {
        return mAutoGrowth;
    }

    public void setmAutoGrowth(boolean mAutoGrowth) {
        this.mAutoGrowth = mAutoGrowth;
    }

    public long getMax() {
        return mMax;
    }

    public void setMax(long max) {
        mMax = max;
    }

    public RingPercent(Context context) {
        this(context, null);
    }

    public RingPercent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingPercent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RingPercent, defStyleAttr, 0);

        //获取色带宽度
        mStripeWidth = a.getDimension(R.styleable.RingPercent_stripeWidth, PxUtils.dpToPx(10, context));
        mRingColor = a.getColor(R.styleable.RingPercent_ringColor, 0xffafb4db);
        mProgressColor = a.getColor(R.styleable.RingPercent_progressColor, 0xff6950a1);
        mCenterTextSize = a.getDimensionPixelSize(R.styleable.RingPercent_centerTextSize, PxUtils.spToPx(20, context));
        mRadius = a.getDimensionPixelSize(R.styleable.RingPercent_radius, PxUtils.dpToPx(0, context));
        mCenterTextIsDisplay = a.getBoolean(R.styleable.RingPercent_centerTextDisplay, false);
        mBootomTextisDisplay = a.getBoolean(R.styleable.RingPercent_bottomTextDisplay, false);
        mBottomTextColor = a.getColor(R.styleable.RingPercent_bottomTextColor, 0xffafb4db);
        init();

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //计算圆环的圆心坐标
        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;
        //计算圆环的半径，这里取View的长宽中较小值的1/2
        if (mRadius == 0) {
            mRadius = (mCenterX > mCenterY ? mCenterY : mCenterX) / 2;
        }
    }

    private Paint paint;
    private Paint progressPaint;

    private void init() {


        //圆环背景绘图工具
        paint = new Paint();
        paint.setStrokeWidth(mStripeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(mRingColor);


        //进度绘图工具
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(mProgressColor);
        progressPaint.setStrokeWidth(mStripeWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);


    }

    @Override
    protected void onDraw(Canvas canvas) {


        mEndAngle = (int) (mCurPercent * 3.6);
        super.onDraw(canvas);
        //画出灰色的圆环
        canvas.drawCircle(mCenterX, mCenterY, mRadius, paint);
        //构造圆环的外围矩形
        RectF rectF = new RectF(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
        //画出进度，注意这里的270表示从圆环的最顶部开始画，如果270替换为0，则是从圆环的右端开始画
        canvas.drawArc(rectF, 270, mEndAngle, false, progressPaint);


        //绘制文本
        if (mCenterTextIsDisplay) {
            Paint centerTextPaint = new Paint();
            String text = mCurPercent + "%";

            centerTextPaint.setTextSize(mCenterTextSize);
            float textLength = centerTextPaint.measureText(text);

            centerTextPaint.setColor(mProgressColor);
            canvas.drawText(text, mCenterX - textLength / 2, mCenterY, centerTextPaint);
        }


        if (mBootomTextisDisplay && mBottomText != null) {
            Paint bottomTextPaint = new Paint();
            bottomTextPaint.setTextSize(mCenterTextSize);
            float bottomTextLength = bottomTextPaint.measureText(mBottomText);
            bottomTextPaint.setColor(mBottomTextColor);
            canvas.drawText(mBottomText, mCenterX - bottomTextLength / 2, mCenterY + mRadius + mStripeWidth + 20, bottomTextPaint);


            Paint labelPaint = new Paint();
            labelPaint.setAntiAlias(true);
            labelPaint.setStyle(Paint.Style.FILL);
            //todo 标注大小
            labelPaint.setStrokeWidth(2);

            labelPaint.setColor(mProgressColor);
            //y轴需要减去10的一半才能和文字对齐
            canvas.drawCircle(mCenterX - bottomTextLength / 2 - 10, mCenterY + mRadius + +mStripeWidth + 14, 4, labelPaint);


        }


    }

    public void setPercent(long amount) {
        int percent;
        percent = (int) (amount * 100 / mMax);
        if (mAutoGrowth) {
            while (percent > 70) {
                mMax *= 2;
                percent = (int) (amount * 100 / mMax);
            }
        } else {
            if (percent > 100) {
                throw new IllegalArgumentException("percent must less than 100!");
            }
        }
        if (mBootomTextisDisplay) {
            mBottomText = mBottomText.substring(0, mBottomText.indexOf(":") + 1);
            mBottomText = mBottomText + amount + "/" + mMax;
        }


        setCurPercent(percent);


    }


    private void setCurPercent(int percent) {

        mPercent = percent;
        ExecutorService fixedThreadPool = newFixedThreadPool(3);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int sleepTime = 1;
                for (int i = 0; i <= mPercent; i++) {
                    if (i % 20 == 0) {
                        sleepTime += 2;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCurPercent = i;
                }
                RingPercent.this.postInvalidate();
            }
        });
    }
}
