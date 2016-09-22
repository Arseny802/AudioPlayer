package com.arseny.audioplayer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arseny on 08.06.16.
 */
public class FileManager extends ListActivity {

    public static final String LOG_TAG = FileManager.class.getSimpleName();

    final String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private List<String> directoryEntries = new ArrayList<>();
    private File currentDirectory = new File(basePath);

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.file_manager_activity);
        try{
            browseTo(currentDirectory);
        } catch(Exception e) {
            e.printStackTrace();
            currentDirectory = new File("/storage");
            browseTo(currentDirectory);
        }
        onCompletion();

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mActionBarToolbar.setTitle("Files on your phone");
    }

    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null) {
            this.browseTo(this.currentDirectory.getParentFile());
        }
    }

    private void browseTo(final File aDirectory){
        if (aDirectory.isDirectory()){
            this.currentDirectory = aDirectory;
            fill(aDirectory.listFiles());

            TextView titleManager = (TextView) findViewById(R.id.titleManager);
            titleManager.setText(currentDirectory.getAbsolutePath());
            //mActionBarToolbar.setTitle(currentDirectory.getAbsolutePath());
        } else {
            if (aDirectory.getAbsolutePath().endsWith(".mp3")){

                Intent intent = new Intent();
                intent.putExtra("path", aDirectory.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();

            } else {

                DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("file://" + aDirectory.getAbsolutePath()));
                        startActivity(i);
                    }
                };
                //listener when NO button clicked
                DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do nothing
                        //or add something you want
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("Подтверждение")
                        .setMessage("Хотите открыть файл " + aDirectory.getName() + "?")
                        .setPositiveButton("Да", okButtonListener)
                        .setNegativeButton("Нет", cancelButtonListener)
                        .show();
            }
        }
    }

    private void fill(File[] files) {
        this.directoryEntries.clear();

        if (this.currentDirectory.getParent() != null)
            this.directoryEntries.add("..");

        for (File file : files) {
            this.directoryEntries.add(file.getAbsolutePath());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<>
                (this, R.layout.file_manager_activity_helper, this.directoryEntries);
        this.setListAdapter(directoryList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selectedFileString = this.directoryEntries.get(position);

        if(selectedFileString.equals("..")){
            this.upOneLevel();
        } else {
            File clickedFile = new File(selectedFileString);
            this.browseTo(clickedFile);
        }
    }

    public void onCompletion() {Log.e(LOG_TAG, basePath);}
}
