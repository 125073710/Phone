package telephony.internal.android.com.phoneui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by  2018/1/18.
 * 自定义字母索引框
 */

public class JpSpellsSortView extends View{
    /*绘制的列表导航字母*
     * あかさたなはまやらわ他
     */
    private String   spells[]= {"あ","か","さ","た","な","は","ま","や","ら","わ","他"};
    //字母画笔
    private Paint spellPaint;
    //字母背景画笔
    private Paint spellbgPaint;
    /*每一个字母的宽度*/
    private int itemWidth;
    /*每一个字母的高度*/
    private int itemHeight;
    /*手指按下的字母索引*/
    private int touchIndex = 0;
    /*手指按下的字母改变接口*/
    private onWordsChangeListener listener;

    public JpSpellsSortView(Context context) {
        super(context);
    }

    public JpSpellsSortView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public JpSpellsSortView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //进行一些初始化操作
    private void init() {
        //初始化字母画笔
        spellPaint = new Paint();
        spellPaint.setColor(Color.parseColor("#FFFFFF"));
        spellPaint.setAntiAlias(true);
        spellPaint.setTextSize(25);
        spellPaint.setTypeface(Typeface.DEFAULT_BOLD);

        //初始化字母背景画笔
        spellbgPaint = new Paint();
        spellbgPaint.setAntiAlias(true);
        spellbgPaint.setColor(Color.parseColor("#ffFFB6C1"));

    }
    //测量每个字母的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
       itemWidth = getMeasuredHeight();
        //使得边距好看一些
        int height = getMeasuredWidth();//590
        itemHeight = height / 11;//spells.length


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i <spells.length ; i++) {
            //判断是不是我们按下的当前字母,让字母显示为白色，其他的为灰色
            if (touchIndex==i){
                //绘制文字圆形背景
                Log.e(TAG,"itemWidth="+itemWidth+"[itemHeight / 2 + i * itemHeight]="+itemHeight / 2 + i * itemHeight);
                canvas.drawRect(20 +  i* itemHeight, -5, 18 + (i+1)* itemHeight, 40, spellbgPaint);
                spellPaint.setColor(Color.WHITE);
            }else {
                spellPaint.setColor(Color.parseColor("#FFFFFF"));
            }
            //获取文字的宽高
            Rect rect = new Rect();
            spellPaint.getTextBounds(spells[i], 0, 1, rect);
            int wordWidth = rect.width();
            //绘制字母
            float wordX = itemWidth / 2 - wordWidth / 2;
            float wordY = itemWidth / 2 + i * itemHeight;
            canvas.drawText(spells[i],wordY, wordX+10,  spellPaint);
        }
    }
    /**
     * 当手指触摸按下的时候改变字母背景颜色
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                //获得我们按下的是那个索引(字母)
                int index = (int) (x / itemHeight);
                Log.e(TAG,"X="+x+"index="+index);
                if (index != touchIndex)
                    touchIndex = index;
                //防止数组越界
                if (listener != null && 0 <= touchIndex && touchIndex <= spells.length - 1) {
                    //回调按下的字母
                    listener.wordsChange(spells[touchIndex]);
                }
                //重新绘制
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起,不做任何操作
                break;
        }
        return true;
    }

    public void setKeyEvent(int index){
        if (index != touchIndex)
            touchIndex = index;
        //防止数组越界
        if (listener != null && 0 <= touchIndex && touchIndex <= spells.length - 1) {
            //回调按下的字母
            listener.wordsChange(spells[touchIndex]);
        }
        //重新绘制
        invalidate();
    }

    /*设置当前按下的是那个字母
    * 暴露给外边使用
    * */
    public void setTouchIndex(String words) {
        String word = words.toLowerCase();
        for (int i = 0; i < spells.length; i++) {
            if (spells[i].equals(word)) {
                touchIndex = i;
                invalidate();
                return;
            }
        }
    }
    /*手指按下了哪个字母的回调接口*/
    public interface onWordsChangeListener {
        void wordsChange(String words);
    }

    /*设置手指按下字母改变监听*/
    public void setOnWordsChangeListener(onWordsChangeListener listener) {
        this.listener = listener;
    }

}
