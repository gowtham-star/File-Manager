package com.example.filemanager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class FileExplorer extends AppCompatActivity {
    File[] finallIST;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        RecyclerView recyclerView = findViewById(R.id.reView);
        SearchView searchView =findViewById(R.id.serachView);
        Button sort = findViewById(R.id.sort);
        searchView.clearFocus();

        String rootDir = getIntent().getStringExtra("folderPath");
        File fileObj = new File(rootDir);

        if (fileObj.listFiles() == null) {
            //No data programatically add not files found
            RelativeLayout layout = findViewById(R.id.relativeLayoutView);

            // Create TextView programmatically.
            TextView textView = new TextView(this);
            textView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setGravity(Gravity.CENTER);
            textView.setText(R.string.no_files);
            layout.addView(textView);
            return;
        }

        this.finallIST = fileObj.listFiles();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                finallIST = filterFiles(s) ;

                notifyAdapter();
                return true;
            }

            public File[] filterFiles(String searchstr) {

                FileFilter logFilefilter = new FileFilter() {
                    public boolean accept(File file) {
                        if (file.getName().contains(searchstr)) {
                            return true;
                        }
                        return false;
                    }
                };
                return fileObj.listFiles(logFilefilter);
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortFiles();
                notifyAdapter();
            }
        });

        notifyAdapter();

    }

    private void sortFiles() {
        Arrays.sort(this.finallIST, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });

    }

    private void notifyAdapter() {
        RecyclerView recyclerView = findViewById(R.id.reView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileObjAdapter(this, this.finallIST));
    }

}