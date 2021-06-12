package com.dzenm.fish_layout;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FishDrawable extends Drawable {

    // 鱼身的透明度
    private static final int ALPHA_PRIMARY = 160;
    // 鱼身之外的透明度
    private static final int ALPHA_SECONDARY = 110;

    // 鱼头的半径
    static final float RADIUS_HEAD = dp2px(10);
    // 鱼身长度
    private static final float LENGTH_BODY = 3.2f * RADIUS_HEAD;

    //-----------鱼鳍-----------//
    // 寻找鱼鳍开始点的线长
    private static final float LENGTH_FIND_FINS = 0.9f * RADIUS_HEAD;
    // 鱼鳍的长度
    private static final float LENGTH_FINS = 1.3f * RADIUS_HEAD;

    //-----------鱼尾-----------//
    // 鱼尾大圆的半径（圆心是身体底部的中点）
    private static final float RADIUS_BIG_CIRCLE = 0.7f * RADIUS_HEAD;
    // 鱼尾中圆的半径
    private static final float RADIUS_MIDDLE_CIRCLE = 0.6f * RADIUS_BIG_CIRCLE;
    // 鱼尾小圆的半径
    private static final float RADIUS_SMALL_CIRCLE = 0.4f * RADIUS_MIDDLE_CIRCLE;
    // 寻找鱼尾中圆的线长
    private static final float LENGTH_FIND_MIDDLE_CIRCLE = RADIUS_BIG_CIRCLE + RADIUS_MIDDLE_CIRCLE;
    // 寻找鱼尾小圆的线长
    private static final float LENGTH_FIND_SMALL_CIRCLE = (0.4f + 2.7f) * RADIUS_MIDDLE_CIRCLE;
    // 寻找大三角形底边中心点的线长
    private static final float LENGTH_FIND_TRIANGLE = 2.7f * RADIUS_MIDDLE_CIRCLE;

    private Paint mPaint;
    private Path mPath;
    // 鱼身的中心点，控制鱼的游动
    private PointF mFishHeadPoint, mFishMiddlePoint;

    // 鱼指向的角度
    private float mFishMainAngle = 90f;

    private float mCurrentValue = 0f;

    public FishDrawable() {
        init();
    }

    public void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setARGB(ALPHA_SECONDARY, 244, 92, 71);
        mPaint.setAntiAlias(true);  // 防锯齿
        mPaint.setDither(true);     // 防抖
        mPath = new Path();

        mFishMiddlePoint = new PointF(4.19f * RADIUS_HEAD, 4.19f * RADIUS_HEAD);

        execAnimator();
    }

    private void execAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.setDuration(1500);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentValue = (float) valueAnimator.getAnimatedValue();
                invalidateSelf();
            }
        });
        valueAnimator.start();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float fishAngle = (float) (mFishMainAngle + Math.sin(Math.toRadians(mCurrentValue)) * 10);

        // 计算并绘制鱼头
        mFishHeadPoint = calculatePoint(mFishMiddlePoint, LENGTH_BODY / 2, fishAngle);
        canvas.drawCircle(mFishHeadPoint.x, mFishHeadPoint.y, RADIUS_HEAD, mPaint);

        // 计算并绘制右鱼鳍
        PointF rightFinsPoint = calculatePoint(mFishHeadPoint, LENGTH_FIND_FINS, fishAngle - 110);
        makeFins(canvas, rightFinsPoint, fishAngle, true);

        // 计算并绘制左鱼鳍
        PointF leftFinsPoint = calculatePoint(mFishHeadPoint, LENGTH_FIND_FINS, fishAngle + 110);
        makeFins(canvas, leftFinsPoint, fishAngle, false);

        // 身体底部的中心点
        PointF bodyBottomCenterPoint = calculatePoint(mFishHeadPoint, LENGTH_BODY, fishAngle - 180);
        // 画节肢1
        PointF middleCircleCenterPoint = makeSegment(canvas, bodyBottomCenterPoint, RADIUS_BIG_CIRCLE,
                RADIUS_MIDDLE_CIRCLE, LENGTH_FIND_MIDDLE_CIRCLE, fishAngle, true);
        // 画节肢2
        makeSegment(canvas, middleCircleCenterPoint, RADIUS_MIDDLE_CIRCLE, RADIUS_SMALL_CIRCLE,
                LENGTH_FIND_SMALL_CIRCLE, fishAngle, false);

        // 画尾巴
        makeTriangle(canvas, middleCircleCenterPoint, LENGTH_FIND_TRIANGLE,
                RADIUS_BIG_CIRCLE, fishAngle);
        makeTriangle(canvas, middleCircleCenterPoint, LENGTH_FIND_TRIANGLE - 10,
                RADIUS_BIG_CIRCLE - 20, fishAngle);

        // 画身体
        makeBody(canvas, mFishHeadPoint, bodyBottomCenterPoint, fishAngle);
    }

    /**
     * 画身体
     *
     * @param canvas                画布
     * @param headPoint             头部的点
     * @param bodyBottomCenterPoint 身体底部中心的点
     * @param fishAngle             鱼指向的角度
     */
    private void makeBody(Canvas canvas, PointF headPoint, PointF bodyBottomCenterPoint,
                          float fishAngle) {
        // 身体的四个点
        PointF topLeftPoint = calculatePoint(headPoint, RADIUS_HEAD, fishAngle + 90);
        PointF topRightPoint = calculatePoint(headPoint, RADIUS_HEAD, fishAngle - 90);
        PointF bottomLeftPoint = calculatePoint(bodyBottomCenterPoint, RADIUS_BIG_CIRCLE,
                fishAngle + 90);
        PointF bottomRightPoint = calculatePoint(bodyBottomCenterPoint, RADIUS_BIG_CIRCLE,
                fishAngle - 90);

        // 二阶贝塞尔曲线的控制点，决定鱼的胖瘦
        PointF controlLeftPoint = calculatePoint(headPoint, 0.56f * LENGTH_BODY,
                fishAngle + 130);
        PointF controlRightPoint = calculatePoint(headPoint, 0.56f * LENGTH_BODY,
                fishAngle - 130);

        // 画身体
        mPath.reset();
        mPath.moveTo(topLeftPoint.x, topLeftPoint.y);
        mPath.quadTo(controlLeftPoint.x, controlLeftPoint.y, bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.quadTo(controlRightPoint.x, controlRightPoint.y, topRightPoint.x, topRightPoint.y);
        mPaint.setAlpha(ALPHA_PRIMARY);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 绘制尾巴
     *
     * @param canvas           画布
     * @param startPoint       绘制的起点
     * @param findCenterLength 寻找中间的线长
     * @param findEdgeLength   寻找边长
     * @param fishAngle        鱼指向的角度
     */
    private void makeTriangle(Canvas canvas, PointF startPoint,
                              float findCenterLength, float findEdgeLength, float fishAngle) {
        // 三角形底边的中心点
        PointF centerPoint = calculatePoint(startPoint, findCenterLength, fishAngle - 180);
        // 三角形底边的两点
        PointF leftPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle + 90);
        PointF rightPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle - 90);

        // 绘制三角形
        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.lineTo(leftPoint.x, leftPoint.y);
        mPath.lineTo(rightPoint.x, rightPoint.y);
        canvas.drawPath(mPath, mPaint);
    }


    private PointF makeSegment(Canvas canvas, PointF bottomCenterPoint, float bigRadius,
                               float smallRadius, float findSmallCircleLength, float fishAngle,
                               boolean hasBigAngle) {
        float segmentAngle;
        if (hasBigAngle) {
            segmentAngle = (float) (mFishMainAngle + Math.cos(Math.toRadians(mCurrentValue * 2)) * 20);
        } else {
            segmentAngle = (float) (mFishMainAngle + Math.sin(Math.toRadians(mCurrentValue * 3)) * 20);
        }

        // 梯形上底的中心点(中等大的圆的圆心)
        PointF upperCenterPoint = calculatePoint(bottomCenterPoint, findSmallCircleLength,
                segmentAngle - 180);
        // 梯形的四个点
        PointF bottomLeftPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle + 90);
        PointF bottomRightPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle - 90);
        PointF upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle + 90);
        PointF upperRightPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle - 90);

        if (hasBigAngle) {
            // 画大圆
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint);
        }
        // 画小圆
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint);
        // 画梯形
        mPath.reset();
        mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(upperLeftPoint.x, upperLeftPoint.y);
        mPath.lineTo(upperRightPoint.x, upperRightPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        canvas.drawPath(mPath, mPaint);

        return upperCenterPoint;
    }

    /**
     * 绘制鱼鳍
     *
     * @param canvas      画布
     * @param startPoint  起始点
     * @param fishAngle   鱼指向的角度
     * @param isRightFins 是否是右边的鱼鳍
     */
    private void makeFins(Canvas canvas, PointF startPoint, float fishAngle, boolean isRightFins) {
        float controlAngle = 115;
        PointF endPoint = calculatePoint(startPoint, LENGTH_FINS, fishAngle - 180);
        PointF controlPoint = calculatePoint(startPoint, 1.8f * LENGTH_FINS,
                isRightFins ? fishAngle - controlAngle : fishAngle + controlAngle);

        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 计算位置
     *
     * @param startPoint 起始点
     * @param length     长度
     * @param angle      角度
     * @return 相对位置
     */
    public PointF calculatePoint(PointF startPoint, float length, float angle) {
        float deltaX = (float) (Math.cos(Math.toRadians(angle)) * length);
        float deltaY = (float) (Math.sin(Math.toRadians(angle - 180)) * length);
        return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        // alpha == 255 设置PixelFormat.OPAQUE
        // alpha == 0 设置PixelFormat.TRANSPARENT
        // alpha 介于之间 设置PixelFormat.TRANSLUCENT
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) (8.38f * RADIUS_HEAD);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (8.38f * RADIUS_HEAD);
    }

    public void setFishMainAngle(float mFishMainAngle) {
        this.mFishMainAngle = mFishMainAngle;
    }

    public PointF getFishMiddlePoint() {
        return mFishMiddlePoint;
    }

    public PointF getFishHeadPoint() {
        return mFishHeadPoint;
    }

    static int dp2px(float value) {
        Resources r = Resources.getSystem();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
    }
}