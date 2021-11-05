package com.example.experiment3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import com.example.experiment3.MusicAdapter.Music_list;
import com.example.music.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyMusic extends AppCompatActivity implements View.OnClickListener {


    private List<Music_list> list;
    private int position;
    private TextView Name;
    private SeekBar seekBar;
    private Timer timer;
    private boolean isCellPlay;/*在挂断电话的时候，用于判断是否为是来电时中断*/
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ObjectAnimator mAnimator;
    private ImageView image;
    private TextView currentPosition;
    private TextView MaxLength;

    SimpleDateFormat format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_item);

        //控件定义
        Name = (TextView) findViewById(R.id.music_name);
        currentPosition = (TextView) findViewById(R.id.music_cur);
        MaxLength = (TextView) findViewById(R.id.music_length);
        Button Play = (Button)findViewById(R.id.play);
        Button ChangeUp = (Button)findViewById(R.id.previous);
        Button ChangeNext = (Button)findViewById(R.id.next);
        image = (ImageView) findViewById(R.id.pic);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());


//        获取歌曲列表和点击的音乐位置
        Intent intent = getIntent();
        list = (List<Music_list>) intent.getSerializableExtra("list");
        position = intent.getIntExtra("position",0);
        format = new SimpleDateFormat("mm:ss");
        Name.setText(list.get(position).getName());


        Play.setOnClickListener(this);
        ChangeUp.setOnClickListener(this);
        ChangeNext.setOnClickListener(this);
        if(ContextCompat.checkSelfPermission(MyMusic.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MyMusic.this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE } ,1 );
        }else{
            initMusicPlayer(position);
            initAnimator();
            mediaPlayer.start();
            mAnimator.start();
            //监听播放时回调函数
            timer = new Timer();
            timer.schedule(new TimerTask() {
                Runnable updateUI = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentPosition.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
                        }catch (Exception e){
                            return;
                        }

                    }
                };
                @Override
                public void run() {
                    if(!isSeekBarChanging){
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        runOnUiThread(updateUI);
                    }
                }
            },0,50);
        }
    }
    private void initMusicPlayer(final int position){
        try {
            mediaPlayer.reset();
            Log.d("查看播放Uri：",list.get(position).getUri());
//            指定音乐路径
            mediaPlayer.setDataSource(list.get(position).getUri());
//            让MediaPlayer进入准备状态
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               public void onPrepared(MediaPlayer mp) {
                   seekBar.setMax(mediaPlayer.getDuration());
                   MaxLength.setText(format.format(mediaPlayer.getDuration())+"");
                   currentPosition.setText("00:00");

               }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//设置图片旋转
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(image, "rotation", 0.0f, 360.0f);
        mAnimator.setDuration(6000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        mAnimator.setInterpolator(new LinearInterpolator());// 匀速
        mAnimator.start();//动画开始
        mAnimator.pause();//动画暂停
    }

    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {//进度条

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }
        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }

    //判断有无权限，没有权限的话退出程序
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.previous:
                if (position == 0){
                    Toast.makeText(this,"现在已经是第一首了",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    position--;
                    String fileName = list.get(position).getDownloadUri().split("/")[4] + ".mp3";
                    if(searchFile(fileName) == null){
                        Toast.makeText(this,"歌曲未下载",Toast.LENGTH_SHORT).show();
                        position++;
                        return;
                    }
                    initMusicPlayer(position);
                    mediaPlayer.start();
                    mAnimator.start();
                    Name.setText(list.get(position).getName());
                }
                break;
            case R.id.play:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start(); //开始播放
                    mAnimator.resume();
                }else {
                    mediaPlayer.pause();
                    mAnimator.pause();
                    timer.purge();//移除所有任务;
                }
                break;

            case R.id.next:
                if (position == list.size()){
                    Toast.makeText(this,"现在已经是最后一首了",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    position++;
                    String fileName = list.get(position).getDownloadUri().split("/")[4] + ".mp3";
                    if(searchFile(fileName) == null){
                        Toast.makeText(this,"歌曲未下载",Toast.LENGTH_SHORT).show();
                        position--;
                        return;
                    }
                    initMusicPlayer(position);
                    mediaPlayer.start();
                    mAnimator.start();
                    Name.setText(list.get(position).getName());
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //活动销毁的时候释放mediaPlayer
        isSeekBarChanging = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }
    private String searchFile(String keyword) { //查找文件是否存在
        String result = null;
        File[] files = new File("/sdcard/Download").listFiles();
        for (File file : files) {
            if (file.getName().equals(keyword)) {
                result = file.getPath();
                break;
            }
        }
        return result;
    }

}