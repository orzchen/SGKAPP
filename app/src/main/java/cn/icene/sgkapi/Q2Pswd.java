package cn.icene.sgkapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import cn.icene.sgkapi.util.NetUtil;

public class Q2Pswd extends AppCompatActivity {
    private String QQ_Code, SecID;
    private EditText QQ_input, QQ_msg_output;
    private String mod = "pswd", lay = "json";


    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0) {
                String strData = (String) msg.obj;
                parseJsonDataAndShow(strData);
//                outPutSecID.setText(strData);

//                Toast.makeText(GetSecIDActivity.this, "SecID申请成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q2_pswd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 返回箭头
        getSupportActionBar().setTitle("QQ老密查询");

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
        QQ_input = findViewById(R.id.q2pswd_code_input);
        QQ_msg_output = findViewById(R.id.q2pswd_msg_output);
    }

    public void operateIntent() {
        // 获取newSecID
        Intent intent = getIntent();
        SecID = intent.getStringExtra("SecID");
//        Log.d(TAG, SecID);
    }

    public void inquireQ2M(View view) {
        QQ_Code = QQ_input.getText().toString();
        if(QQ_Code.length() == 0) {
            Toast.makeText(Q2Pswd.this, "请输入QQ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Q2Pswd.this, "查询中...", Toast.LENGTH_SHORT).show();

            String url = "https://v2ray.orzchen.top/api/sgk-api.php?mod="+mod+"&uin="+QQ_Code+"&lay="+lay;
            Log.d("Tag" , "q2m" + url);
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

    public void parseJsonDataAndShow(String jsonStr) {

        Log.d("TAG", "q2m" + jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            int status_code = jsonObject.optInt("code");
            String out_data = jsonObject.getString("data");

            JSONObject dataObject = new JSONObject(out_data);
            String uin = dataObject.getString("uin");
            String out = dataObject.getString("out");
//            Log.d("tag" , "q2m" + uin);

            if(status_code == 200) {
                if(out.equals("NULL")){
                    QQ_msg_output.setText("QQ: " + uin + " \n老密：数据库中无该信息" );
                } else {
                    QQ_msg_output.setText("QQ: " + uin + " \n老密：" + out);
                }
            } else if(status_code == 500) {
                if(out.equals("NULL")){
                    QQ_msg_output.setText("QQ: " + uin + " \n老密：数据库中无该信息" );
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void copyMsg(View view) {
        ClipData clip = ClipData.newPlainText("", QQ_msg_output.getText());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(clip);
        Toast.makeText(Q2Pswd.this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }

}