package com.dzenm.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    // **************************************** 创建按钮效果Drawable ***************************** //

    public static Drawable pressed(Context context, int normalColor,
                                   int pressedColor, float radius) {
        return pressed(context, normalColor, pressedColor, new float[]{radius, radius, radius, radius});
    }

    public static Drawable pressed(Context context, int normalColor,
                                   int pressedColor, float[] radius) {
        Drawable normalDrawable = drawableOf(context, normalColor, radius);
        Drawable pressedDrawable = drawableOf(context, pressedColor, radius);

        StateListDrawable drawable = new StateListDrawable();
        // 状态为true的背景
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        // 状态为false的背景
        drawable.addState(new int[]{-android.R.attr.state_pressed}, normalDrawable);
        return drawable;
    }

    public static Drawable ripple(Context context, int normalColor,
                                  int rippleColor, float radius) {
        return ripple(context, normalColor, rippleColor, new float[]{radius, radius, radius, radius});
    }

    public static Drawable ripple(Context context, int normalColor,
                                  int rippleColor, float[] radius) {
        return new RippleDrawable(Utils.getColorStateList(context, rippleColor),
                drawableOf(context, normalColor, radius), null);
    }

    // **************************************** 创建普通Drawable ********************************* //

    public static Drawable drawableOf(Context context, float radius) {
        return drawableOf(context, android.R.color.transparent, new float[]{radius, radius, radius, radius});
    }

    public static Drawable drawableOf(Context context, float[] radius) {
        return drawableOf(context, android.R.color.transparent, radius);
    }

    public static Drawable drawableOf(Context context, int color) {
        return drawableOf(context, color, new float[]{0f, 0f, 0f, 0f});
    }

    public static Drawable drawableOf(Context context, int color, float radius) {
        return drawableOf(context, color, new float[]{radius, radius, radius, radius});
    }

    public static Drawable drawableOf(Context context, int color, float[] radius) {
        GradientDrawable drawable = new GradientDrawable();
        // 设置形状
        drawable.setShape(GradientDrawable.RECTANGLE);
        // 设置背景颜色
        drawable.setColor(getColor(context, color));
        // 设置圆角
        float[] radII = new float[]{
                dp2px(radius[0]), dp2px(radius[0]), dp2px(radius[1]), dp2px(radius[1]),
                dp2px(radius[2]), dp2px(radius[2]), dp2px(radius[3]), dp2px(radius[3])
        };
        drawable.setCornerRadii(radII);
        // 扫描渐变和辐射渐变添加这个属性会不起作用
        drawable.setUseLevel(true);
        return drawable;
    }

    // **************************************** 资源文件转换为Int ********************************* //

    /**
     * 获取颜色值, 可以是res/attr下的属性值，也可以是res/color的颜色值
     *
     * @param context 上下文
     * @param color   颜色值, 可以是ColorRes, 也可以是ColorInt, 还可以是AttrRes
     * @return ColorInt颜色值
     */
    public static int getColor(@NonNull Context context, int color) {
        Log.d(TAG, "resolve color origin: " + color);
        if (color > 0) {
            try {
                Log.d(TAG, "resolve color int: " + context.getResources().getColor(color));
                return context.getResources().getColor(color);
            } catch (Exception e) {
                int attrColor = resolveColor(context, color);
                Log.d(TAG, "resolve color attr: " + attrColor);
                if (attrColor != 0) {
                    Log.d(TAG, "resolve color attr: " + attrColor);
                    return attrColor;
                }
            }
        }
        Log.d(TAG, "resolve color origin: " + color);
        return color;
    }

    /**
     * 获取 res/attr/ 文件的颜色值
     *
     * @param context 上下文
     * @param attrRes 颜色值
     * @return 属性对应的颜色值
     */
    public static int resolveColor(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取 res/attr/ 下文件的Drawable
     *
     * @param context 上下文
     * @param attrRes 属性值
     * @return 属性对应的Drawable对象
     */
    public static Drawable resolveDrawable(@NonNull Context context, @AttrRes int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = a.getDrawable(0);
            } else {
                int id = a.getResourceId(0, -1);
                if (id == -1) {
                    drawable = AppCompatResources.getDrawable(context, id);
                }
            }
            return drawable;
        } finally {
            a.recycle();
        }
    }

    /**
     * 获取ColorStateList值，可以是res/attr下的属性值，也可以是res/color的颜色值
     *
     * @param context 上下文
     * @param color   颜色id值
     * @return 单色的ColorStateList
     */
    public static ColorStateList getColorStateList(@NonNull Context context, int color) {
        TypedArray a = context.obtainStyledAttributes(new int[]{color});
        try {
            final TypedValue value = new TypedValue();
            context.getResources().getValue(color, value, true);
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return getActionTextStateList(context, value.data);
            } else {
                return context.getResources().getColorStateList(color, null);
            }
        } catch (Exception e) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, new int[]{}
            };
            // ColorStateList只接受ColorInt类型的颜色, 因此这里通过getColor方法将属性或者ColorRes或者ColorInt
            // 类型的颜色转换成ColorInt类型
            int colorInt = getColor(context, color);
            int[] colors = new int[]{colorInt, colorInt};
            return new ColorStateList(states, colors);
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList getActionTextStateList(@NonNull Context context, int newPrimaryColor) {
        final int fallBackButtonColor = resolveColor(context, android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor;
        }
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, new int[]{}
        };

        int[] colors = new int[]{adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor};
        return new ColorStateList(states, colors);
    }

    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    // **************************************** 屏幕工具 ***************************************** //

    /**
     * @param value 转换的值
     * @return 转换的dip值
     */
    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * @return 屏幕宽度
     */
    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * @return 屏幕高度
     */
    public static int getHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * @return 屏幕宽度, 不包含NavigatorBar
     */
    public static int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * @return 屏幕宽度, 不包含NavigatorBar
     */
    public static int getDisplayHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    // **************************************** 动画资源文件 ************************************* //

    /**
     * 放大
     *
     * @return res/values/style
     */
    public static int expand() {
        return R.style.BaseDialog_Scale_Expand_Animator;
    }

    /**
     * 缩小
     *
     * @return res/values/style
     */
    public static int shrink() {
        return R.style.BaseDialog_Scale_Shrink_Animator;
    }

    /**
     * 下进下出
     *
     * @return res/values/style
     */
    public static int bottom() {
        return R.style.BaseDialog_Bottom_Animator;
    }

    /**
     * 下进上出
     *
     * @return res/values/style
     */
    public static int bottom2Top() {
        return R.style.BaseDialog_Bottom_Top_Animator;
    }

    /**
     * 上进上出
     *
     * @return res/values/style
     */
    public static int top() {
        return R.style.BaseDialog_Top_Animator;
    }

    /**
     * 上进下出
     *
     * @return res/values/style
     */
    public static int top2Bottom() {
        return R.style.BaseDialog_Top_Bottom_Animator;
    }

    /**
     * 左进左出
     *
     * @return res/values/style
     */
    public static int left() {
        return R.style.BaseDialog_Left_Animator;
    }

    /**
     * 右进右出
     *
     * @return res/values/style
     */
    public static int right() {
        return R.style.BaseDialog_Right_Animator;
    }

    /**
     * 左进右出
     *
     * @return res/values/style
     */
    public static int left2Right() {
        return R.style.BaseDialog_Left_Right_Animator;
    }

    /**
     * 右进左出
     *
     * @return res/values/style
     */
    public static int right2Left() {
        return R.style.BaseDialog_Right_Left_Animator;
    }

    /**
     * 下弹出上弹出
     *
     * @return res/values/style
     */
    public static int overshoot() {
        return R.style.BaseDialog_Overshoot_Animator;
    }

    /**
     * 透明度变化
     *
     * @return res/values/style
     */
    public static int alpha() {
        return R.style.BaseDialog_Alpha_Animator;
    }

    /**
     * 回弹效果
     *
     * @return res/values/style
     */
    public static int rebound() {
        return R.style.BaseDialog_Rebound_Animator;
    }

    /**
     * 开始播放Drawable的动画
     *
     * @param drawable R/drawable下的文件，如果已经设置了drawable，使用imageView.getDrawable()获取
     */
    public static void play(Drawable drawable) {
        if (drawable instanceof Animatable) ((Animatable) drawable).start();
    }
}
