package com.example.firebasedemo;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter {
    List<Contact> contacts;
    Context ctx;
    LayoutInflater inflater;

    ContactAdapter(Context ctx, List<Contact> contacts) {
        this.ctx = ctx;
        this.contacts = contacts;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_contact, parent, false);
        return new ContactVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Contact c = contacts.get(position);

        ContactVH vh = (ContactVH) holder;
        vh.nameTextView.setText(c.name);
        vh.emailTextView.setText(c.email);
        vh.phoneTextView.setText(c.phone);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView phoneTextView;

        ContactVH(View iView) {
            super(iView);
            nameTextView = (TextView) iView.findViewById(R.id.item_name);
            emailTextView = (TextView) iView.findViewById(R.id.item_email);
            phoneTextView = (TextView) iView.findViewById(R.id.item_phone);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = this.getAdapterPosition();
            Toast.makeText(ctx, contacts.get(pos).name, Toast.LENGTH_SHORT).show();
        }
    }

}