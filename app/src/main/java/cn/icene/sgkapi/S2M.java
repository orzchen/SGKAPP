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

public class S2M extends AppCompatActivity {
    private String S_Code;
    private EditText S_input, S_msg_output;
    private String mod = "s2m", lay = "json";


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
        setContentView(R.layout.activity_s2_m);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 返回箭头
        getSupportActionBar().setTitle("微博查手机");
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
        S_input = findViewById(R.id.s_code_input);
        S_msg_output = findViewById(R.id.s_msg_output);
    }

    public void inquireS2M(View view) {
        S_Code = S_input.getText().toString();
        if(S_Code.length() == 0) {
            Toast.makeText(S2M.this, "请输入微博UID", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(S2M.this, "查询中...", Toast.LENGTH_SHORT).show();

            String url = "https://v2ray.orzchen.top/api/sgk-api.php?mod="+mod+"&uin="+S_Code+"&lay="+lay;
            Log.d("Tag" , "s2m" + url);
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
                    S_msg_output.setText("微博UID: " + uin + " \n手机：数据库中无该信息" );
                } else {
                    S_msg_output.setText("微博UID: " + uin + " \n手机：" + out + "\n链接：https://weibo.com/u/" + uin);
                }
            } else if(status_code == 500) {
                if(out.equals("NULL")){
                    S_msg_output.setText("微博UID: " + uin + " \n手机：数据库中无该信息" );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void copyMsg(View view) {
        ClipData clip = ClipData.newPlainText("", S_msg_output.getText());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(clip);
        Toast.makeText(S2M.this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }
}