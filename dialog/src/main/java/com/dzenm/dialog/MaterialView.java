package com.dzenm.dialog;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import java.util.Arrays;
import java.util.List;

/**
 * @author dzenm
 * @date 2020/4/9 15:13
 * @IDE Android Studio
 */
public class MaterialView {

    /**
     * add dialog in click listener, @see {@link MaterialDialog.OnClickListener} or
     * {@link MaterialDialog.OnItemClickListener} or {@link MaterialDialog.OnSingleClickListener}
     * or {@link MaterialDialog.OnMultipleClickListener}
     */
    private final MaterialDialog mMaterialDialog;

    /**
     * get some property by delegate, @see {@link DialogDelegate}
     */
    private final DialogDelegate mD;

    /**
     * dialog fragment' s activity
     */
    private final Activity mActivity;

    MaterialDialog.OnItemClickListener mOnItemClickListener;

    MaterialDialog.OnSingleClickListener mOnSingleClickListener;

    MaterialDialog.OnMultipleClickListener mOnMultipleClickListener;

    MaterialView(MaterialDialog dialog, DialogDelegate delegate, Activity activity) {
        mMaterialDialog = dialog;
        mD = delegate;
        mActivity = activity;
    }

    // **************************************** 创建Title *************************************** //

    ViewGroup createTitleLayout(CharSequence title, Drawable icon, int titleColor) {
        boolean isMaterial = mD.isMaterialDesign;

        LinearLayout titleLayout = new LinearLayout(mActivity);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        boolean isShowIcon = TextUtils.isEmpty(title) && icon != null;
        titleLayout.setPadding(Utils.dp2px(24), Utils.dp2px(isShowIcon ? 24 : 20),
                Utils.dp2px(24), Utils.dp2px(isMaterial ? 8 : isShowIcon ? 0 : 16));

        // set android and ios style
        titleLayout.setGravity(isMaterial ? Gravity.CENTER_VERTICAL : Gravity.CENTER);
        titleLayout.setOrientation(isMaterial ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

        // create title view
        TextView titleView = new TextView(mActivity);
        titleView.setTextSize(16);
        titleView.setTextColor(titleColor == 0 ? mD.mPrimaryTextColor : titleColor);
        if (!mD.isMaterialDesign) titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        titleView.setText(title);

        if (icon != null) {
            ImageView iconImage = new ImageButton(mActivity);
            int iconSize = Utils.dp2px(isMaterial ? 32 : 48);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
            if (isMaterial) params.rightMargin = Utils.dp2px(8);
            if (!isMaterial && !TextUtils.isEmpty(title)) params.bottomMargin = Utils.dp2px(8);
            iconImage.setLayoutParams(params);
            iconImage.setPadding(0, 0, 0, 0);

            iconImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconImage.setImageDrawable(icon);
            iconImage.setBackground(null);
            titleLayout.addView(iconImage);
        }
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleLayout.addView(titleView);
        return titleLayout;
    }

    // **************************************** 创建Content ViewGroup *************************** //

    ViewGroup createScrollLayout() {
        NestedScrollView nestedScrollView = new NestedScrollView(mActivity);
        nestedScrollView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return nestedScrollView;
    }

    ViewGroup createContentLayout() {
        // 设置content view, 最大高度
        FrameLayout contentLayout = new FrameLayout(mActivity);
        contentLayout.setLayoutParams(new NestedScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return contentLayout;
    }

    TextView createTextView() {
        TextView textView = new TextView(mActivity);
        textView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        return textView;
    }

    // **************************************** 创建Content ************************************** //

    /**
     * 创建显示一个文本的View
     *
     * @param message     显示的文本
     * @param isShowTitle 是否显示顶部的标题
     * @return MessageView
     */
    View createMessageView(CharSequence message, boolean isShowTitle) {
        TextView messageView = createTextView();
        int top = Utils.dp2px(isShowTitle ? 0 : mD.isMaterialDesign ? 20 : 24);
        int h = Utils.dp2px(24);
        messageView.setPadding(h, top, h, h);
        messageView.setText(message);
        messageView.setGravity(mD.isMaterialDesign ? Gravity.START : Gravity.CENTER_HORIZONTAL);
        messageView.setTextColor(mD.mSecondaryTextColor);
        return messageView;
    }

    /**
     * 创建显示列表的ListView
     *
     * @param isShowTitle  是否显示顶部的标题
     * @param isShowButton 是否显示底部的按钮
     * @return ListView
     */
    ListView createListView(boolean isShowTitle, boolean isShowButton) {
        ListView listView = new ListView(mD.mActivity);
        listView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        // if dialog show title or show button, add padding in top or bottom
        if (mD.isMaterialDesign) {
            listView.setPadding(0, Utils.dp2px(isShowTitle ? 0 : 4),
                    0, Utils.dp2px(isShowButton ? 0 : 4));
        }
        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);
        return listView;
    }

    /**
     * 创建ListView显示的适配器
     *
     * @param which       列表的类型, 1. 文本列表, 2. 单选列表, 3. 多选列表
     * @param isShowTitle 是否显示顶部的标题
     * @param items       列表选项内容
     * @return 适配器
     */
    BaseAdapter createAdapter(final int which, final boolean isShowTitle, final CharSequence[] items) {
        return new ListAdapter<CharSequence>(Arrays.asList(items)) {

            // IOS样式选中的图标
            private final Drawable selected = ContextCompat.getDrawable(
                    mActivity, R.drawable.ic_selected);
            // IOS样式未选中的图标
            private final Drawable unselected = ContextCompat.getDrawable(
                    mActivity, R.drawable.ic_unselected);

            @Override
            public View createView(CharSequence data, int position) {
                return mD.isMaterialDesign
                        ? createMaterialItemView(data, position)
                        : createIOSItemView(data, position);
            }

            private View createMaterialItemView(CharSequence data, final int position) {
                TextView itemView;
                if (which == MaterialDialog.TYPE_SINGLE) {
                    itemView = new CheckedTextView(mActivity);
                    itemView.setCompoundDrawablesWithIntrinsicBounds(Utils.resolveDrawable(mActivity,
                            android.R.attr.listChoiceIndicatorSingle), null, null, null
                    );
                    itemView.setCompoundDrawablePadding(Utils.dp2px(8));
                } else if (which == MaterialDialog.TYPE_MULTIPLE) {
                    itemView = new CheckedTextView(mActivity);
                    itemView.setCompoundDrawablesWithIntrinsicBounds(Utils.resolveDrawable(mActivity,
                            android.R.attr.listChoiceIndicatorMultiple), null, null, null
                    );
                    itemView.setCompoundDrawablePadding(Utils.dp2px(8));
                } else {
                    itemView = new TextView(mActivity);
                }

                itemView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                int v = Utils.dp2px(which != MaterialDialog.TYPE_ITEM ? 8 : 14);
                int h = Utils.dp2px(24);
                itemView.setPadding(h, v, h, v);

                itemView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                itemView.setBackground(getItemBackground(isShowTitle, itemViews.length, position));
                itemView.setTextColor(mD.mPrimaryTextColor);
                itemView.setText(data);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setMaterialCheckedStatus(view, position);
                            }
                        }, 250);
                    }
                });
                return itemView;
            }

            private void setMaterialCheckedStatus(View view, int position) {
                if (which == MaterialDialog.TYPE_ITEM) {
                    mOnItemClickListener.onClick(mMaterialDialog, position);
                } else {
                    CheckedTextView checkedTextView = (CheckedTextView) view;
                    if (which == MaterialDialog.TYPE_SINGLE) {
                        for (int i = 0; i < itemViews.length; i++) {
                            if (position != i) {
                                ((CheckedTextView) itemViews[i]).setChecked(false);
                            }
                            checkable[i] = position == i;
                        }
                        checkedTextView.setChecked(true);
                        mOnSingleClickListener.onClick(mMaterialDialog, position,
                                checkedTextView.isChecked());
                    } else if (which == MaterialDialog.TYPE_MULTIPLE) {
                        checkable[position] = !checkable[position];
                        checkedTextView.toggle();
                        mOnMultipleClickListener.onClick(mMaterialDialog, position,
                                checkedTextView.isChecked());
                    }
                }
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                }, 250);
            }

            private View createIOSItemView(CharSequence data, final int position) {
                LinearLayout parent = new LinearLayout(mActivity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                );
                parent.setLayoutParams(params);
                parent.setOrientation(LinearLayout.VERTICAL);

                TextView itemView = createIOSItemTextView(data, isShowTitle, itemViews.length, position);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setIOSCheckedStatus(view, position);
                            }
                        }, 100);
                    }
                });
                // add divide
                if (position == 0 && isShowTitle) {
                    parent.addView(addDivideView());
                } else if (position <= itemViews.length - 1) {
                    parent.addView(addDivideView());
                }
                parent.addView(itemView);
                return parent;
            }

            private void setIOSCheckedStatus(View view, int position) {
                if (which == MaterialDialog.TYPE_ITEM) {
                    mOnItemClickListener.onClick(mMaterialDialog, position);
                } else {
                    checkable[position] = !checkable[position];
                    boolean isChecked = checkable[position];
                    TextView textView = (TextView) view;
                    textView.setCompoundDrawablesWithIntrinsicBounds(
                            unselected, null, isChecked ? selected : unselected, null);
                    if (which == MaterialDialog.TYPE_SINGLE) {
                        for (int j = 0; j < itemViews.length; j++) {
                            if (position != j) {
                                checkIOSItemView(itemViews[j], false);
                                checkable[j] = false;
                            }
                        }
                        mOnSingleClickListener.onClick(mMaterialDialog, position, isChecked);
                    } else if (which == MaterialDialog.TYPE_MULTIPLE) {
                        mOnMultipleClickListener.onClick(mMaterialDialog, position, isChecked);
                    }
                }
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                }, 100);
            }

            @Override
            public void bindData(int position, View view) {
                // 刷新数据后的回显内容
                if (which == MaterialDialog.TYPE_SINGLE || which == MaterialDialog.TYPE_MULTIPLE) {
                    boolean isChecked = checkable[position];
                    if (mD.isMaterialDesign) {
                        CheckedTextView checkedTextView = (CheckedTextView) view;
                        checkedTextView.setChecked(isChecked);
                    } else {
                        checkIOSItemView(view, isChecked);
                    }
                }
            }

            private void checkIOSItemView(View view, boolean isChecked) {
                LinearLayout parent = (LinearLayout) view;
                int child = parent.getChildCount() == 2 ? 1 : 0;
                TextView textView = (TextView) parent.getChildAt(child);
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        unselected, null, isChecked ? selected : unselected, null);
            }
        };
    }

    /**
     * create a cancel view for ios style list item
     *
     * @return a cancel view
     */
    ViewGroup createIOSCancelView() {
        FrameLayout cancelLayout = new FrameLayout(mActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = Utils.dp2px(8);
        cancelLayout.setLayoutParams(params);
        cancelLayout.setBackground(Utils.drawableOf(mActivity, mD.mBackgroundColor,
                mD.mBackgroundRadiusII));

        TextView cancelView = createIOSItemTextView("取消", false, 1, 0);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        cancelView.setTextColor(Utils.getColor(mActivity, android.R.color.holo_red_light));

        cancelLayout.addView(cancelView);
        return cancelLayout;
    }

    // **************************************** 创建Button ************************************** //

    LinearLayout createButtonLayout(CharSequence positiveText, CharSequence negativeText,
                                    CharSequence neutralText,
                                    MaterialDialog.OnClickListener positiveListener,
                                    MaterialDialog.OnClickListener negativeListener,
                                    MaterialDialog.OnClickListener neutralListener
    ) {
        LinearLayout buttonLayout = new LinearLayout(mActivity);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dp2px(mD.isMaterialDesign ? 50 : 46)
        ));
        if (mD.isMaterialDesign) {
            buttonLayout.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            int padding = Utils.dp2px(8);
            buttonLayout.setPadding(padding, padding, padding, padding);
        }

        boolean isSingleButton = !TextUtils.isEmpty(positiveText)
                && TextUtils.isEmpty(negativeText)
                && TextUtils.isEmpty(neutralText);
        if (!TextUtils.isEmpty(neutralText) && mD.isMaterialDesign) {
            buttonLayout.addView(createButton(isSingleButton, neutralText, neutralListener,
                    MaterialDialog.BUTTON_NEUTRAL));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1
            );
            View empty = new View(mActivity);
            empty.setLayoutParams(params);
            buttonLayout.addView(empty);
        }

        // 根据positive button和negative button文本不为空时添加按钮
        if (!TextUtils.isEmpty(negativeText)) {
            buttonLayout.addView(createButton(isSingleButton, negativeText, negativeListener,
                    MaterialDialog.BUTTON_NEGATIVE
            ));
            if (!mD.isMaterialDesign) {
                buttonLayout.addView(createLine(1, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        if (!TextUtils.isEmpty(positiveText)) {
            buttonLayout.addView(createButton(isSingleButton, positiveText, positiveListener,
                    MaterialDialog.BUTTON_POSITIVE
            ));
        }
        return buttonLayout;
    }

    /**
     * @param isSingleButton 是否显示一个Button
     * @param which          {@link MaterialDialog#BUTTON_NEUTRAL} or
     *                       {@link MaterialDialog#BUTTON_NEGATIVE} or
     *                       {@link MaterialDialog#BUTTON_POSITIVE}
     * @param buttonText     current button text
     * @param listener       button click listener
     * @return a button
     */
    private TextView createButton(boolean isSingleButton, CharSequence buttonText,
                                  final MaterialDialog.OnClickListener listener, final int which) {
        TextView button = new TextView(mActivity);
        LinearLayout.LayoutParams params;
        if (mD.isMaterialDesign) {
            // 设置Button大小
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT
            );
            if (which == MaterialDialog.BUTTON_POSITIVE) {
                params.leftMargin = Utils.dp2px(8);
            }
            // 设置button内边距, 设置点击背景颜色
            button.setMinWidth(Utils.dp2px(56));
            int paddingH = Utils.dp2px(8);
            button.setPadding(paddingH, 0, paddingH, 0);
            Drawable drawable;
            if (mD.isMaterialDesign) {
                drawable = Utils.ripple(mActivity, mD.mActiveColor,
                        mD.mInactiveColor, mD.mBackgroundRadiusII);
            } else {
                drawable = Utils.pressed(mActivity, mD.mActiveColor,
                        mD.mInactiveColor, mD.mBackgroundRadiusII);
            }
            button.setBackground(drawable);
        } else {
            // 设置Button大小
            params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            // 设置点击背景颜色
            float[] radius;
            if (which == MaterialDialog.BUTTON_POSITIVE) {
                if (isSingleButton) {
                    radius = new float[]{0, 0, mD.mBackgroundRadiusII[2], mD.mBackgroundRadiusII[3]};
                } else {
                    radius = new float[]{0, 0, mD.mBackgroundRadiusII[2], 0};
                }
            } else {
                radius = new float[]{0, 0, 0, mD.mBackgroundRadiusII[3]};
            }

            Drawable drawable;
            if (mD.isMaterialDesign) {
                drawable = Utils.ripple(mActivity, mD.mActiveColor, mD.mInactiveColor, radius);
            } else {
                drawable = Utils.pressed(mActivity, mD.mActiveColor, mD.mInactiveColor, radius);
            }
            button.setBackground(drawable);
        }
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);
        button.setText(buttonText);
        button.setTextColor(mD.mButtonTextColor);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(mMaterialDialog, which);
                }
            }
        });
        return button;
    }

    // **************************************** 创建Line ***************************************** //

    View createLine(int width, int height) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        View line = new View(mActivity);
        line.setBackgroundColor(mD.mDivideColor);
        line.setLayoutParams(params);
        return line;
    }

    // **************************************** 辅助方法 ***************************************** //

    private static abstract class ListAdapter<T> extends BaseAdapter {

        final List<T> data;
        final boolean[] checkable;
        final View[] itemViews;

        private ListAdapter(List<T> data) {
            this.data = data;
            checkable = new boolean[data.size()];
            itemViews = new View[data.size()];
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (view == null) {
                view = createView(data.get(position), position);
                itemViews[position] = view;
            } else {
                view = convertView;
            }
            bindData(position, view);
            return view;
        }

        public abstract View createView(T data, int position);

        public abstract void bindData(int position, View view);
    }

    private Drawable getItemBackground(boolean isShowTitle, int len, int position) {
        return Utils.ripple(mActivity, mD.mActiveColor, mD.mInactiveColor,
                getItemRadius(isShowTitle, len, position));
    }

    private Drawable getIOSItemBackground(boolean isShowTitle, int len, int position) {
        return Utils.pressed(mActivity, mD.mActiveColor, mD.mInactiveColor,
                getItemRadius(isShowTitle, len, position));
    }

    private TextView createIOSItemTextView(CharSequence data, boolean isShowTitle, int len,
                                           int position) {
        TextView itemView = new TextView(mActivity);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        int v = Utils.dp2px(16);
        int h = Utils.dp2px(24);
        itemView.setPadding(h, v, h, v);
        itemView.setGravity(Gravity.CENTER);
        itemView.setText(data);
        itemView.setTextColor(mD.mButtonTextColor);
        itemView.setBackground(getIOSItemBackground(isShowTitle, len, position));
        return itemView;
    }

    /**
     * 设置点击的背景圆角效果
     *
     * @param len      list长度
     * @param position 位置
     * @return 圆角
     */
    private float[] getItemRadius(boolean isShowTitle, int len, int position) {
        if (mD.isMaterialDesign) {
            return new float[]{0, 0, 0, 0};
        } else if (isShowTitle) {
            if (position == len - 1) {
                // Item length is one
                return new float[]{0, 0, mD.mBackgroundRadiusII[2], mD.mBackgroundRadiusII[3]};
            } else {
                // if item length is not one, and current item is not first or last
                return new float[]{0, 0, 0, 0};
            }
        } else {
            if (len == 1) {
                // Item length is one
                return new float[]{mD.mBackgroundRadiusII[0], mD.mBackgroundRadiusII[1],
                        mD.mBackgroundRadiusII[2], mD.mBackgroundRadiusII[3]};
            } else if (position == 0 && len > 1) {
                // if item length is not one, and current item is first
                return new float[]{mD.mBackgroundRadiusII[0], mD.mBackgroundRadiusII[1], 0, 0};
            } else if (position == len - 1 && len > 1) {
                // if item length is not one, and current item is last
                return new float[]{0, 0, mD.mBackgroundRadiusII[2], mD.mBackgroundRadiusII[3]};
            } else {
                // if item length is not one, and current item is not first or last
                return new float[]{0, 0, 0, 0};
            }
        }
    }

    /**
     * @return divide view
     */
    private View addDivideView() {
        View view = new View(mActivity);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(mD.mDivideColor);
        return view;
    }
}
