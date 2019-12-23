package info.itloser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * Created by itloser.info
 * on 2019/12/20
 */
public class LatticeImageView extends ImageView {

    Paint latticePaint, selectPaint;

    int lineColor;

    int[] selectedOneList;

    float viewHeight, viewWidth;

    float oneHeight, oneWidth;

    private int numberOfLines, numberOfColumns; //行，列。

    int nowScrollOne;//用于滑动当前方格的处理

    GestureDetector gestureDetector;

    public LatticeImageView(Context context) {
        this(context, null);
    }

    public LatticeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LatticeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);//初始化属性值
        initPaint();
//        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁用硬件加速
        gestureDetector = new GestureDetector(context, listener);//手势监听
    }

    //初始化属性值
    public void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LatticeImageView);
        numberOfLines = typedArray.getInteger(R.styleable.LatticeImageView_lines_number, 4);
        numberOfColumns = typedArray.getInteger(R.styleable.LatticeImageView_columns_number, 8);
        lineColor = typedArray.getColor(R.styleable.LatticeImageView_Line_color, 0xFFFFFFFF);
        selectedOneList = new int[numberOfLines * numberOfColumns];
        if (typedArray.getBoolean(R.styleable.LatticeImageView_select_all, false))
            Arrays.fill(selectedOneList, 1);
        typedArray.recycle();
    }

    //初始化画笔
    public void initPaint() {

        latticePaint = new Paint();
        latticePaint.setStyle(Paint.Style.STROKE);
        latticePaint.setColor(lineColor);

        selectPaint = new Paint();
        selectPaint.setColor(lineColor);
        selectPaint.setAlpha(127);
        selectPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDefLine(canvas);

        drawSelectOne(canvas);

    }

    //画基本的分隔线
    public void drawDefLine(Canvas canvas) {

        viewWidth = this.getWidth();
        viewHeight = this.getHeight();

        //最外层的一圈长方形
        canvas.drawRect(1, 1, viewWidth - 1, viewHeight - 1, latticePaint);

        oneHeight = viewHeight / numberOfLines;
        oneWidth = viewWidth / numberOfColumns;

        //横线
        for (int i = 1; i < numberOfLines; i++) {
            canvas.drawLine(0, oneHeight * i, viewWidth, oneHeight * i, latticePaint);
        }

        //竖线
        for (int i = 1; i < numberOfColumns; i++) {
            canvas.drawLine(oneWidth * i, 0, oneWidth * i, viewHeight, latticePaint);
        }

    }

    //画选中的格子
    private void drawSelectOne(Canvas canvas) {

        for (int i = 1; i <= selectedOneList.length; i++) {
            if (selectedOneList[i - 1] == 1) {
                int x, y;
                if (i / numberOfColumns >= 1 && i % numberOfColumns == 0) {
                    x = i / numberOfColumns;
                    y = numberOfColumns;
                } else {
                    x = i % numberOfColumns == 0 ? 1 : i / numberOfColumns + 1;
                    y = i % numberOfColumns;//取余，余几就证明在第几列
                }
                canvas.drawRect((y - 1) * oneWidth, (x - 1) * oneHeight, oneWidth * y, x * oneHeight, selectPaint);

            }
        }

    }

    //以下是手势监听
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            nowScrollOne = 0;
        return gestureDetector.onTouchEvent(event);
    }

    //设置行数
    public void setNumberOfLinesAndColumns(int numberOfLines, int numberOfColumns) {
        this.numberOfLines = numberOfLines;
        this.numberOfColumns = numberOfColumns;
        selectedOneList = new int[numberOfLines * numberOfColumns];
        nowScrollOne = 0;
        invalidate();
    }

    //根据行列选中某个块，从左上角0开始
    public void selectOne(int i) {
        try {
            //有几率奔溃，所以try一下
            selectedOneList[i - 1] = selectedOneList[i - 1] == 1 ? 0 : 1;
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //监听重写
    GestureDetector.OnGestureListener listener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            selectOneByXY(e.getX(), e.getY());
            return true;//这里必须为true，否则下面的事件就无法回调
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            selectOneByXY(e2.getX(), e2.getY());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    };

    //根据坐标，找需要处理的方格
    public void selectOneByXY(float x, float y) {
        int i = (int) (x / oneWidth);
        int j = (int) (y / oneHeight);

        if (x > viewWidth || y > viewHeight) {
            //这里好像有可能选中的值大于view的值
            return;
        }

        if (nowScrollOne != j * numberOfColumns + i + 1) {
            selectOne(j * numberOfColumns + i + 1);
        }
        nowScrollOne = j * numberOfColumns + i + 1;
    }

    //全选和反选
    public void selectAll(boolean b) {
        if (b)
            Arrays.fill(selectedOneList, 1);
        else
            Arrays.fill(selectedOneList, 0);
        invalidate();
    }

    //按数组批量选中
    public void selectByArray(int[] ints) {
        Arrays.fill(selectedOneList, 0);
        System.arraycopy(ints, 0, selectedOneList, 0, ints.length);
        invalidate();
    }

}