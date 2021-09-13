package cn.icene.sgkapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.icene.sgkapi.util.NetUtil;

public class ChaPhone extends AppCompatActivity {
    private String CP_Code;
//    private String Host = "https://cxx.yun7.me/", Path = "qqxc?phone=";
    private String Host = "https://api.icene.workers.dev/", Path = "qqxc?phone=";
    private EditText CP_input, CP_msg_output;

    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0) {
                String strData = (String) msg.obj;
                parseJsonDataAndShow(strData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 返回箭头
        setContentView(R.layout.activity_cha_phone);
        getSupportActionBar().setTitle("查手机");
        initView();
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
        CP_input = findViewById(R.id.chaphone_code_input);
        CP_msg_output = findViewById(R.id.chaphone_msg_output);
    }

    public void inquireChaPhone(View view) {
        CP_Code = CP_input.getText().toString();

        if(CP_Code.length() == 0) {
            Toast.makeText(ChaPhone.this, "请输入手机号", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChaPhone.this, "查询中...", Toast.LENGTH_SHORT).show();

            String url = Host + Path + CP_Code;
//            String url = "https://v2ray.orzchen.top/api/test.php";
            Log.d("Tag" , "ChaPhone" + url);
            // 做一个耗时任务
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String stringFormaNet = getStringFormNet(url);
//                    Log.d(TAG, "run: "+stringFormaNet);

                    // 像主线程发送
                    Message message = new Message();
                    message.what = 0;
                    message.obj = stringFormaNet;

                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }

    public String getStringFormNet(String url) {
        return NetUtil.doGet(url);
    }

    private void parseJsonDataAndShow(String strData) {
        Log.d("TAG", "ChaPhone" + strData);
        try {
            JSONObject jsonObject = new JSONObject(strData);
            int status_code = jsonObject.optInt("status");
            String message = jsonObject.getString("message");

            if(status_code == 200) {
                String qq = jsonObject.getString("qq");
                String phonediqu = jsonObject.getString("phonediqu");
                String lol = jsonObject.getString("lol");
                String wb = jsonObject.getString("wb");
                String qqlm = jsonObject.getString("qqlm");
                Toast.makeText(ChaPhone.this, message, Toast.LENGTH_SHORT).show();
                CP_msg_output.setText("手机: " + CP_Code + " \nQQ：" + qq + "\n号码归属地：" + phonediqu + "\nLOL：" + lol + "\n微博：" + wb + "\n老密：" + qqlm);
            } else if(status_code == 500) {
                Toast.makeText(ChaPhone.this, message + "状态码 500", Toast.LENGTH_SHORT).show();
                CP_msg_output.setText("数据库中没有相关信息");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void copyMsg(View view) {
        ClipData clip = ClipData.newPlainText("", CP_msg_output.getText());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(clip);
        Toast.makeText(ChaPhone.this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }

}