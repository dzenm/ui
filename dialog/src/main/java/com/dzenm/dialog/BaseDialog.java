package com.dzenm.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialog;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * Dialog的抽象类
 */
public abstract class BaseDialog extends AppCompatDialog {

    protected static final float DEFAULT_RADIUS = 8f;

    /**
     * 根布局, 用于设置dialog显示颜色和圆角大小, 以及dialog的长宽, 或者寻找子View的ID
     */
    protected View mView;

    /**
     * dialog显示的背景, 通过设置根布局的背景mView.setBackground(mBackground)设置dialog的背景
     * 默认白色背景和圆角, 自定义背景调用 {@link #setBackground(Drawable)}
     */
    protected Drawable mBackground = Utils.drawableOf(getContext(), DEFAULT_RADIUS);

    /**
     * dialog上下左右的边距值, 默认值为10, 由于不能直接通过WindowManager属性设置, 需要通过
     * ViewGroup.MarginLayoutParams设置topMargin、bottomMargin, 左右的margin通过width设置
     * 自定义边距值调用 {@link #setMargin(int)}
     */
    protected int mMargin = Utils.dp2px(10);

    /**
     * dialog居中时的宽度, 宽度为(屏幕宽度-10*mMargin), 由于居中时, width值过大, 因此在居中时
     * 做一些限制, 改变居中时的宽度
     */
    protected int mCenterWidth = Utils.getDisplayWidth() - 10 * mMargin;

    /**
     * dialog显示的位置，默认显示在中间, 调用 {@link Gravity} 里的值设置
     * 自定义位置调用 {@link #setGravity(int)}
     */
    protected int mGravity = Gravity.CENTER;

    /**
     * dialog动画, 默认根据 {@link #mGravity} 的位置显示动画
     * 当 {@link #mGravity} 的值为 {@link Gravity} TOP 从顶部往下弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity} BOTTOM 从底部往上弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity} CENTER 从中间缩放显示
     * 自定义动画调用 {@link #setAnimator(int)}
     */
    protected int mAnimator = Utils.shrink();

    /**
     * 主要颜色, 除了灰色和白色之外的颜色, 默认为蓝色
     */
    protected int mPrimaryColor;

    /**
     * 次要颜色, 除了灰色和白色之外的颜色, 默认为半透明的蓝色
     */
    protected int mSecondaryColor;

    /**
     * dialog之外的灰色遮罩 (去除dialog灰色区域)
     * 自定义调用 {@link #setTranslucent(boolean)}
     */
    protected boolean isTranslucent = false;

    /**
     * 圆角大小 {@link #DEFAULT_RADIUS}
     */
    protected float mBackgroundRadius = DEFAULT_RADIUS;

    /**
     * 是否在View和View之间添加分割线 {@link #setDivide(boolean)}
     */
    protected boolean isDivide = false;

    protected boolean isDefaultBackground = true;

    protected boolean isDefaultGravity = true;

    protected boolean isDefaultMargin = true;

    protected boolean isDefaultAnimator = true;

    protected OnClickListener<?> mOnClickListener;

    static {
        // 开启在TextView的drawableTop或者其他额外方式使用矢量图渲染
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    //************************************* 以下为自定义提示内容 *********************************

    /**
     * @param margin {@link #mMargin}
     * @return this
     */
    public BaseDialog setMargin(int margin) {
        mMargin = margin;
        isDefaultMargin = false;
        return this;
    }

    /**
     * @param gravity {@link #mGravity}
     * @return this
     */
    public BaseDialog setGravity(int gravity) {
        mGravity = gravity;
        isDefaultGravity = false;
        return this;
    }

    /**
     * @param animator {@link #mAnimator}
     * @return this
     */
    public BaseDialog setAnimator(int animator) {
        mAnimator = animator;
        isDefaultAnimator = false;
        return this;
    }

    /**
     * Dialog背景，默认的background，为白色圆角背景
     * 使用color文件下的颜色时，必须使用getResources().getColor()，否则不显示
     *
     * @param background {@link #mBackground}
     * @return this
     */
    public BaseDialog setBackground(Drawable background) {
        mBackground = background;
        isDefaultBackground = false;
        return this;
    }

    /**
     * @param translucent dialog灰色遮罩 {@link #isTranslucent}
     * @return this
     */
    public BaseDialog setTranslucent(boolean translucent) {
        isTranslucent = translucent;
        return this;
    }

    /**
     * @param cancel 是否可以通过点击返回关闭dialog
     * @return this
     */
    public BaseDialog setCancel(boolean cancel) {
        setCancelable(cancel);
        return this;
    }

    /**
     * @param cancel 是否可以通过点击dialog外部关闭dialog
     * @return this
     */
    public BaseDialog setTouchOutsideCancel(boolean cancel) {
        setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     * @param divide 是否添加线条, 不添加分割线时为MaterialDesign样式 {@link #isDivide}
     * @return this
     */
    public BaseDialog setDivide(boolean divide) {
        isDivide = divide;
        return this;
    }

    /**
     * @param listener dialog的点击事件
     * @return this
     */
    public BaseDialog setOnDialogClickListener(OnClickListener<?> listener) {
        mOnClickListener = listener;
        return this;
    }

    /**
     * 创建并显示Dialog，放在最后调用
     * 继承时若需要设置gravity，animator， background时
     * 必须重写该方法，并且在 super.show() 之前调用
     * 其他有关View的操作在 super.show() 之后调用
     */
    @Override
    public void show() {
        Window window = getWindow();
        setDialogSize(window);
        setStyle();
        setWindowProperty(window);
        super.show();
        afterShowSetting(window);
    }

    /************************************* 以下为实现细节（不可见方法） *********************************/

    public BaseDialog(@NonNull Context context) {
        this(context, R.style.Dialog_Theme);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        create();
        mPrimaryColor = Utils.getColor(context, R.attr.dialogPrimaryColor);
        mSecondaryColor = Utils.getColor(context, R.attr.dialogSecondaryColor);
    }

    protected int layoutId() {
        return 0;
    }

    /**
     * 初始化View控件
     */
    protected void initView() {

    }

    /**
     * 设置默认的效果
     */
    protected void setStyle() {
        if (isDefaultMargin) {
            if (mGravity == Gravity.TOP) {
                if (isDefaultBackground) {
                    // 底部圆角，白色背景
                    mBackground = Utils.drawableOf(getContext(), new float[]{0, 0, 8f, 8f});
                }
            } else if (mGravity == Gravity.BOTTOM) {
                if (isDefaultBackground) {
                    // 顶部圆角，白色背景
                    mBackground = Utils.drawableOf(getContext(), new float[]{8f, 8f, 0, 0});
                }
            }
        }
        if (isDefaultAnimator) {
            if (mGravity == Gravity.TOP) {
                mAnimator = Utils.top();
            } else if (mGravity == Gravity.BOTTOM) {
                mAnimator = Utils.bottom();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutId() != 0) {
            mView = LayoutInflater.from(getContext()).inflate(layoutId(), null);
            setContentView(mView);
        }
        initView();
    }

    /**
     * 供子类查找id
     *
     * @param id view id
     * @return view
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    /**
     * 初始化dialog的大小
     */
    private void setDialogSize(Window window) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        if (isShowCenter()) {
            setCenterDialogLayoutParams(windowAttributes);
        } else {
            setDialogLayoutParams(layoutParams);
            mView.setLayoutParams(layoutParams);
        }
        window.setAttributes(windowAttributes);
    }

    /**
     * 设置dialog的LayoutParams
     */
    protected void setDialogLayoutParams(ViewGroup.MarginLayoutParams layoutParams) {
        layoutParams.topMargin = Utils.dp2px(mMargin);
        layoutParams.bottomMargin = Utils.dp2px(mMargin);
        layoutParams.width = Utils.getWidth() - 2 * Utils.dp2px(mMargin);
    }

    /**
     * 设置dialog居中的LayoutParams
     */
    protected void setCenterDialogLayoutParams(WindowManager.LayoutParams windowAttributes) {
        if (!isPromptDialog()) {
            windowAttributes.width = Utils.getWidth() - 10 * Utils.dp2px(mMargin);
        }
    }

    protected boolean isPromptDialog() {
        return false;
    }

    /**
     * 设置Windows的属性
     */
    protected void setWindowProperty(Window window) {
        window.setGravity(mGravity);                                  // 显示的位置
        window.setWindowAnimations(mAnimator);                        // 窗口动画
        mView.setBackground(mBackground);
    }

    /**
     * Dialog调用show方法之后的一些设置
     */
    protected void afterShowSetting(Window window) {
        // 解决AlertDialog无法弹出软键盘,且必须放在AlertDialog的show方法之后
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 收起键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (isTranslucent) {    // 消除Dialog内容区域外围的灰色
            window.setDimAmount(0);
        }
    }

    /**
     * @return 是否显示在中间
     */
    protected boolean isShowCenter() {
        return mGravity == Gravity.CENTER;
    }

    private interface OnDialogClickListener<T extends BaseDialog> {
        /**
         * @param dialog  DialogFragmentDelegate
         * @param confirm 是否是确定按钮，通过这个判断点击的是哪个按钮
         * @return 返回true表示，点击之后会dismiss dialog， 返回false不dismiss dialog
         */
        boolean onClick(T dialog, boolean confirm);
    }

    public abstract static class OnClickListener<T extends BaseDialog>
            implements OnDialogClickListener<T> {
        @Override
        public boolean onClick(T dialog, boolean confirm) {
            return true;
        }
    }
}