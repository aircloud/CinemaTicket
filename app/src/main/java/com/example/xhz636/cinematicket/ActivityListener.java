package com.example.xhz636.cinematicket;

import android.content.Intent;

public interface ActivityListener {

    void activityListener(int requestCode, int resultCode, Intent intent);

    void reloadData();

}
