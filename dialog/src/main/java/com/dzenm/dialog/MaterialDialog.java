package com.dzenm.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

/**
 * @author dinzhenyan
 * @date 2019-05-18 15:23
 * <pre>
 *  new MaterialDialog.Builder(this)
 *         .setTitle("Title")
 *         .setMessage("Message")
 *         .setButtonText("ACCEPT", "DECLINE")
 *         .setOnClickListener(new MaterialDialog.OnClickListener() {
 *             public void onClick(MaterialDialog dialog, int which) {
 *                 dialog.dismiss();
 *             }
 *         }).create().show();
 *  将标题设为图片
 *  new MaterialDialog.Builder(this)
 *         .setIcon(R.drawable.ic_warm)
 *         .setMessage("Message")
 *         .setMaterialDesign(false)
 *         .setButtonText("ACCEPT", "DECLINE")
 *         .setOnClickListener(new MaterialDialog.OnClickListener() {
 *             public void onClick(MaterialDialog dialog, int which) {
 *                 dialog.dismiss();
 *             }
 *         }).create().show();
 *  列表选项dialog
 *  new MaterialDialog.Builder(this)
 *         .setTitle("Title")
 *         .setMessage("Hello Message")
 *         .setItem(mItems)
 *         .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
 *             public void onClick(MaterialDialog dialog, int which) {
 *                 Toast.makeText(DialogActivity.this, "" + mItems[which], Toast.LENGTH_SHORT).show();
 *             }
 *         }).create().show();
 *  单选列表dialog
 *  new MaterialDialog.Builder(this)
 *         .setTitle("Title")
 *         .setItem(mItems)
 *         .setOnSingleClickListener(new MaterialDialog.OnSingleClickListener() {
 *             public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
 *                 Toast.makeText(DialogActivity.this, "" + mItems[which] + ", checked: " + isChecked, Toast.LENGTH_SHORT).show();
 *             }
 *         }).create().show();
 *  多选列表dialog
 *  new MaterialDialog.Builder(this)
 *         .setTitle("Title")
 *         .setItem(mItems)
 *         .setOnMultipleClickListener(new MaterialDialog.OnMultipleClickListener() {
 *             public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
 *                 Toast.makeText(DialogActivity.this, "" + mItems[which] + ", checked: " + isChecked, Toast.LENGTH_SHORT).show();
 *             }
 *         }).create().show();
 * </pre>
 */
public final class MaterialDialog extends AppCompatDialogFragment {

    static final int TYPE_ITEM = -1;
    static final int TYPE_SINGLE = -2;
    static final int TYPE_MULTIPLE = -3;

    /**
     * The identifier for the positive button.
     */
    public static final int BUTTON_POSITIVE = -1;

    /**
     * The identifier for the negative button.
     */
    public static final int BUTTON_NEGATIVE = -2;

    /**
     * The identifier for the neutral button.
     */
    public static final int BUTTON_NEUTRAL = -3;

    /**
     * dialog delegate, {@link DialogDelegate}
     */
    protected DialogDelegate mD;

    /**
     * manager fragment's activity
     */
    protected AppCompatActivity mActivity;

    /**
     * content view, not include title and button, if {@link #mMessage} is null, content view is
     * {@link MaterialView#createListView(boolean, boolean)}
     */
    private View mContentView;

    /**
     * @see Builder#mAdapter
     */
    private BaseAdapter mAdapter;

    /**
     * @see Builder#mTitle and
     * @see Builder#mMessage
     */
    private CharSequence mTitle, mMessage;

    /**
     * @see Builder#mTitleColor
     */
    private int mTitleColor;

    /**
     * @see Builder#mIcon
     */
    private Drawable mIcon;

    /**
     * @see Builder#mItems
     */
    private CharSequence[] mItems;

    /**
     * @see Builder#mPositiveButtonText or
     * @see Builder#mNegativeButtonText
     */
    private CharSequence mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;

    /**
     * @see Builder#mPositiveClickListener or
     * @see Builder#mNegativeClickListener
     */
    private OnClickListener mPositiveClickListener, mNegativeClickListener, mNeutralClickListener;

    /**
     * @see #BUTTON_POSITIVE
     * @see #BUTTON_NEGATIVE
     * @see #BUTTON_NEUTRAL
     */
    private int mWhichType;

    /**
     * @see Builder#onMultipleClickListener and
     * @see Builder#mItems
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * @see Builder#mOnSingleClickListener and
     * @see Builder#mItems
     */
    private OnSingleClickListener mOnSingleClickListener;

    /**
     * @see Builder#mOnMultipleClickListener and
     * @see Builder#mItems
     */
    private OnMultipleClickListener mOnMultipleClickListener;

    /**
     * @see Builder#mIContentView
     */
    private IContentView mIContentView;

    // **************************************** 以下为实现过程 *********************************** //

    static {
        // 开启在TextView的drawableTop或者其他额外方式使用矢量图渲染
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private MaterialDialog(AppCompatActivity activity) {
        mActivity = activity;
        mD = new DialogDelegate(this, activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mD.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mD.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mD.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mD.onStart();
    }

    /**
     * 设置一些属性完成之后，调用该方法创建fragment并显示。
     */
    public void show() {
        show(getClass().getSimpleName());
    }

    /**
     * @param tag 给fragment添加tag
     */
    public void show(String tag) {
        show(mActivity.getSupportFragmentManager(), tag);
    }

    protected void initView() {
        MaterialView mMaterialView = new MaterialView(this, mD, mActivity);

        // 获取创建的DecorView
        LinearLayout decorView = mD.mDecorView;

        // 判断是否显示标题, 是否显示Message, Content, 是否显示按钮
        boolean isShowTitle = !TextUtils.isEmpty(mTitle) || mIcon != null;
        boolean isShowContent = !TextUtils.isEmpty(mMessage);
        boolean isShowButton = !(
                (TextUtils.isEmpty(mPositiveButtonText)
                        && TextUtils.isEmpty(mNegativeButtonText))
                        && TextUtils.isEmpty(mNeutralButtonText)
        );

        ViewGroup scrollLayout = mMaterialView.createScrollLayout();
        ViewGroup parent = mMaterialView.createContentLayout();
        if (isShowContent) {
            parent.addView(mMaterialView.createMessageView(mMessage, isShowTitle));
            scrollLayout.addView(parent);
            mContentView = scrollLayout;
        }

        if (mItems != null) {
            if (mOnItemClickListener != null) {
                // Menu Dialog
                mMaterialView.mOnItemClickListener = mOnItemClickListener;
            } else if (mOnSingleClickListener != null) {
                // Single Menu Dialog
                mMaterialView.mOnSingleClickListener = mOnSingleClickListener;
            } else if (mOnMultipleClickListener != null) {
                // Multiple Menu Dialog
                mMaterialView.mOnMultipleClickListener = mOnMultipleClickListener;
            }

            // 设置ListView
            mContentView = mMaterialView.createListView(isShowTitle, isShowButton);
            ((ListView) mContentView).setAdapter(mMaterialView.createAdapter(mWhichType,
                    isShowTitle, mItems));

            // 设置IOS Item背景
            if (!mD.isMaterialDesign && mItems != null) {
                mD.mBackground = Utils.drawableOf(mActivity, android.R.color.transparent);
            }
        } else if (mIContentView != null) {
            // set a view for Dialog
            parent.addView(mIContentView.onCreateView(mD));
            scrollLayout.addView(parent);
            mContentView = scrollLayout;
        }

        // 添加 Title Layout
        if (isShowTitle) {
            decorView.addView(mMaterialView.createTitleLayout(mTitle, mIcon, mTitleColor));
        }

        // 添加 Content Layout
        if (mContentView != null) {
            // 添加ContentView
            decorView.addView(mContentView);

            // 设置IOS样式的列表选项
            if (!mD.isMaterialDesign && mItems != null) {
                Drawable drawable;
                if (isShowTitle) {
                    LinearLayout titleLayout = (LinearLayout) decorView.getChildAt(0);
                    titleLayout.setBackground(Utils.drawableOf(mActivity, mD.mBackgroundColor,
                            new float[]{mD.mBackgroundRadiusII[0], mD.mBackgroundRadiusII[1], 0, 0})
                    );

                    drawable = Utils.drawableOf(mActivity, mD.mBackgroundColor,
                            new float[]{0, 0, mD.mBackgroundRadiusII[0], mD.mBackgroundRadiusII[1]});
                } else {
                    drawable = Utils.drawableOf(mActivity, mD.mBackgroundColor,
                            mD.mBackgroundRadiusII);
                }
                mContentView.setBackground(drawable);

                // 添加取消按钮
                View cancelView = mMaterialView.createIOSCancelView();
                decorView.addView(cancelView);
            }
        }

        // 添加 Button Layout
        if (isShowButton) {
            // 添加分割线
            if (!mD.isMaterialDesign) {
                decorView.addView(mMaterialView.createLine(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }

            decorView.addView(mMaterialView.createButtonLayout(
                    mPositiveButtonText, mNegativeButtonText, mNeutralButtonText,
                    mPositiveClickListener, mNegativeClickListener, mNeutralClickListener
            ));
        }
    }

    // **************************************** 使用Builder模式 ********************************** //

    public static class Builder {

        private final AppCompatActivity mActivity;

        /**
         * dialog主题样式
         */
        private final int mThemeId;

        /**
         * dialog标题文本, 如果文本不为空, 显示标题, 否则不显示,
         * 可以通过 {@link #setTitle(int)} 或 {@link #setTitle(CharSequence)} 设置
         */
        private CharSequence mTitle;

        /**
         * dialog标题文本颜色, 默认使用灰黑色
         */
        private int mTitleColor;

        /**
         * dialog标题图标, 如果图标不为空, 显示图标, 否则不显示
         * 可以通过 {@link #setIcon(int)} 或 {@link #setIcon(Drawable)} 设置
         */
        private Drawable mIcon;

        /**
         * dialog内容文本, 如果文本不为空, 显示文本, 否则不显示, 可以通过
         * {@link #setMessage(int)} 或 {@link #setMessage(CharSequence)} 设置
         */
        private CharSequence mMessage;

        /**
         * 自定义View, 可以通过 {@link #setView(int)} 或 {@link #setView(View)} 设置
         */
        private View mView;

        /**
         * 如果设置了 {@link #mItems}, 需要自定义Adapter的话, 请使用 {@link #setAdapter(BaseAdapter)}
         */
        private BaseAdapter mAdapter;

        /**
         * Item 列表 可以通过 {@link #setItem(CharSequence...)} 或 {@link #setItem(int...)} 设置
         */
        private CharSequence[] mItems;

        /**
         * button文本, 可以通过 {@link #setPositiveClickListener(int, OnClickListener)} 或
         * {@link #setPositiveClickListener(CharSequence, OnClickListener)} 设置右边的按钮,
         * 通过 {@link #setNegativeClickListener(int, OnClickListener)} 或
         * {@link #setNegativeClickListener(CharSequence, OnClickListener) 设置左边的按钮},
         * 通过 {@link #setNeutralClickListener(int, OnClickListener)} 或
         * {@link #setNeutralClickListener(CharSequence, OnClickListener) 设置最左边的按钮},
         */
        private CharSequence mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;

        /**
         * button点击事件, @see {@link #mPositiveButtonText} 或 {@link #mNegativeButtonText} 或
         * {@link #mNeutralButtonText}
         */
        private OnClickListener mOnClickListener, mPositiveClickListener,
                mNegativeClickListener, mNeutralClickListener;

        /**
         * Item的类型
         */
        private int mWhichType;

        /**
         * Item点击事件, 可以通过 {@link #setOnItemClickListener(OnItemClickListener)} 设置
         */
        private OnItemClickListener mOnItemClickListener;

        /**
         * item单选点击事件，可以通过 {@link #setOnSingleClickListener(OnSingleClickListener)} 设置
         */
        private OnSingleClickListener mOnSingleClickListener;

        /**
         * item多选点击事件，可以通过 {@link #setOnMultipleClickListener(OnMultipleClickListener)} 设置
         */
        private OnMultipleClickListener onMultipleClickListener;

        /**
         * if use {@link DialogDelegate}, please implement {@link IContentView}, then setup by
         * {@link #setView(IContentView)}
         */
        private IContentView mIContentView;

        /**
         * @see DialogDelegate#isMaterialDesign
         */
        private boolean isMaterialDesign = true;

        /**
         * @see DialogDelegate#mMargin
         */
        private int mMargin = -1;

        /**
         * @see DialogDelegate#mGravity
         */
        private int mGravity = -1;

        /**
         * @see DialogDelegate#mAnimator
         */
        private int mAnimator = -1;

        /**
         * @see DialogDelegate#mBackground
         */
        private Drawable mBackground;

        /**
         * @see DialogDelegate#mBackgroundColor
         */
        private int mBackgroundColor = -1;

        /**
         * @see DialogDelegate#mBackgroundRadiusII
         */
        private float mBackgroundRadius = -1f;

        /**
         * @see DialogDelegate#mBackgroundRadiusII
         */
        private float[] mBackgroundRadiusII;

        /**
         * @see DialogDelegate#mDimAccount
         */
        private float mDimAccount = -1;

        /**
         * @see DialogDelegate#isTouchInOutSideCancel
         */
        private boolean isTouchInOutSideCancel = true;

        /**
         * @see DialogDelegate#isCancelable
         */
        private boolean isCancelable = true;

        /**
         * @see DialogDelegate#mPrimaryColor
         */
        private int mPrimaryColor = -1;

        /**
         * @see DialogDelegate#mSecondaryColor
         */
        private int mSecondaryColor = -1;

        /**
         * @see DialogDelegate#mButtonTextColor
         */
        private int mButtonTextColor = -1;

        public Builder(AppCompatActivity activity) {
            this(activity, -1);
        }

        public Builder(AppCompatActivity activity, int themeId) {
            mActivity = activity;
            mThemeId = themeId;
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setTitle(@StringRes int titleId) {
            mTitle = mActivity.getText(titleId);
            return this;
        }

        public Builder setTitleColor(@ColorInt int titleColor) {
            mTitleColor = titleColor;
            return this;
        }

        public Builder setIcon(@DrawableRes int icon) {
            mIcon = ContextCompat.getDrawable(mActivity, icon);
            return this;
        }

        public Builder setIcon(Drawable icon) {
            mIcon = icon;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            mMessage = mActivity.getText(messageId);
            return this;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setView(@LayoutRes int resId) {
            mView = LayoutInflater.from(mActivity).inflate(resId, null);
            return this;
        }

        public Builder setView(IContentView view) {
            mIContentView = view;
            return this;
        }

        public Builder setAdapter(BaseAdapter adapter) {
            mAdapter = adapter;
            return this;
        }

        public Builder setItem(CharSequence... item) {
            mItems = item;
            return this;
        }

        public Builder setItem(@StringRes int... item) {
            mItems = new CharSequence[item.length];
            for (int i = 0; i < item.length; i++) {
                mItems[i] = mActivity.getText(item[i]);
            }
            return this;
        }

        public Builder setButtonText(@StringRes int positiveButtonText) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            return this;
        }

        public Builder setButtonText(CharSequence positiveButtonText) {
            mPositiveButtonText = positiveButtonText;
            return this;
        }

        public Builder setButtonText(@StringRes int positiveButtonText,
                                     @StringRes int negativeButtonText) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            mNegativeButtonText = mActivity.getText(negativeButtonText);
            return this;
        }

        public Builder setButtonText(CharSequence positiveButtonText,
                                     CharSequence negativeButtonText) {
            mPositiveButtonText = positiveButtonText;
            mNegativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setButtonText(@StringRes int positiveButtonText,
                                     @StringRes int negativeButtonText,
                                     @StringRes int neutralButtonText) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            mNegativeButtonText = mActivity.getText(negativeButtonText);
            mNeutralButtonText = mActivity.getText(neutralButtonText);
            return this;
        }

        public Builder setButtonText(CharSequence positiveButtonText,
                                     CharSequence negativeButtonText,
                                     CharSequence neutralButtonText) {
            mPositiveButtonText = positiveButtonText;
            mNegativeButtonText = negativeButtonText;
            mNeutralButtonText = neutralButtonText;
            return this;
        }

        public Builder setOnClickListener(OnClickListener listener) {
            mOnClickListener = listener;
            return this;
        }

        public Builder setPositiveClickListener(CharSequence buttonText, OnClickListener listener) {
            mPositiveButtonText = buttonText;
            mPositiveClickListener = listener;
            return this;
        }

        public Builder setPositiveClickListener(@StringRes int buttonText, OnClickListener listener) {
            mPositiveButtonText = mActivity.getText(buttonText);
            mPositiveClickListener = listener;
            return this;
        }

        public Builder setNegativeClickListener(CharSequence buttonText, OnClickListener listener) {
            mNegativeButtonText = buttonText;
            mNegativeClickListener = listener;
            return this;
        }

        public Builder setNegativeClickListener(@StringRes int buttonText, OnClickListener listener) {
            mNegativeButtonText = mActivity.getText(buttonText);
            mNegativeClickListener = listener;
            return this;
        }

        public Builder setNeutralClickListener(CharSequence buttonText, OnClickListener listener) {
            mNeutralButtonText = buttonText;
            mNeutralClickListener = listener;
            return this;
        }

        public Builder setNeutralClickListener(@StringRes int buttonText, OnClickListener listener) {
            mNeutralButtonText = mActivity.getText(buttonText);
            mNeutralClickListener = listener;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            mWhichType = TYPE_ITEM;
            return this;
        }

        public Builder setOnSingleClickListener(OnSingleClickListener listener) {
            mOnSingleClickListener = listener;
            mWhichType = TYPE_SINGLE;
            return this;
        }

        public Builder setOnMultipleClickListener(OnMultipleClickListener listener) {
            onMultipleClickListener = listener;
            mWhichType = TYPE_MULTIPLE;
            return this;
        }

        public Builder setMaterialDesign(boolean materialDesign) {
            isMaterialDesign = materialDesign;
            return this;
        }


        public Builder setMargin(int margin) {
            mMargin = Utils.dp2px(margin);
            return this;
        }

        public Builder setGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public Builder setAnimator(int animator) {
            mAnimator = animator;
            return this;
        }

        public Builder setBackground(Drawable background) {
            mBackground = background;
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setBackgroundRadius(float backgroundRadius) {
            mBackgroundRadius = backgroundRadius;
            return this;
        }

        public Builder setBackgroundRadius(float[] backgroundRadiusII) {
            mBackgroundRadiusII = backgroundRadiusII;
            return this;
        }

        public Builder setDimAccount(float dimAccount) {
            mDimAccount = dimAccount;
            return this;
        }

        public Builder setTouchInOutSideCancel(boolean touchInOutSideCancel) {
            isTouchInOutSideCancel = touchInOutSideCancel;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            isCancelable = cancelable;
            return this;
        }

        public Builder setPrimaryColor(@ColorInt int primaryColor) {
            mPrimaryColor = primaryColor;
            return this;
        }

        public Builder setSecondaryColor(@ColorInt int secondaryColor) {
            mSecondaryColor = secondaryColor;
            return this;
        }

        public Builder setButtonTextColor(@ColorInt int buttonTextColor) {
            mButtonTextColor = buttonTextColor;
            return this;
        }

        public MaterialDialog create() {
            // 创建Material Dialog和dialog的内容
            MaterialDialog dialog = new MaterialDialog(mActivity);
            dialog.mTitle = mTitle;
            dialog.mTitleColor = mTitleColor;
            dialog.mIcon = mIcon;
            dialog.mMessage = mMessage;
            dialog.mContentView = mView;
            dialog.mAdapter = mAdapter;
            dialog.mItems = mItems;
            dialog.mPositiveButtonText = mPositiveButtonText;
            dialog.mNegativeButtonText = mNegativeButtonText;
            dialog.mNeutralButtonText = mNeutralButtonText;

            if (mPositiveClickListener == null && mNegativeClickListener == null
                    && mNeutralClickListener == null && mOnClickListener != null) {
                dialog.mPositiveClickListener = mOnClickListener;
                dialog.mNegativeClickListener = mOnClickListener;
                dialog.mNeutralClickListener = mOnClickListener;
            } else {
                dialog.mPositiveClickListener = mPositiveClickListener;
                dialog.mNegativeClickListener = mNegativeClickListener;
                dialog.mNeutralClickListener = mNeutralClickListener;
            }
            dialog.mWhichType = mWhichType;
            dialog.mOnItemClickListener = mOnItemClickListener;
            dialog.mOnSingleClickListener = mOnSingleClickListener;
            dialog.mOnMultipleClickListener = onMultipleClickListener;
            dialog.mIContentView = mIContentView;

            // 创建Dialog的属性设置
            DialogDelegate delegate = dialog.mD;
            if (mThemeId != -1) {
                delegate.mThemeId = mThemeId;
            }
            if (mMargin != -1) {
                delegate.mMargin = mMargin;
            }
            if (mGravity != -1) {
                delegate.mGravity = mGravity;
            } else if (!isMaterialDesign && mItems != null) {
                delegate.mGravity = Gravity.BOTTOM;
            }

            if (mAnimator != -1) {
                delegate.mAnimator = mAnimator;
            }
            int width = Utils.getDisplayWidth();
            delegate.mWidthInCenter = isMaterialDesign
                    ? (int) (width * DialogDelegate.MATERIAL_WIDTH)
                    : (int) (width * DialogDelegate.IOS_WIDTH);

            if (mBackgroundColor != -1) {
                delegate.mBackgroundColor = mBackgroundColor;
            }
            if (mBackground != null) {
                delegate.mBackground = mBackground;
            }
            if (mBackgroundRadiusII == null) {
                float rad = mBackgroundRadius == -1f
                        ? isMaterialDesign
                        ? DialogDelegate.MATERIAL_RADIUS
                        : DialogDelegate.DEFAULT_RADIUS
                        : mBackgroundRadius;
                delegate.mBackgroundRadiusII = new float[]{rad, rad, rad, rad};
            } else {
                delegate.mBackgroundRadiusII = mBackgroundRadiusII;
            }
            delegate.mDimAccount = mDimAccount;
            delegate.isTouchInOutSideCancel = isTouchInOutSideCancel;
            delegate.isCancelable = isCancelable;
            delegate.isMaterialDesign = isMaterialDesign;

            if (mPrimaryColor != -1) {
                delegate.mPrimaryColor = mPrimaryColor;
            }
            if (mSecondaryColor != -1) {
                delegate.mSecondaryColor = mSecondaryColor;
            }
            if (mButtonTextColor != -1) {
                delegate.mButtonTextColor = mButtonTextColor;
            }

            return dialog;
        }
    }

    /**
     * @see Builder#mPositiveClickListener or
     * @see Builder#mNegativeClickListener
     */
    public abstract static class OnClickListener {
        /**
         * @param dialog 当前显示的Dialog
         * @param which  通过这个判断点击的是哪个按钮
         */
        public void onClick(MaterialDialog dialog, int which) {
        }
    }

    /**
     * @see Builder#mOnItemClickListener
     */
    public interface OnItemClickListener {
        /**
         * @param dialog 当前dialog
         * @param which  通过这个判断点击的位置
         */
        void onClick(MaterialDialog dialog, int which);
    }

    /**
     * @see Builder#mOnSingleClickListener
     */
    public interface OnSingleClickListener {
        /**
         * @param dialog    当前dialog
         * @param which     通过这个判断点击的位置
         * @param isChecked 当前点击的选项是否选中
         */
        void onClick(MaterialDialog dialog, int which, boolean isChecked);
    }

    /**
     * @see Builder#onMultipleClickListener
     */
    public interface OnMultipleClickListener {
        /**
         * @param dialog    当前dialog
         * @param which     通过这个判断点击的位置
         * @param isChecked 当前点击的选项是否选中
         */
        void onClick(MaterialDialog dialog, int which, boolean isChecked);
    }

    /**
     * @see Builder#mIContentView
     */
    public interface IContentView {

        /**
         * create a content view for {@link MaterialDialog}
         *
         * @param delegate set or get some property
         * @return a view
         */
        View onCreateView(DialogDelegate delegate);
    }
}
