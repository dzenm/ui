package com.dzenm;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author dzenm
 * @date 2019-08-14 10:22
 */
public class ProgressView extends View {

    private static final String TAG = ProgressView.class.getSimpleName();

    /**
     * 进度条显示的高度（可通过xml的属性设置）
     */
    private int mProgressHeight;

    /**
     * 已加载的进度条的颜色（可通过xml的属性设置）
     */
    private int mProgressColor;

    /**
     * 当前已完成的进度值
     */
    private int mProgressValue;

    /**
     * 总的进度数量（可通过xml的属性设置）
     */
    private int mMaxValue;

    /**
     * 显示的默认文字
     */
    private String mText;

    /**
     * 文本颜色
     */
    private int mTextColor;

    /**
     * 百分比的文字大小（可通过xml的属性设置）
     */
    private int mTextSize;

    /**
     * 文本的前后间距
     */
    private float mTextPadding;

    /**
     * 百分比文字是否静止在末尾（可通过xml的属性设置）
     */
    private boolean isTextStatic;

    /**
     * 是否通过百分比显示
     */
    private boolean isShowDefaultText;

    /**
     * 是否设置端点半圆形
     */
    private boolean isStrokeCapRound;

    /**
     * 绘制背景灰色线条画笔
     */
    private Paint mPaintRemainingValue;

    /**
     * 绘制进度条画笔
     */
    private Paint mPaintProgressValue;

    /**
     * 绘制下载进度画笔
     */
    private Paint mPaintText;

    /**
     * 获取百分比数字的长宽
     */
    private Rect mTextBound;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);

        mProgressHeight = (int) t.getDimension(R.styleable.ProgressView_progressHeight,
                dp2px(8));
        mProgressValue = t.getInteger(R.styleable.ProgressView_currentValue, 0);
        mProgressColor = t.getColor(R.styleable.ProgressView_progressColor,
                getResources().getColor(android.R.color.holo_blue_light));
        mMaxValue = t.getInteger(R.styleable.ProgressView_maxValue, 100);

        mText = t.getString(R.styleable.ProgressView_text);
        mTextSize = (int) t.getDimension(R.styleable.ProgressView_textSize, 36);
        mTextColor = t.getColor(R.styleable.ProgressView_textColor,
                getResources().getColor(android.R.color.holo_blue_light));

        isTextStatic = t.getBoolean(R.styleable.ProgressView_isTextStatic, false);
        isStrokeCapRound = t.getBoolean(R.styleable.ProgressView_isStrokeCapRound, true);

        mTextPadding = dp2px(8);

        isShowDefaultText = mText == null;
        t.recycle();
    }

    {
        mPaintRemainingValue = new Paint();
        mPaintProgressValue = new Paint();
        mPaintText = new Paint();
        mTextBound = new Rect();
    }

    /**
     * @param progressHeight 进度条的高度
     */
    public void setProgressHeight(int progressHeight) {
        this.mProgressHeight = progressHeight;
        invalidate();
    }

    /**
     * @param progressColor 进度条颜色
     */
    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidate();
    }

    /**
     * @param progressValue 当前进度值
     */
    public void setProgressValue(int progressValue) {
        if (progressValue > mMaxValue) return;
        mProgressValue = progressValue;
        invalidate();
    }

    /**
     * @param maxValue 进度条的最大值
     */
    public void setMaxValue(int maxValue) {
        this.mMaxValue = maxValue;
        invalidate();
    }

    /**
     * @param text 设置文本
     */
    public void setText(@NonNull String text) {
        this.mText = text;
        isShowDefaultText = false;
        invalidate();
    }

    /**
     * @param textSize 文本的大小
     */
    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        invalidate();
    }

    /**
     * @param textColor 设置文本颜色
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        invalidate();
    }

    /**
     * @param textStatic 文本是否是静止在进度条最后面
     */
    public void setTextStatic(boolean textStatic) {
        isTextStatic = textStatic;
        invalidate();
    }

    /**
     * @param strokeCapRound 进度条两端是否是半圆形的
     */
    public void setStrokeCapRound(boolean strokeCapRound) {
        isStrokeCapRound = strokeCapRound;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // 根据文字的位置设置文字的Rect大小
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(mTextColor);
        mPaintText.setTextSize(mTextSize);
        // 设置绘制百分比文字属性
        mText = isShowDefaultText ? mProgressValue + "%" : mText;
        mPaintText.getTextBounds(mText, 0, mText.length(), mTextBound);

        // 文本前后与进度条的间距大小
        float textPadding = isTextStatic ? mTextPadding : 2 * mTextPadding;
        textPadding = isStrokeCapRound ? textPadding : textPadding / 2;
        // 半圆形端点的偏移量，不进行偏移会导致前后端点变成矩形
        int endPointOffset = isStrokeCapRound ? (mProgressHeight >> 1) : 0;
        // 进度的百分比
        float percentage = (getMeasuredWidth() - getTextWidth() - textPadding) *
                mProgressValue / mMaxValue;
        // 绘制文本的起始位置
        float startText = isTextStatic
                ? getMeasuredWidth() - getTextWidth()
                : percentage + (textPadding / 2);
        // 绘制剩余量的起始位置
        float startRemaining = isTextStatic
                ? mProgressValue == 0 ? percentage + endPointOffset : percentage
                : percentage + getTextWidth() + textPadding;
        // 绘制剩余量的结束位置
        float endRemaining = isTextStatic
                ? startText - textPadding
                : getMeasuredWidth() - endPointOffset;

        // 距离顶部偏移量
        int offsetTop = (getMeasuredHeight() >> 1);
        Log.d(TAG, "total height: " + getHeight() + ", offset top: " + offsetTop);

        // 绘制文字
        canvas.drawText(mText, startText, offsetTop + (mTextBound.height() >> 1), mPaintText);

        // 绘制剩余进度条的底色
        setPaint(mPaintRemainingValue, Color.parseColor("#E6E6E6"));
        canvas.drawLine(startRemaining, offsetTop, endRemaining, offsetTop, mPaintRemainingValue);

        if (mProgressValue != 0) {
            // 绘制进度条已进行的颜色
            setPaint(mPaintProgressValue, mProgressColor);
            canvas.drawLine(endPointOffset, offsetTop, percentage, offsetTop, mPaintProgressValue);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(getSuggestedMinimumWidth(), widthMeasureSpec);
        int defaultHeight = Math.max(mProgressHeight, dp2px(32));
        int height = measureHeight(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 计算需要的宽度
     *
     * @param defaultWidth 默认的宽度
     * @param measureSpec  测量规格
     * @return 宽度
     */
    private int measureWidth(int defaultWidth, int measureSpec) {
        int width = defaultWidth;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            width = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            width = Math.max(defaultWidth, specSize);
        }
        return width;
    }

    /**
     * 计算需要的高度
     *
     * @param defaultHeight 默认的高度
     * @param measureSpec   测量规格
     * @return 高度
     */
    private int measureHeight(int defaultHeight, int measureSpec) {
        int height = defaultHeight;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            height = defaultHeight;
        } else if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            height = Math.max(defaultHeight, specSize);
        }
        return height;
    }

    private void setPaint(Paint paint, int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        if (isStrokeCapRound) {
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
        paint.setStrokeWidth((float) mProgressHeight);
    }

    /**
     * 获取文字的宽度
     */
    private int getTextWidth() {
        Paint paint = new Paint();
        paint.setTextSize(mTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.getTextBounds(mText, 0, mText.length(), mTextBound);
        return mTextBound.width() + dp2px(2);
    }

    /**
     * @param value 需要转换的dp值
     * @return dp值
     */
    private static int dp2px(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                Resources.getSystem().getDisplayMetrics()
        );
    }
}