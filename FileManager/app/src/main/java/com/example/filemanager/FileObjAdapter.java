package com.example.filemanager;

import static java.lang.Thread.sleep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

public class FileObjAdapter extends RecyclerView.Adapter<FileObjAdapter.ViewHolder> {

    Context context;
    File[] listOfFilesFolders;
    public FileObjAdapter(Context context, File[] listOfFilesFolders){
        this.context=context;
        this.listOfFilesFolders=listOfFilesFolders;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        File selectedFile = listOfFilesFolders[position];

        holder.textView.setText(selectedFile.getName() );

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.baseline_folder_24);
        }
        else{
            holder.imageView.setImageResource(R.drawable.baseline_file_present_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedFile.isDirectory()){
                    Intent intent1 = new Intent(context, FileExplorer.class);
                    String path = selectedFile.getAbsolutePath();
                    intent1.putExtra("folderPath", path);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
                else{
                    try {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(selectedFile.getAbsolutePath()));
                        MimeTypeMap map = MimeTypeMap.getSingleton();
                        String ext = MimeTypeMap.getFileExtensionFromUrl(selectedFile.getName());
                        String type = map.getMimeTypeFromExtension(ext);
                        intent.setType(type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch(Exception e){
                        Toast.makeText(context.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        Toast.makeText(context.getApplicationContext(),"Unfortunaley file cannot be opened...:(",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenu().add("create");
                popupMenu.getMenu().add("rename");
                popupMenu.getMenu().add("delete");

                popupMenu.setOnMenuItemClickListener(
                        new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if(menuItem.getTitle().equals("delete")){
                                    boolean deleted = selectedFile.delete();
                                    if(deleted){
                                        Toast.makeText(context.getApplicationContext(),"Deleted Sucessfully",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context,FileExplorer.class);
                                        String path = selectedFile.getParent();
                                        intent.putExtra("folderPath", path);
                                        context.startActivity(intent);
                                    }
                                }
                                if(menuItem.getTitle().equals("create")){
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setCancelable(true);
                                    dialog.setTitle("Enter the file name:");
                                    final EditText editText = new EditText(context);
                                    dialog.setView(editText);
                                    dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String newFolder= selectedFile.getParent();
                                            newFolder  = newFolder + "/" +  editText.getText().toString();
                                             File file =  new File(newFolder);
                                            try {
                                                file.createNewFile();
                                            } catch (IOException e) {
                                                Toast.makeText(context.getApplicationContext(),"Failed creating a new file: " + editText.getText().toString() ,Toast.LENGTH_SHORT).show();
                                                throw new RuntimeException(e);
                                            }
                                            Toast.makeText(context.getApplicationContext(),"Created a new  file" + editText.getText().toString() ,Toast.LENGTH_SHORT).show();
                                            try {
                                                sleep(60);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                            Intent intent = new Intent(context,FileExplorer.class);
                                            String path = selectedFile.getParent();
                                            intent.putExtra("folderPath", path);
                                            context.startActivity(intent);
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // do nothing
                                        }
                                    });
                                    dialog.show();
                                }
                                if(menuItem.getTitle().equals("rename")){
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setCancelable(true);
                                    dialog.setTitle("Rename the file name to:");
                                    final EditText editText = new EditText(context);
                                    dialog.setView(editText);
                                    dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String newFolder= selectedFile.getParent();
                                            newFolder  = newFolder + "/" +  editText.getText().toString();
                                            selectedFile.renameTo(new File(newFolder));
                                            Toast.makeText(context.getApplicationContext(),"Renamed to" + editText.getText().toString() ,Toast.LENGTH_SHORT).show();
                                            try {
                                                sleep(60);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                            Intent intent = new Intent(context,FileExplorer.class);
                                            String path = selectedFile.getParent();
                                            intent.putExtra("folderPath", path);
                                            context.startActivity(intent);
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // do nothing
                                        }
                                    });
                                    dialog.show();
                                }
                                return true;
                            }
                        }
                );

                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfFilesFolders.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textV);
            imageView = itemView.findViewById(R.id.Image_View);
        }
    }
}
