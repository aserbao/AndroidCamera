package com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.decodeEditEncode.DecodeEditEncodeActivity;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoPlayerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能:
 * author aserbao
 * date : On 2018/11/23
 * email: 1142803753@qq.com
 */
public abstract class BigFlakeBaseActivity extends AppCompatActivity {
    public String mOutputPath;
    @BindView(R.id.start)
    public Button mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigflake_item);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.start, R.id.player})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start:
                if(mStart.getText().equals("开始录制")) {
//                    runOnUiThread(new Runnable() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showToast("请等待5s左右,完成会显示Toast,请稍等……");
                                excute();
                            } catch (Exception e) {
                                showToast("报异常了"+ e.toString());
                                e.printStackTrace();
                            } catch (Throwable throwable) {
                                showToast("报异常了");
                                throwable.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    mStart.setText("录制完成");
                }
                break;
            case R.id.player:
                if (!TextUtils.isEmpty(mOutputPath)) {
                    VideoPlayerActivity.launch(BigFlakeBaseActivity.this, mOutputPath);
                }
                break;
        }
    }
    public void showToast(final String content){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BigFlakeBaseActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public abstract void excute() throws Throwable;
}
