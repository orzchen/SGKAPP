package cn.icene.sgkapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainMeun extends AppCompatActivity {
    private long exitTime = 0;
    private String SecID;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_meun);
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
    private void initView() {

    }

    public void operateIntent() {
        // 获取newSecID
        Intent intent = getIntent();
        SecID = intent.getStringExtra("SecID");
//        Log.d(TAG, SecID);
    }

    public void toQ2M(View view) {
        Intent intent = new Intent(MainMeun.this, Q2M.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }


    public void toM2Q(View view) {
        Intent intent = new Intent(MainMeun.this, M2Q.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }

    public void toS2M(View view) {
        Intent intent = new Intent(MainMeun.this, S2M.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }

    public void toM2S(View view) {
        Intent intent = new Intent(MainMeun.this, M2S.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }

    public void toQ2Pswd(View view) {
        Intent intent = new Intent(MainMeun.this, Q2Pswd.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }

    public void toQ2L(View view) {
        Intent intent = new Intent(MainMeun.this, Q2L.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }

    public void toL2Q(View view) {
        Intent intent = new Intent(MainMeun.this, L2Q.class);
        // 参数传递
        String valueText = String.valueOf(SecID);
        intent.putExtra("SecID", valueText);
        startActivity(intent);
    }
}