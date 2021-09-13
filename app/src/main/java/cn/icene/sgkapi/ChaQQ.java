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

public class ChaQQ extends AppCompatActivity {
    private String CQ_Code;
//    private String Host = "https://cxx.yun7.me/", Path = "qqcx?qq=";
    private String Host = "https://api.icene.workers.dev/", Path = "qqcx?qq=";
    private EditText CQ_input, CQ_msg_output;

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
        setContentView(R.layout.activity_cha_q_q);
        getSupportActionBar().setTitle("查QQ");
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
        CQ_input = findViewById(R.id.chaqq_code_input);
        CQ_msg_output = findViewById(R.id.chaqq_msg_output);
    }

    public void inquireChaQQ(View view) {
        CQ_Code = CQ_input.getText().toString();

        if(CQ_Code.length() == 0) {
            Toast.makeText(ChaQQ.this, "请输入QQ号", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChaQQ.this, "查询中...", Toast.LENGTH_SHORT).show();

            String url = Host + Path + CQ_Code;
            Log.d("Tag" , "ChaQQ" + url);
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
//        Log.d("TAG", "ChaQQ" + strData);
        try {
            JSONObject jsonObject = new JSONObject(strData);
            int status_code = jsonObject.optInt("status");
            String message = jsonObject.getString("message");

            if(status_code == 200) {
                String phone = jsonObject.getString("phone");
                String phonediqu = jsonObject.getString("phonediqu");
                String lol = jsonObject.getString("lol");
                String wb = jsonObject.getString("wb");
                String qqlm = jsonObject.getString("qqlm");
                Toast.makeText(ChaQQ.this, message, Toast.LENGTH_SHORT).show();
                CQ_msg_output.setText("QQ: " + CQ_Code + " \n手机：" + phone + "\n号码归属地：" + phonediqu + "\nLOL：" + lol + "\n微博：" + wb + "\n老密：" + qqlm);
            } else if(status_code == 500) {
                Toast.makeText(ChaQQ.this, message + "状态码 500", Toast.LENGTH_SHORT).show();
                CQ_msg_output.setText("数据库中没有相关信息");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void copyMsg(View view) {
        ClipData clip = ClipData.newPlainText("", CQ_msg_output.getText());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(clip);
        Toast.makeText(ChaQQ.this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }


}