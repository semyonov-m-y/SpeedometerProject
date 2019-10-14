package ru.semenovmy.learning.speedometerproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.Locale;

public class SpeedometerView extends View {

    private static final int SEMICIRCLE = 180;
    private float centerX;
    private float centerY;
    private float speedArrowAngle;
    private float currentSpeed;
    private int viewBackgroundColorOrange;
    private int viewBackgroundColorYellow;
    private int viewBackgroundColorGreen;
    private int digitsColor;
    private int speedArrowColor;
    private int speedArrowLength;
    private int maxSpeed;
    private Matrix matrix;
    private Paint speedArrowPaint;
    private Paint backgroundPaint;
    private Paint digitsPaint;
    private Path speedArrowPath;
    private RectF circle;

    public SpeedometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.SpeedometerView, 0, 0);
        speedArrowLength = array.getInteger(R.styleable.SpeedometerView_speedArrowLength, 60);
        maxSpeed = array.getInteger(R.styleable.SpeedometerView_maxSpeed, 100);
        currentSpeed = array.getFloat(R.styleable.SpeedometerView_currentSpeed, 100);
        viewBackgroundColorOrange = array.getColor(R.styleable.SpeedometerView_backgroundColorOrange, 0);
        viewBackgroundColorYellow = array.getColor(R.styleable.SpeedometerView_backgroundColorYellow, 0);
        viewBackgroundColorGreen = array.getColor(R.styleable.SpeedometerView_backgroundColorGreen, 0);
        digitsColor = array.getColor(R.styleable.SpeedometerView_digitsColor, 0);
        speedArrowColor = array.getColor(R.styleable.SpeedometerView_speedArrowColor, 0);

        init();
    }

    private void init() {
        speedArrowPaint = new Paint();
        speedArrowPaint.setColor(speedArrowColor);
        backgroundPaint = new Paint();
        digitsPaint = new Paint();
        digitsPaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        speedArrowPath = new Path();
        matrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        updateCurrentSpeed();
        drawBackground(canvas);
        drawDigits(canvas);
        drawSpeedArrow(canvas);
    }

    private void updateCurrentSpeed() {
        float increment = 0.3f;
        if (currentSpeed - increment < 0) {
            currentSpeed = 0;
        } else if (currentSpeed - increment > maxSpeed) {
            currentSpeed = maxSpeed;
        } else {
            currentSpeed -= increment;
        }
    }

    private void drawBackground(Canvas canvas) {
        setSpeedScaleColor();
        circle = getCircle(canvas, 1f);
        canvas.drawArc(circle, SEMICIRCLE, SEMICIRCLE, true, backgroundPaint);
        centerX = circle.centerX();
        centerY = circle.centerY();
    }

    private void setSpeedScaleColor() {
        backgroundPaint.setColor(viewBackgroundColorOrange);
        if (currentSpeed < 80) {
            backgroundPaint.setColor(viewBackgroundColorYellow);
        }
        if (currentSpeed < 60) {
            backgroundPaint.setColor(viewBackgroundColorGreen);
        }
    }

    private void drawDigits(Canvas canvas) {
        digitsPaint.setColor(digitsColor);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(SEMICIRCLE + 100 / maxSpeed, centerX, centerY);
        Path circle = new Path();
        double halfSemicircle = (this.circle.width() / 2 - this.circle.width() / 8) * Math.PI;
        int digitsIncrement = 10;
        for (int i = digitsIncrement; i < maxSpeed; i += digitsIncrement) {
            circle.addCircle(centerX, centerY, (this.circle.width() / 2 - this.circle.width() / 8), Path.Direction.CW);
            digitsPaint.setTextSize(50);
            canvas.drawTextOnPath(String.format(Locale.getDefault(), "%d", i), circle,
                    (float) (i * halfSemicircle / maxSpeed),-50, digitsPaint);
        }
        canvas.restore();
    }

    private void drawSpeedArrow(Canvas canvas) {
        speedArrowPaint.setColor(speedArrowColor);
        setSpeedArrowAngle();
        drawArrowPath();
        canvas.drawPath(speedArrowPath, speedArrowPaint);
        invalidate();
    }

    private void drawArrowPath() {
        float radius = speedArrowLength * circle.width() / 200;
        int x = 0, y = 0;
        int speedArrowTopWidth = (int) (circle.width() / 100);
        speedArrowPath.reset();

        Point point1 = new Point(x, y);
        Point point2 = new Point(x + 3 * speedArrowTopWidth, (int) radius);
        Point point3 = new Point(x - speedArrowTopWidth, (int) radius);

        speedArrowPath.moveTo(point1.x, point1.y);
        speedArrowPath.lineTo(point2.x, point2.y);
        speedArrowPath.lineTo(point3.x, point3.y);

        matrix.setTranslate(centerX, centerY - radius);
        matrix.postRotate(speedArrowAngle - SEMICIRCLE / 2, centerX, centerY);
        speedArrowPath.transform(matrix);
    }

    private void setSpeedArrowAngle() {
        speedArrowAngle = currentSpeed / 100 * SEMICIRCLE;
    }

    private RectF getCircle(Canvas canvas, float index) {
        int canvasCircleWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        return circle = new RectF(0, 0, canvasCircleWidth * index, canvasCircleWidth * index);
    }
}