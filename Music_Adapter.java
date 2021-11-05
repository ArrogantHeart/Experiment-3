package com.example.experiment3.MusicAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.music.R;

import java.util.List;

public class Music_Adapter extends ArrayAdapter<Music_list> {
    private int resourceId;


    public Music_Adapter(@NonNull Context context, int textViewResourceId, @NonNull List<Music_list> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View converView, ViewGroup parent){
        Music_list music_list = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView musicName = (TextView)view.findViewById(R.id.Name_music);
        musicName.setText(music_list.getName());
        return view;
    }
}
