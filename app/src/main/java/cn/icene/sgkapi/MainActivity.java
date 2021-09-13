package cn.icene.sgkapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.icene.sgkapi.util.NetUtil;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private EditText SecID_Text;
    private CheckBox SecID_Remember, Login_Remember;

    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0) {
                String strData = (String) msg.obj;
                int flag = jsonLoginData(strData);
//                outPutSecID.setText(strData);

//                Toast.makeText(MainActivity.this, "登录请求返回码：" + flag, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, MainMeun.class);
                Intent intent = new Intent(MainActivity.this, HomePage.class);
//                Intent intent = new Intent(MainActivity.this, Meun.class);
                // 不允许返回上一级
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                if(flag== 1) {
                    // 参数传递 getLocalSecID()
                    String valueText = String.valueOf(SecID_Text.getText());
                    intent.putExtra("SecID", valueText);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "登陆成功，状态码 1", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "SecID错误，状态码 0", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("登录");
        initView();

        this.autoLogin();
        this.getSecID();
        this.operateIntent();
    }

    private void initView() {
        SecID_Text = findViewById(R.id.sec_id_input);
        SecID_Remember = findViewById(R.id.cb_secid_remember);
        Login_Remember = findViewById(R.id.cb_login_remember);
    }

    public void toGetSecID(View view) {
        Intent intent = new Intent(MainActivity.this, GetSecIDActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void operateIntent() {
        // 获取newSecID
        Intent intent = getIntent();
//        Log.d(TAG, "Intent : " + intent.getStringExtra("newSecID"));
        Log.d(TAG, "intent" +( intent.getStringExtra("newSecID") != null));
        if(intent.getStringExtra("newSecID")  != null) {
            SecID_Remember.setChecked(true);
            String newSecID = intent.getStringExtra("newSecID");
//            Log.d(TAG, "newSecID ======== " + newSecID);
            saveSecID(newSecID);
        }
    }

    public void saveSecID(String str) {
        SharedPreferences newSecID = getSharedPreferences("SecID", MODE_PRIVATE);
        SharedPreferences.Editor edit = newSecID.edit();
        edit.putString("SecID", str);
//        edit.apply();
        edit.commit();
        if(str != null){
            Toast.makeText(this, "已保存SecID", Toast.LENGTH_SHORT).show();
        }

        SecID_Text.setText(str);
    }

    public void getSecID() {
        SharedPreferences SecID = getSharedPreferences("SecID", MODE_PRIVATE);
        String content = SecID.getString("SecID", "");
//        String checkBox_statu = SecID.getString("checkBox_statu", "");
//        Log.d(TAG, "获取本地SecID" + content);
//        SecID_Remember.setChecked(true);
        SecID_Text.setText(content);
    }

    public String getLocalSecID() {
        SharedPreferences SecID = getSharedPreferences("SecID", MODE_PRIVATE);
        String content = SecID.getString("SecID", "");
        return content;
    }

    public void loginSystem(View view) {
        if(SecID_Text.getText().toString().length() == 0)
        {
            Toast.makeText(this, "你还没有输入SecID呢", Toast.LENGTH_SHORT).show();
            return ;
        } else {
            if(SecID_Remember.isChecked()){
                SharedPreferences newSecID = getSharedPreferences("SecID", MODE_PRIVATE);
                SharedPreferences.Editor edit = newSecID.edit();
                edit.putString("SecID", SecID_Text.getText().toString());
                edit.putBoolean("checkBox_statu", true);
                edit.apply();
            } else {
                SharedPreferences newSecID = getSharedPreferences("SecID", MODE_PRIVATE);
                SharedPreferences.Editor edit = newSecID.edit();
                edit.putBoolean("checkBox_statu", false);
                edit.apply();
            }

            // 跳转到菜单里面 传递SecID
            String SecID = SecID_Text.getText().toString();
            String loginApi = "https://v2ray.orzchen.top/api/judge-secid.php?secid=" + SecID;
            // Intent
            if (SecID != null) {

                // 做一个耗时任务
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String stringFormNet = getStringFormNet(loginApi);
//                        Log.d(TAG, "Login1 : " + loginApi);
//                        int flag = jsonLoginData(stringFormNet);

                        // 像主线程发送
                        Message message = new Message();
                        message.what = 0;
                        message.obj = stringFormNet;

                        mHandler.sendMessage(message);
                    }
                }).start();
            } else {
                Toast.makeText(this, "你还没有输入SecID呢", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public int jsonLoginData(String jsonStr) {
//        int flag = 0;
        Log.d(TAG, "Login : " + jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            int flag = jsonObject.optInt("flag");
            return flag;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long exitTime = 0;

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

    public String getStringFormNet(String url) {
        return NetUtil.doGet(url);
    }

    public void autoLogin() {
        if(Login_Remember.isChecked()) {
            // 跳转到菜单里面 传递SecID
            String SecID = getLocalSecID();
            String loginApi = "https://v2ray.orzchen.top/api/judge-secid.php?secid=" + SecID;
            // Intent
            if (SecID != null) {

                // 做一个耗时任务
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String stringFormNet = getStringFormNet(loginApi);
//                        Log.d(TAG, "Login1 : " + loginApi);
//                        int flag = jsonLoginData(stringFormNet);

                        // 像主线程发送
                        Message message = new Message();
                        message.what = 0;
                        message.obj = stringFormNet;

                        mHandler.sendMessage(message);
                    }
                }).start();
            } else {
                Toast.makeText(this, "你还没有输入SecID呢", Toast.LENGTH_SHORT).show();
            }
        }
    }
}