package com.example.abc.random_videocall_application;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class SpacesItemDecoration extends  RecyclerView.ItemDecoration {
private int space;
private String fromWhere = "";

public SpacesItemDecoration(int space) {
        this.space = space;
        }
public SpacesItemDecoration(int space, String fromWhere) {

        this.space = space;
        this.fromWhere = fromWhere;
        }
@Override
public void getItemOffsets(Rect outRect, View view,
                           RecyclerView parent, RecyclerView.State state) {
        if(fromWhere.equals("")) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;
        }else {
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = space;
        outRect.top = space;
        }

        }
        }
