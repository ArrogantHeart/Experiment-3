package com.example.experiment3.MusicAdapter;

import java.io.Serializable;

public class Music_list implements Serializable {
    public String Name;
    public String Uri;
    public String DownloadUri;

    public Music_list() {
    }

    public Music_list(String name, String uri, String downloadUri) {
        Name = name;
        Uri = uri;
        DownloadUri = downloadUri;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getDownloadUri() {
        return DownloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        DownloadUri = downloadUri;
    }

    public Music_list(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
