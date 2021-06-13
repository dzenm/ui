package com.dzenm.fish_layout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FishLayout extends RelativeLayout {

    private Paint mPaint;
    private ImageView mFishView;
    private FishDrawable mFishDrawable;

    private float mTouchX = 0, mTouchY = 0;
    private float ripple = 0;
    private int alpha = 0;

    public FishLayout(Context context) {
        this(context, null);
    }

    public FishLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FishLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);

        mFishView = new ImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mFishView.setLayoutParams(params);

        mFishDrawable = new FishDrawable();
        mFishView.setImageDrawable(mFishDrawable);
        addView(mFishView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAlpha(alpha);
        canvas.drawCircle(mTouchX, mTouchY, ripple * 150, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchX = event.getX();
        mTouchY = event.getY();

        execRippleAnimator();

        makeTrail();

        return super.onTouchEvent(event);
    }

    private void execRippleAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this,
                "ripple", 0, 1.0f);
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    private void makeTrail() {
        PointF fishRelativeMiddle = mFishDrawable.getFishMiddlePoint();
        // 起始点
        PointF fishMiddle = new PointF(mFishView.getX() + fishRelativeMiddle.x,
                mFishView.getY() + fishRelativeMiddle.y);
        PointF fishHead = new PointF(mFishView.getX() + mFishDrawable.getFishHeadPoint().x,
                mFishView.getY() + mFishDrawable.getFishHeadPoint().y);
        // 结束点
        PointF touch = new PointF(mTouchX, mTouchY);

        float angle = includeAngle(fishMiddle, fishHead, touch);
        float delta = includeAngle(fishMiddle, new PointF(fishMiddle.x + 1, fishMiddle.y), fishHead);
        // 鱼游动的贝塞尔曲线的控制点
        PointF controlPoint = mFishDrawable.calculatePoint(fishMiddle, FishDrawable.RADIUS_HEAD * 1.6f,
                angle / 2 + delta);
        Path path = new Path();
        path.moveTo(fishMiddle.x - fishRelativeMiddle.x, fishMiddle.y - fishRelativeMiddle.y);
        path.cubicTo(fishHead.x - fishRelativeMiddle.x, fishHead.y - fishRelativeMiddle.y,
                controlPoint.x - fishRelativeMiddle.x, controlPoint.y - fishRelativeMiddle.y,
                mTouchX - fishRelativeMiddle.x, mTouchY - fishRelativeMiddle.y);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mFishView, "x", "y", path);
        objectAnimator.setDuration(2000);

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final float[] tan = new float[2];
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                pathMeasure.getPosTan(pathMeasure.getLength() * fraction, null, tan);
                float angle = (float) Math.toDegrees(Math.atan2(-tan[1], tan[0]));
                mFishDrawable.setFishMainAngle(angle);
            }
        });
        objectAnimator.start();
    }

    public float getRipple() {
        return ripple;
    }

    public void setRipple(float ripple) {
        alpha = (int) (150 * (1 - ripple));
        this.ripple = ripple;
        invalidate();
    }

    /**
     * 向量夹角计算
     *
     * @param O 圆点
     * @param A A点
     * @param B B点
     * @return 夹角值
     */
    private float includeAngle(PointF O, PointF A, PointF B) {
        float AOB = (A.x - O.x) * (B.x - O.x) + (A.y - O.y) * (B.y - O.y);
        // OA长度
        float OALength = (float) Math.sqrt((A.x - O.x) * (A.x - O.x) + (A.y - O.y) * (A.y - O.y));
        float OBLength = (float) Math.sqrt((B.x - O.x) * (B.x - O.x) + (B.y - O.y) * (B.y - O.y));

        // cosAOB = (OA * OB) / (|OA| * |OB|)
        float cosAOB = AOB / (OALength * OBLength);

        // 将弧度转化为度数, Math.acos 反余弦
        float angleAOB = (float) Math.toDegrees(Math.acos(cosAOB));
        // 判断方向，正左侧，负右侧，0在线上，Android的坐标系为Y是朝下，因此左右互换
        float direct = (A.y - B.y) / (A.x - B.x) - (O.y - B.y) * (O.x - B.x);
        if (direct == 0) {
            return AOB >= 0 ? 0 : 180;
        }
        return direct > 0 ? -angleAOB : angleAOB;
    }
}
