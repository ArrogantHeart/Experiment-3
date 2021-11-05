package com.example.experiment3;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.experiment3.Download.DownloadService;
import com.example.experiment3.MusicAdapter.Music_Adapter;
import com.example.experiment3.MusicAdapter.Music_list;
import com.example.music.R;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private List<Music_list> musicLists = new ArrayList<>();
    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        }
        Intent intent = new Intent(getApplicationContext(), DownloadService.class);
        bindService(intent, connection, BIND_AUTO_CREATE); // 绑定服务
        initMusic();
        Music_Adapter adapter = new Music_Adapter(MainActivity.this,
                R.layout.music_list,musicLists);

        ListView listView = findViewById(R.id.Music_View);
        listView.setAdapter(adapter);

        startService(intent); // 启动服务


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music_list musicItem = musicLists.get(position);
                String file = musicItem.getDownloadUri().split("/")[4] + ".mp3";
                String url = searchFile(file);
                Log.d(TAG,"onItemClick:"+url);
//                downloadBinder.startDownload(url);
                if (url == null){
                    //提示
                    Toast.makeText(MainActivity.this,"歌曲未下载",Toast.LENGTH_SHORT).show();
                    //开始下载歌曲
                    downloadBinder.startDownload(musicItem.getDownloadUri());
                }
                else {
                    //存在则跳转到播放界面
                    Intent playIntent = new Intent(MainActivity.this,MyMusic.class);
                    playIntent.putExtra("list", (Serializable) musicLists);
                    playIntent.putExtra("position",position);
                    startActivity(playIntent);
                }
            }
        });
    }
//    https://freemusicarchive.org/track/tochter-zion-freue-dich/download
//    https://freemusicarchive.org/track/the-empress-of-china/download
//    https://freemusicarchive.org/track/Julie_Maxwells_piano_music_-_Piano_Soul_-_25_ppp/download
    private void initMusic(){
        for(int i = 0;i < 1;i++){
            Music_list music_1 = new Music_list("tochter-zion-freue-dich","/sdcard/Download/tochter-zion-freue-dich.mp3","https://freemusicarchive.org/track/tochter-zion-freue-dich/download");
            musicLists.add(music_1);
            Music_list music_2 = new Music_list("the-empress-of-china","/sdcard/Download/the-empress-of-china.mp3","https://freemusicarchive.org/track/the-empress-of-china/download");
            musicLists.add(music_2);
            Music_list music_3 = new Music_list("Julie_Maxwells_piano_music_-_Piano_Soul_-_25_ppp","/sdcard/Download/Julie_Maxwells_piano_music_-_Piano_Soul_-_25_ppp.mp3","https://freemusicarchive.org/track/Julie_Maxwells_piano_music_-_Piano_Soul_-_25_ppp/download");
            musicLists.add(music_3);
            Music_list music_4 = new Music_list("Hurrycane","/sdcard/Download/Hurrycane.mp3","https://freemusicarchive.org/track/Hurrycane/download");
            musicLists.add(music_4);
            Music_list music_5 = new Music_list("Happy Mornings","/sdcard/Download/happy-mornings.mp3","https://freemusicarchive.org/track/happy-mornings/download");
            musicLists.add(music_5);
            Music_list music_6 = new Music_list("Lessons","/sdcard/Download/lessons.mp3","https://freemusicarchive.org/track/lessons/download");
            musicLists.add(music_6);
            Music_list music_7 = new Music_list("Celebration","/sdcard/Download/celebration.mp3","https://freemusicarchive.org/track/celebration/download");
            musicLists.add(music_7);
        }
    }
    @Override
    public void onClick(View v) {
        if (downloadBinder == null) {
            return;
        }
        switch (v.getId()) {
//            case R.id.Music_View:
//
//                break;
//            case R.id.Music_View:
//                downloadBinder.pauseDownload();
//                String url = "https://freemusicarchive.org/track/"++"/download";
//                downloadBinder.startDownload(url);
//                break;
//            case R.id.cancel_download:
//                downloadBinder.cancelDownload();
//                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
    private String searchFile(String keyword) {
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
