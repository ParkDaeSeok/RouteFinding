package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import static android.speech.tts.TextToSpeech.ERROR;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private TextToSpeech tts;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

        EditText editText = (EditText) findViewById(R.id.text);

        ImageView imageView = (ImageView) findViewById(R.id.image);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("안녕");
                tts.speak("안녕",TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        //Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(bitmap2);
        // 점 도형(동그라미)
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 동그라미안에 글씨
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                return false;
            }
        });
    }
}