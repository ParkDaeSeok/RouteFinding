package com.example.routefinding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // recyclerview
//        recyclerView = (RecyclerView) findViewById(R.id.rv);
//        linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        arrayList = new ArrayList<>();
//
//        mainAdapter = new MainAdapter(arrayList);
//        recyclerView.setAdapter(mainAdapter);
//
        Intent intent = getIntent();
        arrayList = (ArrayList<MainData>) intent.getSerializableExtra("list");
//        mainAdapter.notifyDataSetChanged();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout baseLayout = new LinearLayout(this);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        baseLayout.setBackgroundColor(Color.rgb(255,255,255));

        if(arrayList != null) {
            for(int i=0; i<arrayList.size(); i++) {
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout lay1 = new LinearLayout(this);
                lay1.setOrientation(LinearLayout.HORIZONTAL);
                lay1.setBackgroundColor(Color.rgb(255,255,255));

                LinearLayout.LayoutParams textparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView text1 = new TextView(this);
                text1.setText("text1");
                lay1.addView(text1,textparams1);

                LinearLayout.LayoutParams btnparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Button btn1 = new Button(this);
                btn1.setText("취소");
                lay1.addView(btn1,btnparams1);

                baseLayout.addView(lay1,params1);
            }
        }
        setContentView(baseLayout,params);
    }
}
