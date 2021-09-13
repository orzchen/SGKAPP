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

public class ChaLOL extends AppCompatActivity {
    private String LOL_id, LOL2QQ;
    private EditText CL_input, CL_msg_output;
    private String Host_1 = "https://api.icene.workers.dev/", Path_1 = "qqcx?qq=";
    private String Host_2 = "https://v2ray.orzchen.top/api/sgk-api.php?mod=l2q&lay=json&uin=";
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
        setContentView(R.layout.activity_cha_l_o_l);
        getSupportActionBar().setTitle("查LOL");

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
        CL_input = findViewById(R.id.chalol_code_input);
        CL_msg_output = findViewById(R.id.chalol_msg_output);
    }

    public void inquireChaLOL(View view) {
        LOL_id = CL_input.getText().toString();

        if(LOL_id.length() == 0) {
            Toast.makeText(ChaLOL.this, "请输入LOL昵称", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChaLOL.this, "查询中...", Toast.LENGTH_SHORT).show();

            String url = Host_2 + LOL_id;
//            String url = "https://v2ray.orzchen.top/api/test.php";
            Log.d("Tag" , "ChaLOL" + url);
            // 做一个耗时任务
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String stringFormaNet = getStringFormNet(url);
//                    Log.d("TAG", "run: "+stringFormaNet);
                    Log.d("TAG", "runQQ: "+parseJsonData(stringFormaNet));
                    LOL2QQ = parseJsonData(stringFormaNet);
                    Log.d("TAG", "run: "+LOL2QQ);
                    inquireChaLOLtoMore(LOL2QQ);
                    // 像主线程发送
//                    Message message = new Message();
//                    message.what = 0;
////                    message.obj = stringFormaNet;
//                    message.obj = strFormnet;
//
//                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }

    public void inquireChaLOLtoMore(String strData) {
        String url = Host_1 + Path_1 + strData;
        // 做一个耗时任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                String stringFormaNet = getStringFormNet(url);
                    Log.d("TAG", "run: "+stringFormaNet);

                 //像主线程发送
                Message message = new Message();
                message.what = 0;
                message.obj = stringFormaNet;

                mHandler.sendMessage(message);
            }
        }).start();

    }

    public String getStringFormNet(String url) {
        return NetUtil.doGet(url);
    }

    private String parseJsonData(String strData) {
        String requslt = "";
        try {
            JSONObject jsonObject = new JSONObject(strData);
            int status_code = jsonObject.optInt("code");
            String out_data = jsonObject.getString("data");

            JSONObject dataObject = new JSONObject(out_data);
            String out = dataObject.getString("out");

            requslt = requslt + out;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requslt;
    }

    private void parseJsonDataAndShow(String strData) {
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
                Toast.makeText(ChaLOL.this, message, Toast.LENGTH_SHORT).show();
                CL_msg_output.setText( "LOL：" + lol + "\nQQ: " + LOL2QQ + " \n手机：" + phone + "\n号码归属地：" + phonediqu +"\n微博：" + wb + "\n老密：" + qqlm);
            } else if(status_code == 500) {
                Toast.makeText(ChaLOL.this, message + "状态码 500", Toast.LENGTH_SHORT).show();
                CL_msg_output.setText("数据库中没有相关信息");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void copyMsg(View view) {
        ClipData clip = ClipData.newPlainText("", CL_msg_output.getText());
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);// 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(clip);
        Toast.makeText(ChaLOL.this, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
    }

}