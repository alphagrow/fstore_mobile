package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    ArrayList<Bitmap> imagesUriArrayList;

    ImageView imageview;
    Bitmap bitmap = null;
    Context context;

    public ImageAdapter(Context context, ArrayList<Bitmap> contacts) {
        imagesUriArrayList = contacts;
        context = context;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView ima;

        public ViewHolder(View itemView) {
            super(itemView);

            ima = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.listview, parent, false);
        ImageAdapter.ViewHolder viewHolder = new ImageAdapter.ViewHolder(contactView);
        return viewHolder;
    }




    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Bitmap selectedImage = imagesUriArrayList.get(position);

//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),selectedImage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        holder.ima.setImageBitmap(selectedImage);
        //Picasso.get().load(selectedImage).into(holder.imageview);

    }


    @Override
    public int getItemCount() {
        return imagesUriArrayList.size();
    }
}