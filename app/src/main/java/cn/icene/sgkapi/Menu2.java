package cn.icene.sgkapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

//查Q绑、查电话、查LOL
public class Menu2 extends AppCompatActivity {
    private String SecID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 返回箭头
        getSupportActionBar().setTitle("功能");
        initView();

        this.operateIntent();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void operateIntent() {
        // 获取newSecID
        Intent intent = getIntent();
        SecID = intent.getStringExtra("SecID");
    }

    private void initView() {
    }


    public void toChaLOL(View view) {
        Intent intent = new Intent(Menu2.this, ChaLOL.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }

    public void toChaQQ(View view) {
        Intent intent = new Intent(Menu2.this, ChaQQ.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }


    public void toChaPhone(View view) {
        Intent intent = new Intent(Menu2.this, ChaPhone.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }
}