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

public class M2Q extends AppCompatActivity {
    private String M_Code;
    private EditText M_input, M_msg_output;
    private String mod = "m2q", lay = "json";


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
        setContentView(R.layout.activity_m2_q);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 返回箭头
        getSupportActionBar().setTitle("手机查QQ");
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
        M_input = findViewById(R.id.m_code_input);
        M_msg_output = findViewById(R.id.m_msg_output);
    }

    public void inquireM2Q(View view) {
        M_Code = M_input.getText().toString();
        if(M_Code.length() == 0) {
            Toast.makeText(M2Q.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(M2Q.this, "查询中...", Toast.LENGTH_SHORT).show();

            String url = "https://v2ray.orzchen.top/api/sgk-api.php?mod="+mod+"&uin="+M_Code+"&lay="+lay;
            Log.d("Tag" , "m2q" + url);
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
        Log.d("TAG", "m2q" + strData);
        try {
            JSONObject jsonObject = new JSONObject(strData);
            int status_code = jsonObject.optInt("code");
            String out_data = jsonObject.getString("data");

            JSONObject dataObject = new JSONObject(out_data);
            String uin = dataObject.getString("uin");
            String out = dataObject.getString("out");
//            Log.d("tag" , "q2m" + uin);

            if(status_code == 200) {
                if(out.equals("NULL")){
                    M_msg_output.setText("手机: " + uin + " \nQQ：数据库中无该信息" );
                } else {
                    M_msg_output.setText("手机: " + uin + " \nQQ：" + out);
                }
            } else if(status_code == 500) {
                if(out.equals("NULL")){
                    M_msg_output.setText("手机: " + uin + " \nQQ：数据库中无该信息" );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void copyMsg(View view) {
        ClipData clip = ClipData.newPlainText("", M_msg_output.getText());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(clip);
        Toast.makeText(M2Q.this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }
}