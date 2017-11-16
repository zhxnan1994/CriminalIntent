package com.zhang.shaon.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;

/**
 * Created by zhang on 2017-11-16.
 */

public class PhotoDialogFragment extends DialogFragment {

    private static final String ARG_PATH = "path";
    private ImageView mPhoto;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_dialog, null);
        String path=(String) getArguments().getSerializable(ARG_PATH);
        //Uri uri=(Uri) Uri.parse(path);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path,
                getActivity());
        mPhoto = (ImageView) v.findViewById(R.id.crime_photo_zoom);
        if(bitmap!=null)
            mPhoto.setImageBitmap(bitmap);
        return  new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    public static PhotoDialogFragment newInstance(String path){
        Bundle args=new Bundle();
        args.putSerializable(ARG_PATH, path);

        PhotoDialogFragment fragment=new PhotoDialogFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    private void sendResult(int resultCode){

        if(getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
