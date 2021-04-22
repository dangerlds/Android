package com.dangerdasheng.buttomtabdemo.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.dangerdasheng.buttomtabdemo.R;
import com.dangerdasheng.buttomtabdemo.util.L;

/**
 * =============================
 * Author:   liudasheng
 * Version:  1.0
 * DateTime: 2020/08/14
 * Function: 可以渐变的View，包含动态图标
 * =============================
 */
public class AlphaTabView extends View {

    private Context mContext;                           //上下文
    private Bitmap mIconNormal;                         //默认图标
    private Bitmap mIconSelected;                       //选中的图标
    private String mText;                               //描述文本
    private int mTextColorNormal = 0xFF999999;          //文本默认颜色
    private int mTextColorSelected = 0xFF46C018;     //文本选中颜色
    private int mTextSize = 12;                         //文本字体 12sp
    private int mPadding = 5;                           //文本与图标间距 5dp

    private float mAlpha;                               //当前透明度
    private Paint mSelectedPaint = new Paint();         //背景的画笔
    private Rect mIconAvailableRect = new Rect();       //图标可用的绘制区域
    private Rect mIconDrawRect = new Rect();            //图标真正的绘制区域
    private Paint mTextPaint;                           //描述文本的画笔
    private Rect mTextBound;                            //描述文本矩形测量大小
    private Paint.FontMetricsInt mFmi;                  //用于获取字体的各种属性

    private boolean isShowRemove;                       //是否移除当前角标
    private boolean isShowPoint;                        //是否显示圆点
    private int mBadgeNumber;                           //角标数
    private int mBadgeBackgroundColor = 0xFFFF0000;     //角标背景颜色

    public AlphaTabView(Context context) {
        this(context, null);
        mContext = context;
    }

    public AlphaTabView(Context context,AttributeSet attrs){
        this(context,attrs,0);
        mContext = context;
    }



    public AlphaTabView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        mContext = context;

        /* TypedValue.applyDimension : 把Android系统中的非标准度量尺寸转变为标准度量尺寸*/
        /* getResources().getDisplayMetrics():获取手机屏幕参数 */
        mTextSize =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        mTextSize,getResources().getDisplayMetrics());
        mPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                ,mPadding,getResources().getDisplayMetrics());
        //获取所有的自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AlphaTabView);
        BitmapDrawable iconNormal =
                (BitmapDrawable)a.getDrawable(R.styleable.AlphaTabView_tabIconNormal);
        if (iconNormal != null){
            mIconNormal = iconNormal.getBitmap();
        }
        BitmapDrawable iconSelected =
                (BitmapDrawable)a.getDrawable(R.styleable.AlphaTabView_tabIconSelected);
        if (iconSelected != null){
            mIconSelected = iconSelected.getBitmap();
        }

        if (null != mIconNormal){
            mIconSelected = null == mIconSelected ? mIconNormal : mIconSelected;
        }else {
            mIconNormal = null == mIconSelected ? mIconNormal : mIconSelected;
        }
        mText = a.getString(R.styleable.AlphaTabView_tabText);
        mTextSize = a.getDimensionPixelSize(R.styleable.AlphaTabView_tabTextSize, mTextSize);
        mTextColorNormal = a.getColor(R.styleable.AlphaTabView_textColorNormal, mTextColorNormal);
        mTextColorSelected = a.getColor(R.styleable.AlphaTabView_textColorSelected, mTextColorSelected);
        mBadgeBackgroundColor = a.getColor(R.styleable.AlphaTabView_badgeBackgroundColor,mBadgeBackgroundColor);
        mPadding = (int) a.getDimension(R.styleable.AlphaTabView_paddingTexwithIcon,mPadding);
        a.recycle();
        initText();
    }

    /**
     * 如果有设置文字就获取文字的区域大小
     */
    private void initText(){
        if (mText != null){
            mTextBound = new Rect();
            mTextPaint = new Paint();
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setAntiAlias(true); //抗锯齿
            mTextPaint.setDither(true);    //防抖动
            mTextPaint.getTextBounds(mText,0,mText.length(),mTextBound);
            mFmi = mTextPaint.getFontMetricsInt(); //获取字体属性

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mText == null && (mIconNormal == null || mIconSelected == null)){
            throw new IllegalArgumentException("必须设置 tabText 或者 tabIconSelected、tabIconNormal 两个，或者全部设置");
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        //计算出可用绘图的区域
        int availableWidth = measuredWidth - paddingLeft - paddingRight;
        int availableHeight = measuredHeight - paddingBottom - paddingTop;
        if (mText != null && mIconNormal != null){
            availableHeight -= (mTextBound.height()+ mPadding);
            //计算出图标可以绘制的画布大小
            mIconAvailableRect.set(paddingLeft,paddingTop,paddingLeft + availableWidth,paddingTop + availableHeight);
            //计算文字的绘图区域
            int textLeft = paddingLeft + (availableWidth-mTextBound.width())/2;
            int textTop = mIconAvailableRect.bottom + mPadding;
            mTextBound.set(textLeft,textTop,textLeft + mTextBound.width(),textTop+mTextBound.height());
        }else if (mText == null){
            mIconAvailableRect.set(paddingLeft,paddingTop,paddingLeft + availableWidth,paddingTop + availableHeight);
        }else if (mIconNormal == null){
            int textLeft = paddingLeft + (availableWidth-mTextBound.width())/2;
            int textTop = paddingTop + (availableHeight-mTextBound.height())/2;
            mTextBound.set(textLeft,textTop,textLeft+mTextBound.width(),textTop+mTextBound.height());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int alpha = (int)Math.ceil(mAlpha *255);
        if (mIconNormal != null && mIconSelected != null){
            Rect drawRect = availableToDrawRect(mIconAvailableRect,mIconNormal);
            L.e("width" + String.valueOf(mIconAvailableRect.width()));
            L.e("height" + String.valueOf(mIconAvailableRect.height()));
            mSelectedPaint.reset();
            mSelectedPaint.setAntiAlias(true);//设置边缘的锯齿
            mSelectedPaint.setFilterBitmap(true);//设置位图滤波处理
            mSelectedPaint.setAlpha(255 - alpha);//alpha设置必须放在paint的属性最后设置，否则不起作用
            canvas.drawBitmap(mIconNormal,null,drawRect,mSelectedPaint);
            mSelectedPaint.reset();
            mSelectedPaint.setAntiAlias(true);
            mSelectedPaint.setFilterBitmap(true);
            mSelectedPaint.setAlpha(alpha);
            canvas.drawBitmap(mIconSelected,null,drawRect,mSelectedPaint);
        }
        if (mText != null){
            mTextPaint.setColor(mTextColorNormal);
            mTextPaint.setAlpha(255-alpha);
            //drawText中，u轴坐标代表的是baseline的值，经测试，mTextbounds.height() + mFmi.bottom就是字体的高
            //所以再最后绘制前，修正偏移量，将文字向上修正 mFmi.bottom / 2 即可实现垂直居中
            canvas.drawText(mText,mTextBound.left,mTextBound.bottom-mFmi.bottom/2,mTextPaint);
            mTextPaint.setColor(mTextColorSelected);
            mTextPaint.setAlpha(alpha);
            canvas.drawText(mText,mTextBound.left,mTextBound.bottom-mFmi.bottom/2,mTextPaint);

        }
        if (!isShowRemove){
            drawBadge(canvas);
        }

    }

    /**
     * draw
     */
    private void drawBadge(Canvas canvas){
        int i = getMeasuredWidth() / 14 ;
        int j = getMeasuredHeight() / 9;
        i = Math.min(i, j);
        if (mBadgeNumber > 0){
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(mBadgeBackgroundColor);
            backgroundPaint.setAntiAlias(true);
            String number = mBadgeNumber > 99 ? "99+" : String.valueOf(mBadgeNumber);
            float textSize = i / 1.5f == 0 ? 5 : i / 1.5f;
            int width;
            int height = (int)dp2px(mContext,i);
            Bitmap bitmap;
            if (number.length() == 1){
                width = (int)dp2px(mContext,i);
                bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            }else if (number.length() == 2){
                width = (int)dp2px(mContext,i+5);
                bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            }else {
                width = (int)dp2px(mContext,i+8);
                bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            }
            Canvas canvasMessages = new Canvas(bitmap);
            RectF messageRectF = new RectF(0,0,width,height);
            canvasMessages.drawRoundRect(messageRectF,50,50,backgroundPaint);
            Paint numberPaint = new Paint();
            numberPaint.setColor(Color.WHITE);
            numberPaint.setTextSize(dp2px(mContext,textSize));
            numberPaint.setAntiAlias(true);
            numberPaint.setTextAlign(Paint.Align.CENTER);
            numberPaint.setTypeface(Typeface.DEFAULT_BOLD);
            Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
            float x = width / 2f;
            //fontMetrics.descent : descent:是baseline之下至字符最低处的距离
            //fontMetrics.ascent : ascent是baseline之上至字符最高处的距离
            float y = height / 2f - fontMetrics.descent + (fontMetrics.descent - fontMetrics.ascent) / 2;
            canvasMessages.drawText(number,x,y,numberPaint);
            float left = getMeasuredWidth() / 10 * 6f;
            float top = dp2px(mContext,5);
            canvas.drawBitmap(bitmap,left,top,null);
            bitmap.recycle();

        }else if (mBadgeNumber == 0){

        }else {
            if (isShowPoint){
                Paint paint = new Paint();
                paint.setColor(mBadgeBackgroundColor);
                paint.setAntiAlias(true);
                float left = getMeasuredWidth() / 10 * 6f;
                float top = dp2px(getContext(),5);
                i = Math.min(i, 10);
                float width = dp2px(getContext(),i);
                RectF messageRectF = new RectF(left,top,left + width,top + width);
                canvas.drawOval(messageRectF,paint);
            }
        }

    }

    public void showPoint(){
        isShowRemove = false;
        mBadgeNumber = -1;
        isShowPoint = true;
        invalidate();
    }

    public void showNumber(int badgeNum){
        isShowRemove = false;
        isShowPoint = false;
        mBadgeNumber = badgeNum;
        if (badgeNum > 0){
            invalidate();
        }else {
            isShowRemove = true;
            invalidate();
        }
    }

    public void removeShow(){
        mBadgeNumber = 0;
        isShowPoint = false;
        isShowRemove = true;
        invalidate();
    }

    public int getBadgeNumber(){
        return mBadgeNumber;
    }

    public boolean isShowPoint(){
        return isShowPoint;
    }

    private Rect availableToDrawRect(Rect availableRect,Bitmap bitmap){
        float dx = 0,dy = 0;
        float wRatio = availableRect.width() * 1.0f / bitmap.getWidth();
        float hRatio = availableRect.height() * 1.0f / bitmap.getHeight();
        if (wRatio > hRatio){
            dx = (availableRect.width() - hRatio * bitmap.getWidth()) / 2;

        }else{
            dy = (availableRect.height() - wRatio *bitmap.getHeight()) / 2;
        }
        int left = (int)(availableRect.left + dx + 0.5f);
        int top = (int)(availableRect.top + dy + 0.5f);
        int right = (int)(availableRect.right - dx + 0.5f);
        int bottom = (int)(availableRect.bottom - dy + 0.5f);
        mIconDrawRect.set(left,top,right,bottom);
        return mIconDrawRect;


    }

    /**
     * @param alpha 对外提供的设置透明度的方案，取值0.0~1.0
     */
    public void setIconAlpha(float alpha){
        if (alpha <0 || alpha>1){
            throw new IllegalArgumentException("透明度必须是0.0~1.0");
        }
        mAlpha = alpha;
        invalidateView();
    }

    /**
     * 根据当前所在线程更新界面
     */
    private void invalidateView(){
        if (Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        }else {
            postInvalidate();
        }
    }

    private float dp2px(Context context,float dipValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale);
    }




}
