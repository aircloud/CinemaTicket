package com.example.xhz636.cinematicket;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collections;

public class SeatView extends View {

    private Paint paint = new Paint();
    private Paint overviewPaint = new Paint();
    private Paint headPaint;
    private Bitmap headBitmap;
    private Bitmap seatValidBitmap;
    private Bitmap setChooseBitmap;
    private Bitmap seatSoldBitmap;
    private Bitmap overviewBitmap;
    private Paint lineNumberPaint;
    private float lineNumberTxtHeight;
    private ArrayList<String> lineNumbers = new ArrayList<>();
    private Paint.FontMetrics lineNumberPaintFontMetrics;
    private Matrix matrix = new Matrix();
    private Matrix tempMatrix = new Matrix();
    private int row, column;
    private int lastX, lastY;
    private float xScale1 = 1, yScale1 = 1;
    private int seatBitmapWidth, seatBitmapHeight;
    private int seatWidth, seatHeight;
    private int widthSpacing, heightSpacing, numberWidth;
    private float overviewWidth, overviewHeight;
    private float overviewBlockWidth, overviewBlockHeight;
    private float overviewWidthSpacing, overviewHeightSpacing;
    private float overviewScale = 4.8f;
    private float screenHeight;
    private float screenWidthScale = 0.5f;
    private int defaultScreenWidth;
    private boolean isScaling;
    private float scaleX, scaleY;
    private boolean isFirstScale = true;
    private SeatClicker seatClicker;
    private String screenName;
    private boolean isDrawOverview = false;
    private boolean isDrawOverviewBitmap = true;
    private int overview_checked;
    private int overview_sold;
    private int txt_color;
    private int seatCheckedResID;
    private int seatSoldResID;
    private int seatAvailableResID;
    private int downX, downY;
    private boolean pointer;
    private float headHeight;
    private Paint pathPaint;
    private RectF rectF;
    private int borderHeight = 1;
    private Paint redBorderPaint;
    private boolean isOnClick;

    private ArrayList<Integer> soldlist = new ArrayList<>();

    private static final int SEAT_TYPE_SOLD = 1;
    private static final int SEAT_TYPE_SELECTED = 2;
    private static final int SEAT_TYPE_AVAILABLE = 3;

    public SeatView(Context context) {
        super(context);
    }

    public SeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public SeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeatTableView);
        overview_checked = typedArray.getColor(R.styleable.SeatTableView_overview_checked, Color.parseColor("#5A9E64"));
        overview_sold = typedArray.getColor(R.styleable.SeatTableView_overview_sold, Color.RED);
        txt_color=typedArray.getColor(R.styleable.SeatTableView_txt_color,Color.WHITE);
        seatCheckedResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_checked, R.drawable.seat_green);
        seatSoldResID = typedArray.getResourceId(R.styleable.SeatTableView_overview_sold, R.drawable.seat_sold);
        seatAvailableResID = typedArray.getResourceId(R.styleable.SeatTableView_seat_available, R.drawable.seat_gray);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        widthSpacing = (int) dip2Px(5);
        heightSpacing = (int) dip2Px(10);
        defaultScreenWidth = (int) dip2Px(10);
        seatValidBitmap = BitmapFactory.decodeResource(getResources(), seatAvailableResID);
        float defaultImgW = 80;
        float defaultImgH = 68;
        float scaleX = defaultImgW / seatValidBitmap.getWidth();
        float scaleY = defaultImgH / seatValidBitmap.getHeight();
        xScale1 = scaleX;
        yScale1 = scaleY;
        seatHeight= (int) (seatValidBitmap.getHeight()*yScale1);
        seatWidth= (int) (seatValidBitmap.getWidth()*xScale1);
        setChooseBitmap = BitmapFactory.decodeResource(getResources(), seatCheckedResID);
        seatSoldBitmap = BitmapFactory.decodeResource(getResources(), seatSoldResID);
        seatBitmapWidth = (int) (column * seatValidBitmap.getWidth()*xScale1 + (column - 1) * widthSpacing);
        seatBitmapHeight = (int) (row * seatValidBitmap.getHeight()*yScale1 + (row - 1) * heightSpacing);
        paint.setColor(Color.RED);
        numberWidth = (int) dip2Px(20);
        screenHeight = dip2Px(20);
        headHeight = dip2Px(30);
        headPaint = new Paint();
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setTextSize(24);
        headPaint.setColor(Color.WHITE);
        headPaint.setAntiAlias(true);
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));
        redBorderPaint = new Paint();
        redBorderPaint.setAntiAlias(true);
        redBorderPaint.setColor(Color.RED);
        redBorderPaint.setStyle(Paint.Style.STROKE);
        redBorderPaint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);
        rectF = new RectF();
        overviewBlockHeight = seatHeight / overviewScale;
        overviewBlockWidth = seatWidth / overviewScale;
        overviewWidthSpacing = widthSpacing / overviewScale;
        overviewHeightSpacing = heightSpacing / overviewScale;
        overviewWidth = column * overviewBlockWidth + (column - 1) * overviewWidthSpacing + overviewWidthSpacing * 2;
        overviewHeight = row * overviewBlockHeight + (row - 1) * overviewHeightSpacing + overviewHeightSpacing * 2;
        overviewBitmap = Bitmap.createBitmap((int) overviewWidth, (int) overviewHeight, Bitmap.Config.ARGB_4444);
        lineNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lineNumberPaint.setColor(bacColor);
        lineNumberPaint.setTextSize(getResources().getDisplayMetrics().density * 16);
        lineNumberTxtHeight = lineNumberPaint.measureText("4");
        lineNumberPaintFontMetrics = lineNumberPaint.getFontMetrics();
        lineNumberPaint.setTextAlign(Paint.Align.CENTER);
        if(lineNumbers==null) {
            lineNumbers=new ArrayList<>();
        }
        else if(lineNumbers.size()<=0) {
            for (int i = 0; i < row; i++) {
                lineNumbers.add((i + 1) + "");
            }
        }
        matrix.postTranslate(numberWidth + widthSpacing, headHeight + screenHeight + borderHeight + heightSpacing);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (row <= 0 || column == 0) {
            return;
        }
        drawSeat(canvas);
        drawNumber(canvas);
        if (headBitmap == null) {
            headBitmap = drawHeadInfo();
        }
        canvas.drawBitmap(headBitmap, 0, 0, null);
        drawScreen(canvas);
        if (isDrawOverview) {
            long s = System.currentTimeMillis();
            if (isDrawOverviewBitmap) {
                drawOverview();
            }
            canvas.drawBitmap(overviewBitmap, 0, 0, null);
            drawOverview(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int y = (int) event.getY();
        int x = (int) event.getX();
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            pointer = true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointer = false;
                downX = x;
                downY = y;
                isDrawOverview = true;
                handler.removeCallbacks(hideOverviewRunnable);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isOnClick) {
                    int downDX = Math.abs(x - downX);
                    int downDY = Math.abs(y - downY);
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        int dx = x - lastX;
                        int dy = y - lastY;
                        matrix.postTranslate(dx, dy);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.postDelayed(hideOverviewRunnable, 1500);

                autoScale();
                int downDX = Math.abs(x - downX);
                int downDY = Math.abs(y - downY);
                if ((downDX > 10 || downDY > 10) && !pointer) {
                    autoScroll();
                }

                break;
        }
        isOnClick = false;
        lastY = y;
        lastX = x;
        return true;
    }

    private Runnable hideOverviewRunnable = new Runnable() {
        @Override
        public void run() {
            isDrawOverview = false;
            invalidate();
        }
    };

    Bitmap drawHeadInfo() {
        String txt = "已售";
        float txtY = getBaseLine(headPaint, 0, headHeight);
        int txtWidth = (int) headPaint.measureText(txt);
        float spacing = dip2Px(10);
        float spacing1 = dip2Px(5);
        float y = (headHeight - seatValidBitmap.getHeight()) / 2;
        float width = seatValidBitmap.getWidth() + spacing1 + txtWidth + spacing + seatSoldBitmap.getWidth() + txtWidth + spacing1 + spacing + setChooseBitmap.getHeight() + spacing1 + txtWidth;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), (int) headHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, getWidth(), headHeight, headPaint);
        headPaint.setColor(Color.BLACK);
        float startX = (getWidth() - width) / 2;
        tempMatrix.setScale(xScale1,yScale1);
        tempMatrix.postTranslate(startX,(headHeight - seatHeight) / 2);
        canvas.drawBitmap(seatValidBitmap, tempMatrix, headPaint);
        canvas.drawText("可选", startX + seatWidth + spacing1, txtY, headPaint);
        float soldSeatBitmapY = startX + seatValidBitmap.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(xScale1,yScale1);
        tempMatrix.postTranslate(soldSeatBitmapY,(headHeight - seatHeight) / 2);
        canvas.drawBitmap(seatSoldBitmap, tempMatrix, headPaint);
        canvas.drawText("已售", soldSeatBitmapY + seatWidth + spacing1, txtY, headPaint);
        float checkedSeatBitmapX = soldSeatBitmapY + seatSoldBitmap.getWidth() + spacing1 + txtWidth + spacing;
        tempMatrix.setScale(xScale1,yScale1);
        tempMatrix.postTranslate(checkedSeatBitmapX,(headHeight - seatHeight) / 2);
        canvas.drawBitmap(setChooseBitmap, tempMatrix, headPaint);
        canvas.drawText("已选", checkedSeatBitmapX + spacing1 + seatWidth, txtY, headPaint);
        headPaint.setStrokeWidth(1);
        headPaint.setColor(Color.GRAY);
        canvas.drawLine(0, headHeight, getWidth(), headHeight, headPaint);
        return bitmap;
    }

    void drawScreen(Canvas canvas) {
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setColor(Color.parseColor("#e2e2e2"));
        float startY = headHeight + borderHeight;
        float centerX = seatBitmapWidth * getMatrixScaleX() / 2 + getTranslateX();
        float screenWidth = seatBitmapWidth * screenWidthScale * getMatrixScaleX();
        if (screenWidth < defaultScreenWidth) {
            screenWidth = defaultScreenWidth;
        }
        Path path = new Path();
        path.moveTo(centerX, startY);
        path.lineTo(centerX - screenWidth / 2, startY);
        path.lineTo(centerX - screenWidth / 2 + 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2 - 20, screenHeight * getMatrixScaleY() + startY);
        path.lineTo(centerX + screenWidth / 2, startY);
        canvas.drawPath(path, pathPaint);
        pathPaint.setColor(Color.BLACK);
        pathPaint.setTextSize(20 * getMatrixScaleX());
        canvas.drawText(screenName, centerX - pathPaint.measureText(screenName) / 2, getBaseLine(pathPaint, startY, startY + screenHeight * getMatrixScaleY()), pathPaint);
    }

    void drawSeat(Canvas canvas) {
        zoom = getMatrixScaleX();
        float translateX = getTranslateX();
        float translateY = getTranslateY();
        float scaleX = zoom;
        float scaleY = zoom;
        for (int i = 0; i < row; i++) {
            float top = i * seatValidBitmap.getHeight() * yScale1 * scaleY + i * heightSpacing * scaleY + translateY;
            float bottom = top + seatValidBitmap.getHeight() * yScale1 * scaleY;
            if (bottom < 0 || top > getHeight()) {
                continue;
            }
            for (int j = 0; j < column; j++) {
                float left = j * seatValidBitmap.getWidth() * xScale1 * scaleX + j * widthSpacing * scaleX + translateX;
                float right = (left + seatValidBitmap.getWidth() * xScale1 * scaleY);
                if (right < 0 || left > getWidth()) {
                    continue;
                }
                int seatType = getSeatType(i, j);
                tempMatrix.setTranslate(left, top);
                tempMatrix.postScale(xScale1, yScale1, left, top);
                tempMatrix.postScale(scaleX, scaleY, left, top);
                switch (seatType) {
                    case SEAT_TYPE_AVAILABLE:
                        canvas.drawBitmap(seatValidBitmap, tempMatrix, paint);
                        break;
                    case SEAT_TYPE_SELECTED:
                        canvas.drawBitmap(setChooseBitmap, tempMatrix, paint);
                        drawText(canvas, i, j, top, left);
                        break;
                    case SEAT_TYPE_SOLD:
                        canvas.drawBitmap(seatSoldBitmap, tempMatrix, paint);
                        break;
                }

            }
        }
    }

    private int getSeatType(int row, int column) {
        if (isHave(getID(row, column)) >= 0) {
            return SEAT_TYPE_SELECTED;
        }
        else if (isSold(row, column)) {
            return SEAT_TYPE_SOLD;
        }
        return SEAT_TYPE_AVAILABLE;
    }

    private int getID(int row, int column) {
        return row * this.column + (column + 1);
    }

    private void drawText(Canvas canvas, int row, int column, float top, float left) {
        String txt = (row + 1) + "排";
        String txt1 = (column + 1) + "座";
        if(seatClicker !=null){
            String[] strings = seatClicker.checkedSeatTxt(row, column);
            if(strings!=null&&strings.length>0){
                if(strings.length>=2){
                    txt=strings[0];
                    txt1=strings[1];
                }else {
                    txt=strings[0];
                    txt1=null;
                }
            }
        }
        TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(txt_color);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        float seatHeight = this.seatHeight * getMatrixScaleX();
        float seatWidth = this.seatWidth * getMatrixScaleX();
        txtPaint.setTextSize(seatHeight / 3);
        float center = seatHeight / 2;
        float txtWidth = txtPaint.measureText(txt);
        float startX = left + seatWidth / 2 - txtWidth / 2;
        if(txt1==null){
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + seatHeight), txtPaint);
        }else {
            canvas.drawText(txt, startX, getBaseLine(txtPaint, top, top + center), txtPaint);
            canvas.drawText(txt1, startX, getBaseLine(txtPaint, top + center, top + center + seatHeight / 2), txtPaint);
        }
    }

    int bacColor = Color.parseColor("#7e000000");

    void drawNumber(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        lineNumberPaint.setColor(bacColor);
        int translateY = (int) getTranslateY();
        float scaleY = getMatrixScaleY();
        rectF.top = translateY - lineNumberTxtHeight / 2;
        rectF.bottom = translateY + (seatBitmapHeight * scaleY) + lineNumberTxtHeight / 2;
        rectF.left = 0;
        rectF.right = numberWidth;
        canvas.drawRoundRect(rectF, numberWidth / 2, numberWidth / 2, lineNumberPaint);
        lineNumberPaint.setColor(Color.WHITE);
        for (int i = 0; i < row; i++) {

            float top = (i *seatHeight + i * heightSpacing) * scaleY + translateY;
            float bottom = (i * seatHeight + i * heightSpacing + seatHeight) * scaleY + translateY;
            float baseline = (bottom + top - lineNumberPaintFontMetrics.bottom - lineNumberPaintFontMetrics.top) / 2;

            canvas.drawText(lineNumbers.get(i), numberWidth / 2, baseline, lineNumberPaint);
        }
    }

    void drawOverview(Canvas canvas) {
        int left = (int) -getTranslateX();
        if (left < 0) {
            left = 0;
        }
        left /= overviewScale;
        left /= getMatrixScaleX();
        int currentWidth = (int) (getTranslateX() + (column * seatWidth + widthSpacing * (column - 1)) * getMatrixScaleX());
        if (currentWidth > getWidth()) {
            currentWidth = currentWidth - getWidth();
        } else {
            currentWidth = 0;
        }
        int right = (int) (overviewWidth - currentWidth / overviewScale / getMatrixScaleX());

        float top = -getTranslateY() + headHeight;
        if (top < 0) {
            top = 0;
        }
        top /= overviewScale;
        top /= getMatrixScaleY();
        if (top > 0) {
            top += overviewHeightSpacing;
        }
        int currentHeight = (int) (getTranslateY() + (row * seatHeight + heightSpacing * (row - 1)) * getMatrixScaleY());
        if (currentHeight > getHeight()) {
            currentHeight = currentHeight - getHeight();
        } else {
            currentHeight = 0;
        }
        int bottom = (int) (overviewHeight - currentHeight / overviewScale / getMatrixScaleY());
        canvas.drawRect(left, top, right, bottom, redBorderPaint);
    }

    Bitmap drawOverview() {
        isDrawOverviewBitmap = false;
        int bac = Color.parseColor("#7e000000");
        overviewPaint.setColor(bac);
        overviewPaint.setAntiAlias(true);
        overviewPaint.setStyle(Paint.Style.FILL);
        overviewBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(overviewBitmap);
        canvas.drawRect(0, 0, overviewWidth, overviewHeight, overviewPaint);
        overviewPaint.setColor(Color.WHITE);
        for (int i = 0; i < row; i++) {
            float top = i * overviewBlockHeight + i * overviewHeightSpacing + overviewHeightSpacing;
            for (int j = 0; j < column; j++) {

                int seatType = getSeatType(i, j);
                switch (seatType) {
                    case SEAT_TYPE_AVAILABLE:
                        overviewPaint.setColor(Color.WHITE);
                        break;
                    case SEAT_TYPE_SELECTED:
                        overviewPaint.setColor(overview_checked);
                        break;
                    case SEAT_TYPE_SOLD:
                        overviewPaint.setColor(overview_sold);
                        break;
                }
                float left;
                left = j * overviewBlockWidth + j * overviewWidthSpacing + overviewWidthSpacing;
                canvas.drawRect(left, top, left + overviewBlockWidth, top + overviewBlockHeight, overviewPaint);
            }
        }
        return overviewBitmap;
    }

    private void autoScroll() {
        float currentSeatBitmapWidth = seatBitmapWidth * getMatrixScaleX();
        float currentSeatBitmapHeight = seatBitmapHeight * getMatrixScaleY();
        float moveYLength = 0;
        float moveXLength = 0;
        if (currentSeatBitmapWidth < getWidth()) {
            if (getTranslateX() < 0 || getMatrixScaleX() < numberWidth + widthSpacing) {
                if (getTranslateX() < 0) {
                    moveXLength = (-getTranslateX()) + numberWidth + widthSpacing;
                }
                else {
                    moveXLength = numberWidth + widthSpacing - getTranslateX();
                }
            }
        } else {
            if (getTranslateX() < 0 && getTranslateX() + currentSeatBitmapWidth > getWidth()) {

            }
            else {
                if (getTranslateX() + currentSeatBitmapWidth < getWidth()) {
                    moveXLength = getWidth() - (getTranslateX() + currentSeatBitmapWidth);
                }
                else {
                    moveXLength = -getTranslateX() + numberWidth + widthSpacing;
                }
            }

        }
        float startYPosition = screenHeight * getMatrixScaleY() + heightSpacing * getMatrixScaleY() + headHeight + borderHeight;
        if (currentSeatBitmapHeight+headHeight < getHeight()) {
            if (getTranslateY() < startYPosition) {
                moveYLength = startYPosition - getTranslateY();
            } else {
                moveYLength = -(getTranslateY() - (startYPosition));
            }
        } else {
            if (getTranslateY() < 0 && getTranslateY() + currentSeatBitmapHeight > getHeight()) {
            }
            else {
                if (getTranslateY() + currentSeatBitmapHeight < getHeight()) {
                    moveYLength = getHeight() - (getTranslateY() + currentSeatBitmapHeight);
                } else {
                    moveYLength = -(getTranslateY() - (startYPosition));
                }
            }
        }
        Point start = new Point();
        start.x = (int) getTranslateX();
        start.y = (int) getTranslateY();
        Point end = new Point();
        end.x = (int) (start.x + moveXLength);
        end.y = (int) (start.y + moveYLength);
        moveAnimate(start, end);
    }

    private void autoScale() {
        if (getMatrixScaleX() > 2.2) {
            zoomAnimate(getMatrixScaleX(), 2.0f);
        } else if (getMatrixScaleX() < 0.98) {
            zoomAnimate(getMatrixScaleX(), 1.0f);
        }
    }

    Handler handler = new Handler();

    ArrayList<Integer> selects = new ArrayList<>();

    public ArrayList<Integer> getSelectedSeat() {
        ArrayList<Integer> results = new ArrayList<>();
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
                if (isHave(getID(i, j)) >= 0) {
                    results.add(i * this.column + j);
                }
            }
        }
        return results;
    }

    private int isHave(Integer seat) {
        return Collections.binarySearch(selects, seat);
    }

    private void remove(int index) {
        selects.remove(index);
    }

    float[] m = new float[9];

    private float getTranslateX() {
        matrix.getValues(m);
        return m[2];
    }

    private float getTranslateY() {
        matrix.getValues(m);
        return m[5];
    }

    private float getMatrixScaleY() {
        matrix.getValues(m);
        return m[4];
    }

    private float getMatrixScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }

    private float dip2Px(float value) {
        return getResources().getDisplayMetrics().density * value;
    }

    private float getBaseLine(Paint p, float top, float bottom) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        int baseline = (int) ((bottom + top - fontMetrics.bottom - fontMetrics.top) / 2);
        return baseline;
    }

    private void moveAnimate(Point start, Point end) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MoveEvaluator(), start, end);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        MoveAnimation moveAnimation = new MoveAnimation();
        valueAnimator.addUpdateListener(moveAnimation);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    private void zoomAnimate(float cur, float tar) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(cur, tar);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        ZoomAnimation zoomAnim = new ZoomAnimation();
        valueAnimator.addUpdateListener(zoomAnim);
        valueAnimator.addListener(zoomAnim);
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    private float zoom;

    private void zoom(float zoom) {
        float z = zoom / getMatrixScaleX();
        matrix.postScale(z, z, scaleX, scaleY);
        invalidate();
    }

    private void move(Point p) {
        float x = p.x - getTranslateX();
        float y = p.y - getTranslateY();
        matrix.postTranslate(x, y);
        invalidate();
    }

    class MoveAnimation implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            Point p = (Point) animation.getAnimatedValue();
            move(p);
        }
    }

    class MoveEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            int x = (int) (startPoint.x + fraction * (endPoint.x - startPoint.x));
            int y = (int) (startPoint.y + fraction * (endPoint.y - startPoint.y));
            return new Point(x, y);
        }
    }

    class ZoomAnimation implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            zoom = (Float) animation.getAnimatedValue();
            zoom(zoom);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

    }

    public void setData(int row, int column) {
        this.row = row;
        this.column = column;
        init();
        invalidate();
    }

    public void setSeat(int seatid) {
        if (!soldlist.contains(seatid))
            soldlist.add(seatid);
        invalidate();
    }

    public void clearSelect() {
        selects.clear();
        invalidate();
    }

    ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isScaling = true;
            float scaleFactor = detector.getScaleFactor();
            if (getMatrixScaleY() * scaleFactor > 3) {
                scaleFactor = 3 / getMatrixScaleY();
            }
            if (isFirstScale) {
                scaleX = detector.getCurrentSpanX();
                scaleY = detector.getCurrentSpanY();
                isFirstScale = false;
            }

            if (getMatrixScaleY() * scaleFactor < 0.5) {
                scaleFactor = 0.5f / getMatrixScaleY();
            }
            matrix.postScale(scaleFactor, scaleFactor, scaleX, scaleY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
            isFirstScale = true;
        }
    });

    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isOnClick = true;
            int x = (int) e.getX();
            int y = (int) e.getY();

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    int tempX = (int) ((j * seatWidth + j * widthSpacing) * getMatrixScaleX() + getTranslateX());
                    int maxTemX = (int) (tempX + seatWidth * getMatrixScaleX());

                    int tempY = (int) ((i * seatHeight + i * heightSpacing) * getMatrixScaleY() + getTranslateY());
                    int maxTempY = (int) (tempY + seatHeight * getMatrixScaleY());

                    if (seatClicker != null && !isSold(i, j)) {
                        if (x >= tempX && x <= maxTemX && y >= tempY && y <= maxTempY) {
                            int id = getID(i, j);
                            int index = isHave(id);
                            if (index >= 0) {
                                remove(index);
                                if (seatClicker != null) {
                                    seatClicker.unCheck(i, j);
                                }
                            } else {
                                addChooseSeat(i, j);
                                if (seatClicker != null) {
                                    seatClicker.checked(i, j);
                                }
                            }
                            isDrawOverviewBitmap = true;
                            float currentScaleY = getMatrixScaleY();

                            if (currentScaleY < 1.7) {
                                scaleX = x;
                                scaleY = y;
                                zoomAnimate(currentScaleY, 1.9f);
                            }

                            invalidate();
                            break;
                        }
                    }
                }
            }

            return super.onSingleTapConfirmed(e);
        }
    });

    private void addChooseSeat(int row, int column) {
        int id = getID(row, column);
        for (int i = 0; i < selects.size(); i++) {
            int item = selects.get(i);
            if (id < item) {
                selects.add(i, id);
                return;
            }
        }
        selects.add(id);
    }

    private boolean isSold(int row, int column) {
        if (soldlist.contains(row * this.column + column))
            return true;
        else
            return false;
    }

    public interface SeatClicker {

        void checked(int row, int column);

        void unCheck(int row, int column);

        String[] checkedSeatTxt(int row,int column);

    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setSeatClicker(SeatClicker seatClicker) {
        this.seatClicker = seatClicker;
        invalidate();
    }

}