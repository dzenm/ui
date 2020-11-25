package com.dzenm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.widget.Checkable;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.dzenm.switch_button.R;

/**
 * @author dzenm
 */
public class SwitchButton extends View implements Checkable {

    private final static String TAG = "SwitchButton";
    private static final int DEFAULT_WIDTH = dp2px(80);
    private static final int DEFAULT_HEIGHT = dp2px(40);
    private final static long DEFAULT_DURATION = 200L;
    private final static long NULL_DURATION = 0L;
    private final static int STATE_DRAG = 0x01;
    private final static int STATE_CLICK = 0x02;

    private Paint mPaint;
    private Paint mButtonPaint;

    /**
     * 背景的位置和大小
     */
    private RectF mBackground = new RectF();
    /**
     * 边框的大小
     */
    private int mBackgroundStrokeWidth = dp2px(1f);
    /**
     * 选中时的背景颜色
     */
    private int mBackgroundCheckedColor = 0xFF51D367;
    /**
     * 未选中时的背景颜色
     */
    private int mBackgroundUncheckedColor = 0X33000000;

    /**
     * 按钮切换时缩放的大小
     */
    private RectF mToggleScale = new RectF();
    /**
     * 按钮切换时缩放的偏移量
     */
    private int mToggleScaleOffset = dp2px(1f);

    /**
     * 按钮选中的颜色
     */
    private int mButtonSelectedColor = 0xFFFFFFFF;
    /**
     * 按钮未选中的颜色
     */
    private int mButtonUnselectedColor = 0xFFFFFFFF;
    /**
     * 按钮相对于最左边的偏移量
     */
    private int mButtonOffset = 0;
    /**
     * 按钮阴影的圆角
     */
    private int mShadowRadius = dp2px(2.5f);
    /**
     * 按钮阴影的颜色
     */
    private int mShadowColor = 0X22000000;
    /**
     * 按钮阴影的偏移量
     */
    private int mShadowOffset = dp2px(1.5f);

    /**
     * 切换状态的透明度变化
     */
    private float mAlpha;
    /**
     * 选中的状态
     */
    private boolean isChecked = false;
    /**
     * 滑动还是点击效果
     */
    private boolean isScroller = false;
    /**
     * 是否开启过渡动画
     */
    private boolean isEnabledAnimator = true;
    /**
     * 是否正在运动动画
     */
    private boolean isRunningAnimator = false;
    /**
     * 是否设置阴影效果
     */
    private boolean isShadow = true;
    /**
     * 是否默认是选中状态
     */
    private boolean isCheckFromResource = false;

    /**
     * 颜色透明度随着滑动的位置改变的估值
     */
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    /**
     * 触摸事件的方式
     */
    private int mTouchState = STATE_CLICK;

    /**
     * 状态改变的监听事件
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    {
        // 需禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        mButtonSelectedColor = a.getColor(R.styleable.SwitchButton_buttonCheckedColor, mButtonSelectedColor);
        mButtonUnselectedColor = a.getColor(R.styleable.SwitchButton_buttonUncheckedColor, mButtonUnselectedColor);
        mBackgroundCheckedColor = a.getColor(R.styleable.SwitchButton_checkedColor, mBackgroundCheckedColor);
        mBackgroundUncheckedColor = a.getColor(R.styleable.SwitchButton_uncheckedColor, mBackgroundUncheckedColor);

        isChecked = a.getBoolean(R.styleable.SwitchButton_isChecked, isChecked);
        isEnabledAnimator = a.getBoolean(R.styleable.SwitchButton_isEnabledAnimator, isEnabledAnimator);
        isShadow = a.getBoolean(R.styleable.SwitchButton_isShadow, isShadow);
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (isShadow) {
            mButtonPaint.setShadowLayer(mShadowRadius, dp2px(1f), mShadowOffset, mShadowColor);
        }
        post(new Runnable() {
            @Override
            public void run() {
                isCheckFromResource = true;
                invalidate();
                toggleWithAnimator(!isChecked, NULL_DURATION);
            }
        });
    }

    public void setCheckedColor(int checkedColor) {
        mBackgroundCheckedColor = checkedColor;
    }

    public void setUncheckedColor(int uncheckedColor) {
        mBackgroundUncheckedColor = uncheckedColor;
    }

    public void setButtonSelectedColor(int selectedColor) {
        mButtonSelectedColor = selectedColor;
    }

    public void setButtonUnselectedColor(int buttonUnselectedColor) {
        mButtonUnselectedColor = buttonUnselectedColor;
    }

    public void setEnabledAnimator(boolean enabledAnimator) {
        isEnabledAnimator = enabledAnimator;
    }

    public void setShadow(boolean shadow) {
        isShadow = shadow;
        if (isShadow) {
            mButtonPaint.setShadowLayer(mShadowRadius, 0, mShadowOffset, mShadowColor);
        } else {
            mButtonPaint.setShadowLayer(0, 0, 0, 0);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackground = new RectF(
                mBackgroundStrokeWidth,
                mBackgroundStrokeWidth,
                getMeasuredWidth() - mBackgroundStrokeWidth,
                getMeasuredHeight() - mBackgroundStrokeWidth
        );
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // 调用父View的onDraw函数，因为View这个类帮我们实现了一些基本的而绘制功能，比如绘制背景颜色、背景图片
        super.onDraw(canvas);

        // 获取高的一半大小
        int mid = getMeasuredHeight() >> 1;

        // 绘制背景
        int color = (int) mArgbEvaluator.evaluate(mAlpha, mBackgroundUncheckedColor, mBackgroundCheckedColor);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(mBackgroundStrokeWidth);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(mBackground, mid, mid, mPaint);

        // 绘制切换时的过渡动画
        mToggleScale = new RectF(
                mButtonOffset + 2 * mBackgroundStrokeWidth,
                mToggleScaleOffset + mBackgroundStrokeWidth,
                getMeasuredWidth() - mToggleScaleOffset - mBackgroundStrokeWidth,
                getMeasuredHeight() - mToggleScaleOffset - mBackgroundStrokeWidth
        );
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mToggleScale, mid, mid, mPaint);

        // 绘制切换的按钮
        int cx = mButtonOffset + mid;
        mButtonPaint.setColor(isChecked ? mButtonSelectedColor : mButtonUnselectedColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, mid, getButtonRadius() - mBackgroundStrokeWidth, mButtonPaint);

        // 绘制切换的按钮的阴影
        mButtonPaint.setStyle(Paint.Style.STROKE);
        mButtonPaint.setColor(mShadowColor);
        mButtonPaint.setStrokeWidth(1);
        canvas.drawCircle(cx, mid, getButtonRadius() - mBackgroundStrokeWidth, mButtonPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设置默认的宽度
        int width = getMeasureSize(DEFAULT_WIDTH, widthMeasureSpec);
        // 设置默认的高度
        int height = getMeasureSize(DEFAULT_HEIGHT, heightMeasureSpec);
        // 重新设置大小
        setMeasuredDimension(width, height);
    }

    private int getMeasureSize(int defaultSize, int measureSpec) {
        int measureSize = defaultSize;

        // 获取测量模式和测量的大小
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            // 如果没有指定实际的大小，就使用默认给的大小
            case MeasureSpec.UNSPECIFIED:
                // 如果获取的最大值为size，wrap_content
            case MeasureSpec.AT_MOST:
                // 当前View的大小即是它的默认大小，也可以默认设置其他值
                measureSize = defaultSize;
                break;
            // 如果是固定大小，不进行改变，match_parent
            case MeasureSpec.EXACTLY:
                measureSize = size;
                break;
            default:
                break;
        }
        return measureSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        float downX = 0;
        if (isRunningAnimator || !isEnabled()) {
            return false;
        }
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float offset = Math.abs(x - downX);
                if (offset > 5) {
//                    isScroller = true;
                    float fraction = x / getWidth();
                }

                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                if (!isScroller) {
                    toggleWithAnimator(isChecked, isEnabledAnimator ? DEFAULT_DURATION : NULL_DURATION);
                }
                isScroller = false;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }
        return handled;
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;


            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, isChecked);
            }

            final AutofillManager afm = getContext().getSystemService(AutofillManager.class);
            if (afm != null) {
                afm.notifyValueChanged(this);
            }

            invalidate();
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    /**
     * 切换状态携带动画
     *
     * @param reverse  是否反向执行动画运行
     * @param duration 动画时长
     */
    private void toggleWithAnimator(final boolean reverse, long duration) {
        int scrollWidth = getTotalOffsetWidth();
        final int start = reverse ? scrollWidth : mBackgroundStrokeWidth;
        final int end = reverse ? mBackgroundStrokeWidth : scrollWidth;

        // 切换状态的动画
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 动画进行的百分比
                float fraction = animation.getAnimatedFraction();

                float percent = reverse ? 1 - fraction : fraction;
                Log.d(TAG, "onAnimationUpdate percent: " + percent);

                int totalHeight = getMeasuredHeight() >> 1;
                mToggleScaleOffset = (int) (percent * totalHeight);
                Log.d(TAG, "onAnimationUpdate mToggleScaleOffset: " + mToggleScaleOffset);

                int totalWidth = Math.abs(end - start);
                int buttonOffset = (int) (percent * totalWidth);
                Log.d(TAG, "onAnimationUpdate mButtonOffset: " + buttonOffset);

                toggleStateWithInvalidate(percent, buttonOffset, mToggleScaleOffset);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isCheckFromResource) {
                    isCheckFromResource = false;
                } else {
                    toggle();
                }
                isRunningAnimator = false;
            }
        });
        isRunningAnimator = true;
        animator.start();
    }

    /**
     * 切换选中的状态
     *
     * @param alpha            透明度变化
     * @param horizontalOffset 横向的偏移量变化
     * @param verticalOffset   纵向的偏移量变化
     */
    private void toggleStateWithInvalidate(float alpha, int horizontalOffset, int verticalOffset) {
        Log.d(TAG, "alpha: " + alpha);
        mAlpha = alpha;
        mButtonOffset = horizontalOffset;
        mToggleScaleOffset = verticalOffset;
        invalidate();
    }

    /**
     * @return 获取总的可偏移宽度
     */
    private int getTotalOffsetWidth() {
        int totalWidth = getMeasuredWidth();
        int buttonRadius = getButtonRadius();
        return totalWidth - 2 * (buttonRadius + mBackgroundStrokeWidth);
    }

    /**
     * @return 获取切换按钮的半径
     */
    private int getButtonRadius() {
        return (getMeasuredHeight() >> 1) - mBackgroundStrokeWidth;
    }

    private boolean isDrag() {
        return mTouchState == STATE_DRAG;
    }

    private boolean isClick() {
        return mTouchState == STATE_CLICK;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return SwitchButton.class.getName();
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(isChecked);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCheckable(true);
        info.setChecked(isChecked);
    }

    private static class SavedState extends BaseSavedState {

        private boolean checked;

        /**
         * Constructor called from {@link CompoundButton#onSaveInstanceState()}
         */
        private SavedState(Parcelable source) {
            super(source);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.checked = isChecked();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    @Override
    public void onProvideStructure(ViewStructure structure) {
        super.onProvideStructure(structure);
        structure.setDataIsSensitive(!isCheckFromResource);
    }


    @Override
    public void autofill(AutofillValue value) {
        if (!isEnabled()) {
            return;
        }

        if (!value.isToggle()) {
            Log.w(TAG, value + " could not be autofilled into " + this);
            return;
        }
        setChecked(value.getToggleValue());
    }

    @Override
    public int getAutofillType() {
        return isEnabled() ? AUTOFILL_TYPE_TOGGLE : AUTOFILL_TYPE_NONE;
    }

    @Override
    public AutofillValue getAutofillValue() {
        return isEnabled() ? AutofillValue.forToggle(isChecked()) : null;
    }

    private static int dp2px(float value) {
        Resources r = Resources.getSystem();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
    }

    public interface OnCheckedChangeListener {
        /**
         * @param isChecked 是否选中
         */
        void onCheckedChanged(SwitchButton switchButton, boolean isChecked);
    }

}
