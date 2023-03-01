package com.example.routefinding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.NodeChangeEvent;

import static android.speech.tts.TextToSpeech.ERROR;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;


public class MainActivity extends AppCompatActivity {

    private Button btn_picture;
    private Button btn_select;
    private Button btn_reset;
    private Button btn_lock;
    private AppCompatButton btn_voice;

    private Uri uri;
    private Bitmap bitmap;

    // 사진 이미지뷰
    private ImageView imageView;
    // 캔버스 뷰
    private ImageView imageView2;

    // 번호찍을 변수 저장 리스트 (x,y)
    private List<Integer> list = new ArrayList<>();

    // 번호 (point)
    private int count = 0;
    
    // text 번호
    private int voice = 0;

    // text를 음성으로 읽어주는 객체
    private TextToSpeech tts;

    // text를 저장하는 list
    private ArrayList<String> textList;

    // 그림 그리는 bitmap
    private Bitmap bitmap2;
    
    // longtouch 체크
    boolean longCheck = false;

    // 이미지뷰 잠금 버튼 true false
    boolean lockCheck = false;

    private static final int REQUEST_IMAGE_CODE = 101;

    private int imageSetBF = 0; // 사진 이전
    private int imageSetAF = 0; // 사진 이후

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 권한 설정
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();


        // 사진이 걸리는 이미지뷰
        imageView = (ImageView) findViewById(R.id.imageView);

        // 사진 찍기
        btn_picture = (Button) findViewById(R.id.btn_picture);
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        // 사진 선택
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPicture();
                if(imageSetAF != 0) {
                    if(imageSetBF != imageSetAF) { // 새로운 사진이 세팅되었으면 // 리셋
                        bitmap2.eraseColor(Color.TRANSPARENT);
                        reset();
                    }
                    imageSetBF = imageSetAF; // 이전세팅에 이후세팅 정수 대입
                }
            }
        });

        // 번호 리셋 버튼
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(bitmap2 != null) { // bitmap2 == 점을 찍는 화면이 있다면
                      bitmap2.eraseColor(Color.TRANSPARENT);
                      reset(); // list(포인트)에 뭔가 없다면 메시지 
                      // 있다면 list와 textList삭제하고 count 0으로 초기화
                  }
            }
        });

        // 이미지뷰 잠금
        btn_lock = (Button) findViewById(R.id.btn_lock);
        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lock = btn_lock.getText().toString();
                if(lock.equals("잠금")) { // 버튼 클릭 시 text가 잠금일시
                    lockCheck = true; // 화면 락
                    btn_lock.setText("잠금해제");
                }
                else if(lock.equals("잠금해제")) { // 버튼 클릭 시 text가 잠금해제일시
                    lockCheck = false;
                    btn_lock.setText("잠금");
                }
            }
        });

        // 점에 대한 각도 텍스트가 들어가는 list
        textList = new ArrayList<>();

        // tts 객체
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        // 텍스트 음성 시작
        btn_voice = (AppCompatButton) findViewById(R.id.btn_voice);
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textList != null) { // textlist이 널이 아니면
                    String textSum = String.join("  ", textList);
                    tts.speak(textSum, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
        // 캔버스
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        // 화면 전체 크기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        // 비율인가?
        // createBitmap width,height 어떻게 되는 건지
        int width = size.x; //1050;
        int height = (int)(size.y*0.6883);  //1700;
        int radius = 35;
        // 사진 캔버스 비트맵

        bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);
        // 점 도형(동그라미)
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 동그라미안에 글씨
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        
        // 사진 터치
        imageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (imageSetBF != 0) {
                    if (lockCheck == false) { // lock이 아니고
                        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                longCheck = true; // true이면 longtouch // false이면 그냥 터치
                                return false;
                            }
                        });

                        if (event.getAction() == MotionEvent.ACTION_DOWN && longCheck == false) {
                            list.add((int) (event.getX()));
                            list.add((int) (event.getY()));

                            if (list != null) {
                                paint.setColor(Color.RED); // 점 도형 (동그라미)
                                paint2.setColor(Color.WHITE); // 점안에 글씨
                                paint2.setTextSize(35); // 글씨 크기
                                count++;
                                int point = 0; // 포인트들이 겹치는 확인 정수 (0이면 아니고 1이면 겹침)
                                if (count >= 2) { // 두번째 점부터
                                    // 원래 있던 점 근처에 찍으면 list처음부터 확인 내꺼랑
                                    // 근처에 점을 못찍도록
                                    for (int i = 0; i < list.size() - 2; i += 2) { // 내꺼 x,y는 빼고 반복문
                                        list.get(count * 2 - 2); // 현재 x
                                        list.get(count * 2 - 1); // 현재 y

                                        point = checkPoint(list.get(count * 2 - 2), list.get(count * 2 - 1), list.get(i), list.get(i + 1));
                                        if (point == 1) { // 근처에 점을 찍으면 x,y 50차이 내
                                            Toast.makeText(MainActivity.this, "이미 포인트가 있습니다.", Toast.LENGTH_SHORT).show();
                                            list.remove(count * 2 - 1);
                                            list.remove(count * 2 - 2);
                                            count--;
                                            break;
                                        }
                                    }
                                }

                                // 점 찍는것
                                for (int i = count * 2 - 2; i < list.size(); i += 2) {
                                    canvas.drawCircle(list.get(i), list.get(i + 1), radius, paint);
                                    if (count < 10) { // 숫자가 10이전
                                        canvas.drawText(String.valueOf(count), (list.get(i)) - 12, (list.get(i + 1)) + 5, paint2);
                                    } else { // 숫자가 10 이상
                                        canvas.drawText(String.valueOf(count), (list.get(i)) - 20, (list.get(i + 1)) + 5, paint2);
                                    }
                                    if (count != 1 && point == 0) { // 점이 두개 이상일때 각도로 시간체크 그리고 점들이 겹치지않는다면
                                        int angle = (int) (getAngle(list.get(i - 2), list.get(i - 1), list.get(i), list.get(i + 1)));
                                        String text = angleToClock(angle, count);
                                        if (text != null) {
                                            textList.add(text);
                                        } else {
                                        }
                                    }
                                }
                                imageView2.setImageBitmap(bitmap2);

                            }
                            // 뷰를 누를 때
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            // 뷰를 누른 후 움직일 때
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (longCheck == true) { // 누른 후 뗄 때 그리고 길게 터치 시
                                list.add((int) event.getX());
                                list.add((int) event.getY());
                                int check = checkCancel(list.get(list.size() - 2), list.get(list.size() - 1));
                                // 내가 터치한 것이 원래 있던 점이랑 같은지
                                if (check != 0) { // 만약 그 점에 맞게 터치했다면
                                    paint.setColor(Color.RED);
                                    paint2.setColor(Color.WHITE);
                                    paint2.setTextSize(35);

                                    bitmap2.eraseColor(Color.TRANSPARENT); // 전체 리셋하고 다시 점 그리기

                                    for (int i = 0; i < check * 2 - 2; i += 2) {
                                        canvas.drawCircle(list.get(i), list.get(i + 1), radius, paint);
                                        if (count < 10) { // 숫자가 10이전
                                            canvas.drawText(String.valueOf((i / 2 + 1)), (list.get(i)) - 12, (list.get(i + 1)) + 5, paint2);
                                        } else { // 숫자가 10 이상
                                            canvas.drawText(String.valueOf((i / 2 + 1)), (list.get(i)) - 20, (list.get(i + 1)) + 5, paint2);
                                        }
                                    }

                                    int size = list.size(); // 리스트 전체 사이즈 저장
                                    for (int i = (size - 1); i > (check * 2 - 3); i--) { // 길게 클릭한 점부터 리스트에서 삭제
                                        list.remove(i);
                                    }
                                    if (check >= 2) { // 체크한 것이 2이상일 경우
                                        int textSize = textList.size();
                                        for (int i = (textSize - 1); i > (check - 2); i--) { // 체크한것부터 -1부터 끝까지 삭제
                                            textList.remove(i);
                                        }
                                    } else { // 체크한 것이 1일 경우
                                        int textSize = textList.size();
                                        for (int i = (textSize - 1); i >= 0; i--) { // 전부 삭제
                                            textList.remove(i);
                                        }
                                    }

                                    count = check - 1; // 체크한 곳으로 카운트
                                    imageView2.setImageBitmap(bitmap2);
                                }
                            }
                            longCheck = false; // longCheck 해제
                        }
                    } else { // lock 잠금되어있을 때
                        if(textList.size() != 0 && event.getAction() == MotionEvent.ACTION_DOWN) { // 텍스트사이즈가 0이 아니거나 뷰를 눌렀을때
                            voice++;
                            if(voice == (textList.size())) { // voice와 텍스트리스트 사이즈가 같으면 마지막이라는 의미
                                // 마지막 speak하고 voice 초기화
                                tts.speak(textList.get(voice-1), TextToSpeech.QUEUE_FLUSH, null, null);
                                voice = 0;
                            } else {
                                tts.speak(textList.get(voice-1), TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    }
                } else { // 사진이 없을 때 imageSet이 0일때
                    Toast.makeText(MainActivity.this, "사진이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
    
    public void reset() {
        int size = list.size();
        int textSize = textList.size();
        if(list.size() != 0) { // 점을 찍은 게 있다면
            for(int i=(size-1); i>=0; i--) { // 점 xy좌표 리스트 삭제
                list.remove(i);
            }
            for(int i=(textSize-1); i>=0; i--) { // 음성메시지 리스트 삭제
                textList.remove(i);
            }
            count=0; // 카운트를 0부터 되도록 (어차피 1 늘릴꺼라)
        }
        else { // 점 찍은 게 없다면
            Toast.makeText(MainActivity.this, "포인트가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 각도를 시간으로 바꿔주는 메서드
    public String angleToClock(int angle, int count) {
        int clock = 12;
        int timeAngle = 165;
        int clock2 = 0;
        String text = null;
        if (-180<=angle && angle<=-165 || 165<angle && angle<=180) { // 12시만 특별히 -180<x<=-165 or 165<=x<180
            text = count + "번은 " + clock + "시에 있습니다.";
        }
        else {
            for(int i=(clock-1); i>0; i--) {
                clock2++;
                timeAngle -= 30;
                if(timeAngle >= 0) { // 시간 각도가 0도 이상일 경우
                    if(timeAngle<angle && angle<=timeAngle+30) {
                        text = count + "번은 " + clock2 + "시에 있습니다.";
                        break;
                    }
                }
                else { // 시간 각도가 0미만일 경우
                    if(timeAngle<angle && angle <=timeAngle+30) {
                        text = count + "번은 " + clock2 + "시에 있습니다.";
                        break;
                    }
                }
            }
        }

        return text;
    }


    public int checkCancel(int x, int y) { // 점 취소 버튼 확인 // 몇번째 점인지 리턴
        int check = 0;
        for(int i=0; i<list.size()-2; i+=2) {
            if(Math.abs(list.get(i)-x) <= 50 && Math.abs(list.get(i+1)-y) <= 50) {
                if(i==0 || i%2==0) { // 0이거나 짝수일때
                    check = (i/2)+1;
                    break;
                }
            } else {
                check = 0;
            }
        }
        return check;
    }

    public int checkPoint(int nowx, int nowy, int checkx, int checky) { // 현재 점이랑 체크 점이랑 같은지 확인 맞으면 1리턴 아니면 0
        int check = 0;
        if(Math.abs(nowx-checkx) <= 50 && Math.abs(nowy-checky) <= 50) { // 현재 x와 체크 x 차이 50이하고 현재y와 체크y 차이가 50이하면
            check = 1;
        } else {
            check = 0;
        }
        return check;
    }

    public static double getAngle(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        double rad = Math.atan2(dx,dy);
        double degree = (rad*180)/Math.PI;

        return degree;
    }



    // 사진 찍기
    public void takePicture() {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(imageTakeIntent);

    }

    // 사진 선택
    public void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        select.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                bitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(bitmap);
                imageSetAF++;
                if(imageSetAF != 0) {
                    if(imageSetBF != imageSetAF) { // 새로운 사진이 세팅되었으면 // 리셋
                        bitmap2.eraseColor(Color.TRANSPARENT);
                        reset();
                    }
                    imageSetBF = imageSetAF; // 이전세팅에 이후세팅 정수 대입
                }
            }
        }
    });

    ActivityResultLauncher<Intent> select = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();

                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imageView.setImageBitmap(bitmap);
                            imageSetAF++;
                            if(imageSetAF != 0) {
                                if(imageSetBF != imageSetAF) { // 새로운 사진이 세팅되었으면 // 리셋
                                    bitmap2.eraseColor(Color.TRANSPARENT);
                                    reset();
                                }
                                imageSetBF = imageSetAF; // 이전세팅에 이후세팅 정수 대입
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    // 결과값 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
        }
    };


}