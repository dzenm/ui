package com.dzenm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SideBar extends View {

    private Paint mPaint = new Paint();

    /**
     * 显示滑动的文本内容
     */
    private String[] mTexts = {
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z", "#"
    };

    /**
     * 选中的位置
     */
    private int mSelectedPosition = -1;
    /**
     * 选中的文本颜色
     */
    private int mSelectedTextColor;
    /**
     * 未选中的文本颜色
     */
    private int mUnselectedTextColor;
    /**
     * 选中的背景颜色
     */
    private int mSelectedBackgroundColor;
    /**
     * 触摸事件
     */
    private OnScrollChangedListener mOnScrollChangedListener;

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideBar);
        mSelectedTextColor = a.getColor(R.styleable.SideBar_selectedTextColor, Color.WHITE);
        mUnselectedTextColor = a.getColor(R.styleable.SideBar_unselectedTextColor,
                Color.parseColor("#757575"));
        mUnselectedTextColor = a.getColor(R.styleable.SideBar_unselectedBackgroundColor, Color.RED);

        a.recycle();
    }

    public void setTexts(String... mTexts) {
        this.mTexts = mTexts;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.mSelectedTextColor = selectedTextColor;
    }

    public void setUnselectedTextColor(int unselectedTextColor) {
        this.mUnselectedTextColor = unselectedTextColor;
    }

    public void setSelectedBackgroundColor(int selectedBackgroundColor) {
        this.mSelectedBackgroundColor = selectedBackgroundColor;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        this.mOnScrollChangedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取焦点改变背景颜色.
        int height = getHeight();                       // 获取对应高度
        int width = getWidth();                         // 获取对应宽度
        int singleHeight = height / mTexts.length;      // 获取每一个文本的高度

        for (int i = 0; i < mTexts.length; i++) {
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(36);

            // 选中时的状态
            if (i == mSelectedPosition) {
                mPaint.setColor(mSelectedBackgroundColor);
                mPaint.setStyle(Paint.Style.FILL);
                int radius = singleHeight >> 1;
                int cx = width >> 1;
                int cy = singleHeight * i + singleHeight * 3 / 4;
                canvas.drawCircle(cx, cy, radius, mPaint);

                mPaint.setColor(mSelectedTextColor);
                mPaint.setFakeBoldText(true);
            } else {
                mPaint.setColor(mUnselectedTextColor);
            }

            // x坐标等于中间-字符串宽度的一半.
            float xPos = (width >> 1) - mPaint.measureText(mTexts[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(mTexts[i], xPos, yPos, mPaint);

            // 重置画笔
            mPaint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int oldSelected = mSelectedPosition;

        // 点击y坐标
        final float y = event.getY();
        // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        final int selectIndex = (int) (y / getHeight() * mTexts.length);

        if (action == MotionEvent.ACTION_UP) {
            setBackground(new ColorDrawable(0x00000000));
            mSelectedPosition = -1;
            invalidate();
            if (mOnScrollChangedListener != null) {
                mOnScrollChangedListener.onScrollChanged(null);
            }
        } else {
            if (oldSelected != selectIndex
                    && selectIndex >= 0
                    && selectIndex < mTexts.length) {
                if (mOnScrollChangedListener != null) {
                    mOnScrollChangedListener.onScrollChanged(mTexts[selectIndex]);
                }
                mSelectedPosition = selectIndex;
                invalidate();
            }
        }
        return true;
    }

    /**
     * 滑动时的监听事件
     */
    public interface OnScrollChangedListener {
        /**
         * @param text 当前滑动时所在的文本位置, 如果没有对应的文本或者没有进行滑动, 那么该值为null
         */
        void onScrollChanged(String text);
    }
}