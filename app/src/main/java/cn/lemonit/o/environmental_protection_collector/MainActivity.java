package cn.lemonit.o.environmental_protection_collector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView centerImageView;
    private TextView deviceConnectStateTextView;

    private static Boolean connectState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.centerImageView = findViewById(R.id.center_image_view);
        this.deviceConnectStateTextView = findViewById(R.id.device_connect_state);

        bindListener();
        setDeviceStateText(connectState);
    }

    private void setDeviceStateText(Boolean connectState) {
        this.deviceConnectStateTextView.setText(connectState ? "设备连接成功" : "设备连接");
    }

    private void bindListener() {
        // 中间貔貅图片点击事件绑定
        this.centerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startControlActivity();
            }
        });
        // 连接设备标签点击事件绑定
        this.deviceConnectStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDevice();
            }
        });
    }

    private void startControlActivity() {
        if (!connectState) {
            Toast.makeText(this, "您还没有连接设备，请连接设备后再使用", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ControlActivity.class));
    }

    private void connectDevice() {
        if (connectState) {
            Toast.makeText(this, "设备已连接，无需重新发起连接", Toast.LENGTH_SHORT).show();
            return;
        }
        connectState = true;
        this.setDeviceStateText(true);
        Toast.makeText(this, "设备连接成功", Toast.LENGTH_SHORT).show();
    }
}
