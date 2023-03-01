package com.example.routefinding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class ViewCanvas extends View {
    public ViewCanvas(Context context) {
        super(context);
    }

    public ViewCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private ImageView imageView;
    private int x;
    private int y;
    private int count = 0;
    private boolean reset = false; // 그림 리셋
    private List<Integer> list = new ArrayList();


    public boolean getReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    @Override
    //protected void onDraw(Canvas canvas) {
    //   super.onDraw(canvas);
    //    Paint paint = new Paint();
    //    Paint paint2 = new Paint();
    //    if (reset == false) {
    //        paint.setColor(Color.WHITE);
    //       paint.setTextSize(50);
    //        paint2.setColor(Color.RED);
    //        for(int i=0; i< list.size(); i+=2) {
    //            canvas.drawCircle(list.get(i), list.get(i+1), 50, paint2);
    //            canvas.drawText(String.valueOf(count), (list.get(i))-15, (list.get(i+1))+5, paint);
    //        }
    //    } else {
     //       paint.setColor(Color.TRANSPARENT);
     //       for(int i=0; i< list.size(); i+=2) {
     //           //canvas.drawCircle(list.get(i), list.get(i+1), 50, paint);
     //           canvas.drawText(String.valueOf(count), (list.get(i))-15, (list.get(i+1))+5, paint);
      //      }
       //     reset = false;
       //     count = 0;
        //    list = new ArrayList<Integer>();
        //}

    //}

    //@Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //count +=1;
            //list.add((int) (event.getX()));
            //list.add((int) (event.getY()));
            // 뷰를 누를 때
        } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            // 뷰를 누른 후 움직일 때
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            // 뷰에서 손가락을 땔 때
        }
        invalidate();
        return true;
    }


}
