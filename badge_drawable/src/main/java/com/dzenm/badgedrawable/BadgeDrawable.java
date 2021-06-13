package com.dzenm.badgedrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 * Bitmap bitmap = new BadgeDrawable.Builder(getApplicationContext())
 *        .setDrawable(drawableResId)
 *        .setCircle(isCircle)
 *        .setInner(isInner)
 *        .setNumber(number)
 *        .setBadgePosition(positionBadge)
 *        .build();
 * imageViewBadge.setImageBitmap(bitmap);
 * or
 * new BadgeDrawable.Builder(getApplicationContext())
 *                 .setDrawable(drawableResId)
 *        .setCircle(isCircle)
 *        .setNumber(number)
 *        .setBadgeBorderSize(badgeBorderSize)
 *        .setBadgePosition(positionBadge)
 *        .build(imageViewBadge);
 * </pre>
 */
public class BadgeDrawable {

    private static final int DEFAULT_COUNT = 0;
    private static final int MAXIMUM_COUNT = 99;

    private static final float DEFAULT_BADGE_SIZE = dp2px(20);
    private static final float DEFAULT_BADGE_CIRCLE_SIZE = dp2px(16);
    private static final float DEFAULT_BORDER_SIZE = dp2px(4);

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    /**
     * Badge文本颜色, Badge背景颜色, Badge边框颜色
     */
    @ColorInt
    private int mTextColor, mBadgeColor, mBadgeBorderColor;

    /**
     * Badge所在的位置
     */
    @BadgePosition
    private int mBadgePosition = BadgePosition.TOP_RIGHT;

    /**
     * Badge大小, Badge边框大小
     */
    private float mBadgeSize, mBadgeBorderSize;

    /**
     * Badge显示的数量, 默认为{@link #DEFAULT_COUNT}, 只显示红点, 当大于0时, 显示具体的数字,
     * 默认当大于 {@link #MAXIMUM_COUNT} 时, 通过 {@link #mMaximumNumber} 可以设置最大显示的数量
     */
    private int mNumber = DEFAULT_COUNT, mMaximumNumber = MAXIMUM_COUNT;

    /**
     * {@link #isCircle} 为false时, 显示为椭圆形, 为true时, 显示为圆形
     */
    private boolean isCircle;

    @IntDef({
            BadgePosition.TOP_LEFT,
            BadgePosition.TOP_RIGHT,
            BadgePosition.BOTTOM_LEFT,
            BadgePosition.BOTTOM_RIGHT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BadgePosition {
        int TOP_LEFT = 1;
        int TOP_RIGHT = 2;
        int BOTTOM_LEFT = 3;
        int BOTTOM_RIGHT = 4;
    }

    private BadgeDrawable(Context context) {
        mContext = context;
        mTextColor = getColor(android.R.color.white);
        mBadgeColor = getColor(android.R.color.holo_red_light);
        mBadgeBorderColor = getColor(android.R.color.white);
        isCircle = false;
        if (isCircle) {
            mBadgeSize = DEFAULT_BADGE_CIRCLE_SIZE;
        } else {
            mBadgeSize = DEFAULT_BADGE_SIZE;
        }
        mBadgeBorderSize = DEFAULT_BORDER_SIZE;
    }

    private int getNumber() {
        return mNumber;
    }

    private Bitmap buildBitmap(Bitmap oldBitmap) {
        // 计算显示的文本内容
        mMaximumNumber = MAXIMUM_COUNT;
        String numberText = mNumber > mMaximumNumber ? mMaximumNumber + "+" : String.valueOf(mNumber);

        // 计算显示文本大小和区域
        Rect textBounds = new Rect();
        // 计算数量文本大小所占百分比, 计算数量文本的基值
        float numberTextSize = 0.6f * (mBadgeSize - mBadgeBorderSize);
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(numberTextSize);
        textPaint.getTextBounds(numberText, 0, numberText.length(), textBounds);

        // 测量Badge的宽高
        float badgeWidth, badgeHeight;
        if (isDefaultCount()) {         // 数字为0, 显示小圆点
            badgeWidth = badgeHeight = dp2px(10);
        } else if (isCircle) {          // 圆形Badge, 宽高相等
            badgeWidth = badgeHeight = mBadgeSize;
        } else if (isDigits()) {        // 数字为个位数, 宽高相等
            badgeWidth = badgeHeight = textBounds.height() + dp2px(8);
        } else {                        // 椭圆形Badge, 以数量文字大小+6
            badgeHeight = textBounds.height() + dp2px(8);
            badgeWidth = textBounds.width() + dp2px(8);
        }

        // 测量Badge的所在的位置
        int width = oldBitmap.getWidth(), height = oldBitmap.getHeight();
        RectF badgeRect = null;
        if (mBadgePosition == BadgePosition.TOP_RIGHT) {
            badgeRect = new RectF(width - badgeWidth,
                    0,
                    width,
                    badgeHeight);
        } else if (mBadgePosition == BadgePosition.BOTTOM_RIGHT) {
            badgeRect = new RectF(width - badgeWidth,
                    height - badgeHeight,
                    width,
                    height);
        } else if (mBadgePosition == BadgePosition.TOP_LEFT) {
            badgeRect = new RectF(0,
                    0,
                    badgeWidth,
                    badgeHeight);
        } else if (mBadgePosition == BadgePosition.BOTTOM_LEFT) {
            badgeRect = new RectF(0,
                    height - badgeHeight,
                    badgeWidth,
                    height);
        }

        // 创建带有Badge的Bitmap
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);

        // 绘制原图片
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(mBadgeColor);

        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(oldBitmap, rect, rect, paint);

        // 绘制Badge的边框
        if (mBadgeBorderSize > 0) {
            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setFilterBitmap(true);
            borderPaint.setDither(true);
            borderPaint.setTextAlign(Paint.Align.CENTER);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(mBadgeBorderColor);
            borderPaint.setStrokeWidth(mBadgeBorderSize);
            drawBackground(canvas, badgeRect, borderPaint);
        }

        // 绘制Badge的背景
        drawBackground(canvas, badgeRect, paint);

        // 绘制Badge的文本
        if (!isDefaultCount()) {
            float x = badgeRect.centerX() - (textPaint.measureText(numberText) * 0.5f);
            float y = badgeRect.centerY() - (textPaint.ascent() + textPaint.descent()) * 0.5f;
            canvas.drawText(numberText, x, y, textPaint);
        }
        return newBitmap;
    }

    /**
     * 绘制Badge背景
     */
    private void drawBackground(Canvas canvas, RectF badgeRect, Paint paint) {
        if (isCircle || isDigits()) {   // 是否绘制椭圆的Badge
            canvas.drawOval(badgeRect, paint);
        } else {
            // 第二个参数是x半径, 第三个参数是y半径
            canvas.drawRoundRect(badgeRect, mBadgeSize * 0.4f, mBadgeSize * 0.4f, paint);
        }
    }

    private boolean isDigits() {
        // 是否是个位的数量
        return mNumber <= MAXIMUM_COUNT % 10 && mNumber > DEFAULT_COUNT;
    }

    private boolean isDefaultCount() {
        // 是否是默认数量
        return mNumber == DEFAULT_COUNT;
    }

    public static class Builder {

        private final BadgeDrawable mBadgeDrawable;
        private Bitmap mBitmap;

        public Builder(Context context) {
            mBadgeDrawable = new BadgeDrawable(context);
        }

        /**
         * @param textColor Badge文本的颜色, 默认为白色
         * @return this
         */
        public Builder setTextColor(@ColorRes int textColor) {
            mBadgeDrawable.mTextColor = getColor(textColor);
            return this;
        }

        /**
         * @param badgeColor Badge背景颜色, 默认为红色
         * @return this
         */
        public Builder setBadgeColor(@ColorRes int badgeColor) {
            mBadgeDrawable.mBadgeColor = getColor(badgeColor);
            return this;
        }

        /**
         * @param badgeSize Badge的大小, 圆形时默认大小为 {@link #DEFAULT_BADGE_CIRCLE_SIZE},
         *                  椭圆时默认大小为 {@link #DEFAULT_BADGE_SIZE},
         * @return this
         */
        public Builder setBadgeSize(float badgeSize) {
            mBadgeDrawable.mBadgeSize = dp2px(badgeSize);
            return this;
        }

        /**
         * @param badgeBorderColor Badge边框颜色, 默认为白色
         * @return this
         */
        public Builder setBadgeBorderColor(@ColorRes int badgeBorderColor) {
            mBadgeDrawable.mBadgeBorderColor = getColor(badgeBorderColor);
            return this;
        }

        /**
         * @param badgeBorderSize Badge边框大, 默认为 {@link #DEFAULT_BORDER_SIZE}
         * @return this
         */
        public Builder setBadgeBorderSize(float badgeBorderSize) {
            mBadgeDrawable.mBadgeBorderSize = dp2px(badgeBorderSize);
            return this;
        }

        /**
         * @param badgePosition Badge显示的位置, 可选值见 {@link BadgePosition}, 默认在右上角
         * @return this
         */
        public Builder setBadgePosition(@BadgePosition int badgePosition) {
            mBadgeDrawable.mBadgePosition = badgePosition;
            return this;
        }

        /**
         * @param circle Badge显示的形状是否是圆形, 默认为否
         * @return this
         */
        public Builder setCircle(boolean circle) {
            mBadgeDrawable.isCircle = circle;
            return this;
        }

        /**
         * @param number {@link #mNumber} 大于 {@link #mMaximumNumber}, 显示{@link #mMaximumNumber}
         *               {@link #mNumber} 等于 {@link #DEFAULT_COUNT}, 显示点
         *               {@link #mNumber} 小于 {@link #DEFAULT_COUNT}, 不显示点
         * @return this
         */
        public Builder setNumber(int number) {
            mBadgeDrawable.mNumber = number;
            return this;
        }

        /**
         * @param resId 需要添加Badge的Image Resource ID
         * @return this
         */
        public Builder setDrawable(@DrawableRes int resId) {
            setDrawable(mContext.getResources().getDrawable(resId, null));
            return this;
        }

        /**
         * @param drawable 需要添加Badge的Drawable
         * @return this
         */
        public Builder setDrawable(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                mBitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                mBitmap = createBitmapFromDrawable(drawable);
            }
            return this;
        }

        /**
         * @param bitmap 需要添加Badge的Bitmap
         * @return this
         */
        public Builder setDrawable(Bitmap bitmap) {
            mBitmap = bitmap;
            return this;
        }

        /**
         * 在创建一个带有Badge的Bitmap之前, 需要使用一个可以附着Badge的Drawable, 通过
         * {@link #setDrawable(int)} 方法添加一个Drawable, 创建一个最简单的Badge
         *
         * @return 创建一个附带Badge的Bitmap
         */
        public Bitmap build() {
            if (mBadgeDrawable.getNumber() < 0) {
                return mBitmap;
            } else {
                return mBadgeDrawable.buildBitmap(mBitmap);
            }
        }

        /**
         * 创建一个Badge, 并设置到ImageView中
         *
         * @param target 目标View
         */
        public void build(ImageView target) {
            Bitmap bitmap = build();
            target.setImageBitmap(bitmap);
        }

        /**
         * 通过Drawable创建Bitmap
         */
        private Bitmap createBitmapFromDrawable(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    private static Float dp2px(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    private static int getColor(int resId) {
        return ContextCompat.getColor(mContext, resId);
    }
}