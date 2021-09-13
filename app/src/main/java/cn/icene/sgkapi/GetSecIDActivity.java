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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.icene.sgkapi.util.NetUtil;

import static android.content.ContentValues.TAG;

public class GetSecIDActivity extends AppCompatActivity {

    private EditText getSecID_Code;
    private EditText outPutSecID;

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

//    {"SecID":"OUlxvgJKCdWuQmaD9RNnhYH4s","statu":1}
    public void parseJsonDataAndShow(String jsonStr) {

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            String secID = jsonObject.optString("SecID");
            int statuCode = jsonObject.optInt("statu");

            // 显示json数据
            if(statuCode == 1) {
                Toast.makeText(GetSecIDActivity.this, "SecID申请成功", Toast.LENGTH_SHORT).show();
                outPutSecID.setText(secID);
            } else if(statuCode == 0) {
                Toast.makeText(GetSecIDActivity.this, "申请验证码错误", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sec_i_d);
        getSupportActionBar().setTitle("获取SecID");
        initView();

    }

    private void initView() {
        outPutSecID = findViewById(R.id.get_sec_id_output);
        outPutSecID.setFocusable(false);
        getSecID_Code = findViewById(R.id.get_sec_id_input);
    }

    public void toMainActivity(View view) {
        if(outPutSecID.getText().toString().length() == 0){
            Toast.makeText(this, "你还没有申请SecID", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(GetSecIDActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        //这里可以传递参数

//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
        //intent.setClassName();
//        startActivity(intent);

        // 参数传递
        String valueText = String.valueOf(outPutSecID.getText());
        intent.putExtra("newSecID", valueText);
//        Log.d(TAG, "toMainActivity: " + outPutSecID.getText());
//        intent.putExtra("newSecID", "jfkdfj");

        startActivity(intent);

    }


    public void getNewSecID(View view) {
        // 判读一下验证码框
//        Log.d(TAG, "getNewSecID: " + getSecID_Code.getText().toString().length() );
//        Log.d(TAG, "equals: " + (getSecID_Code.getText().toString().length() != 0));
        if(getSecID_Code.getText().toString().length() != 0)
        {
            Toast.makeText(GetSecIDActivity.this, "正在申请SecID", Toast.LENGTH_SHORT).show();
            String code = getSecID_Code.getText().toString();
            String url = "https://v2ray.orzchen.top/api/get-secid.php?code=" + code;
            // 做一个耗时任务
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String stringFormaNet = getStringFormNet(url);
//                    Log.d(TAG, "run: "+stringFormaNet);
//                    outPutSecID.setText(stringFormat);

                    // 像主线程发送
                    Message message = new Message();
                    message.what = 0;
                    message.obj = stringFormaNet;

                    mHandler.sendMessage(message);
                }
            }).start();

//            Toast.makeText(this, "申请SecID成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "你还没有输入验证码呢！", Toast.LENGTH_SHORT).show();
        }

    }

    public String getStringFormNet(String url) {
            return NetUtil.doGet(url);
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
}