package cn.lemonit.o.environmental_protection_collector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ControlActivity extends AppCompatActivity {

    private ImageView modeChangeImageView;
    private TextView modeNameTextView;
    private TextView smartTypeTextView;

    private LinearLayout manualModeLayout;
    private RelativeLayout manualControlLayout;
    private RelativeLayout smartModeLayout;

    private ImageView manualFanImageView;
    private TextView manualFanValueTextView;
    private TextView manualPressureValueTextView;
    private TextView manualTemperatureValueTextView;
    private TextView smartPressureValueTextView;
    private TextView smartTemperatureValueTextView;

    private Button manualControlPressureSubButton;
    private Button manualControlPressurePlusButton;
    private Button manualControlTemperatureSubButton;
    private Button manualControlTemperaturePlusButton;

    private static final Integer MIN_PRESSURE_VALUE = 20;
    private static final Integer MAX_PRESSURE_VALUE = 420;
    private static final Integer DEFAULT_PRESSURE_VALUE = 20;
    private static final Integer MIN_TEMPERATURE_VALUE = 20;
    private static final Integer MAX_TEMPERATURE_VALUE = 130;
    private static final Integer DEFAULT_TEMPERATURE_VALUE = 20;

    private Boolean isSmartMode = false;
    private Integer smartType = 0;
    private Integer pressureValue = DEFAULT_PRESSURE_VALUE;
    private Integer temperatureValue = DEFAULT_TEMPERATURE_VALUE;
    private Timer smartTimer;
    private TimerTask smartTimerTask;
    private Timer manualFanTimer;
    private TimerTask manualFanTimerTask;
    private Boolean manualFanState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        bindViews();
        bindListener();
        this.toggleSmartMode();
    }

    private void bindViews() {
        this.modeChangeImageView = findViewById(R.id.mode_change_image_view);
        this.modeNameTextView = findViewById(R.id.mode_name_text_view);
        this.smartTypeTextView = findViewById(R.id.smart_type_text_view);

        this.manualModeLayout = findViewById(R.id.manual_mode_layout);
        this.manualControlLayout = findViewById(R.id.manual_control_layout);
        this.smartModeLayout = findViewById(R.id.smart_mode_layout);

        this.manualFanImageView = findViewById(R.id.manual_fan_image_view);
        this.manualFanValueTextView = findViewById(R.id.manual_fan_value_text_view);
        this.manualPressureValueTextView = findViewById(R.id.manual_pressure_value_text_view);
        this.manualTemperatureValueTextView = findViewById(R.id.manual_temperature_value_text_view);
        this.smartPressureValueTextView = findViewById(R.id.smart_pressure_value_text_view);
        this.smartTemperatureValueTextView = findViewById(R.id.smart_temperature_value_text_view);

        this.manualControlPressureSubButton = findViewById(R.id.manual_control_pressure_sub_button);
        this.manualControlPressurePlusButton = findViewById(R.id.manual_control_pressure_plus_button);
        this.manualControlTemperatureSubButton = findViewById(R.id.manual_control_temperature_sub_button);
        this.manualControlTemperaturePlusButton = findViewById(R.id.manual_control_temperature_plus_button);
    }

    private void bindListener() {
        // 手动模式、智能模式切换
        this.modeChangeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSmartMode();
            }
        });
        // 智能模式的状态切换
        this.smartTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSmartType();
            }
        });
        // 手动模式风扇开关
        this.manualFanImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setManualFanState(!manualFanState);
            }
        });
        // 手动模式的加减按钮
        this.manualControlPressureSubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressureValue(pressureValue - 1);
            }
        });
        this.manualControlPressurePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressureValue(pressureValue + 1);
            }
        });
        this.manualControlTemperatureSubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTemperatureValue(temperatureValue - 1);
            }
        });
        this.manualControlTemperaturePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTemperatureValue(temperatureValue + 1);
            }
        });
    }

    /**
     * 在手动和智能模式之间切换
     */
    private void toggleSmartMode() {
        this.isSmartMode = !this.isSmartMode;
        this.modeNameTextView.setText(this.isSmartMode ? "智能模式" : "手动模式");
        this.manualModeLayout.setVisibility(this.isSmartMode ? View.GONE : View.VISIBLE);
        this.manualControlLayout.setVisibility(this.isSmartMode ? View.GONE : View.VISIBLE);
        this.smartModeLayout.setVisibility(this.isSmartMode ? View.VISIBLE : View.GONE);
        this.smartTypeTextView.setVisibility(this.isSmartMode ? View.VISIBLE : View.GONE);
        this.setPressureValue(DEFAULT_PRESSURE_VALUE);
        this.setTemperatureValue(DEFAULT_TEMPERATURE_VALUE);
        if (this.isSmartMode) {
            this.resetSmartType();
        } else {
            this.smartTimer.cancel();
            this.setManualFanState(false);
        }
    }

    private void resetSmartType() {
        if (this.smartTimer != null) {
            this.smartTimer.cancel();
        }
        this.smartType = -1;
        this.toggleSmartType();
    }


    private void toggleSmartType() {
        this.smartType = (this.smartType + 1) % 3;
        switch (this.smartType) {
            case 0:
                smartTypeUp();
                break;
            case 1:
                smartTypeDown();
                break;
            default:
                smartTypeStop();
        }
    }

    private void smartTypeUp() {
        this.smartTypeTextView.setText("压紧加热中...");
        this.initSmartTimer();
        this.smartTimer.schedule(this.smartTimerTask, 0, 500);
    }

    private void smartTypeDown() {
        this.smartTypeTextView.setText("冷却中...");
    }

    private void smartTypeStop() {
        this.smartTypeTextView.setText("混压结束");
        this.smartTimer.cancel();
        this.setTemperatureValue(DEFAULT_TEMPERATURE_VALUE);
        this.setPressureValue(DEFAULT_PRESSURE_VALUE);
    }

    private void initSmartTimer() {
        this.smartTimer = new Timer();
        this.smartTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPressureValue(pressureValue + (1 + (int) Math.floor(Math.random() * 2)) * (smartType == 0 ? 1 : -1));
                        setTemperatureValue(temperatureValue + (1 + (int) Math.floor(Math.random() * 4)) * (smartType == 0 ? 1 : -1));
                    }
                });
            }
        };
    }

    private void setManualFanState(Boolean newState) {
        this.manualFanValueTextView.setText(newState ? "ON" : "OFF");
        if (newState) {
            initManualFanTimer();
            this.manualFanTimer.schedule(this.manualFanTimerTask, 0, 500);
        } else {
            if (this.manualFanTimer != null) {
                this.manualFanTimer.cancel();
            }
        }
    }

    private void initManualFanTimer() {
        this.manualFanTimer = new Timer();
        this.manualFanTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPressureValue(pressureValue - (1 + (int) Math.floor(Math.random() * 2)));
                        setTemperatureValue(temperatureValue - (1 + (int) Math.floor(Math.random() * 2)));
                        if (pressureValue.equals(MIN_PRESSURE_VALUE) && temperatureValue.equals(MIN_TEMPERATURE_VALUE)) {
                            // 都降到最低值了，停止风扇
                            Toast.makeText(ControlActivity.this, "压力与温度已达最低值，自动关闭风扇", Toast.LENGTH_SHORT).show();
                            setManualFanState(false);
                        }
                    }
                });
            }
        };
    }

    private void setPressureValue(Integer pressureValue) {
        this.pressureValue = Math.max(MIN_PRESSURE_VALUE, Math.min(MAX_PRESSURE_VALUE, pressureValue));
        this.manualPressureValueTextView.setText(String.valueOf(this.pressureValue));
        this.smartPressureValueTextView.setText(String.valueOf(this.pressureValue));
    }

    private void setTemperatureValue(Integer temperatureValue) {
        this.temperatureValue = Math.max(MIN_TEMPERATURE_VALUE, Math.min(MAX_TEMPERATURE_VALUE, temperatureValue));
        this.smartTemperatureValueTextView.setText(String.valueOf(this.temperatureValue));
        this.manualTemperatureValueTextView.setText(String.valueOf(this.temperatureValue));
    }

}

