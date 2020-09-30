package com.dipay.dipaygalery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {
    @NonNull

    private Context context;

    ArrayList<Bitmap> imgList;


    public RecyclerAdapter(ArrayList<Bitmap> imgList, Context context){
        this.imgList = imgList;
        this.context = context;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout, parent, false);
        ImageViewHolder imageViewHolder  = new ImageViewHolder(view, context, imgList);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.Album.setImageBitmap(imgList.get(position));
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        ImageView Album;
        Context context;
        ArrayList<Bitmap> imgList;


        public ImageViewHolder(@NonNull View itemView, Context context, ArrayList<Bitmap> imgList) {
            super(itemView);
            Album = itemView.findViewById(R.id.album);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.context = context;
            this.imgList = imgList;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, DisplayActivity.class);
            intent.putExtra("image_id", imgList.get(getAdapterPosition()));
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            imgList.remove(getAdapterPosition());
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        }

    }


}
