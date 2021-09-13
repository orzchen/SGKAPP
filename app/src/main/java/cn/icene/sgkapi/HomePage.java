package cn.icene.sgkapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {
    private long exitTime = 0;
    private String SecID;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().setTitle("查询入口");

        this.operateIntent();
    }

    public void operateIntent() {
        // 获取newSecID
        Intent intent = getIntent();
        SecID = intent.getStringExtra("SecID");
    }

    public void toMainMeun(View view) {
        Intent intent = new Intent(HomePage.this, MainMeun.class);
        intent.putExtra("SecID", SecID);
        startActivity(intent);
    }

    public void toMenu2(View view) {
        Intent intent = new Intent(HomePage.this, Menu2.class);
        intent.putExtra("SecID", SecID);
        startActivity(intent);
    }
}