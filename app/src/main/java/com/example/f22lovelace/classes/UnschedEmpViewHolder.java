package com.example.f22lovelace.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

public class UnschedEmpViewHolder extends RecyclerView.ViewHolder{
    TextView employeeId, first, middle, last, phone;
    ImageView open, close;

    public UnschedEmpViewHolder(@NonNull View itemView) {
        super(itemView);

        employeeId = itemView.findViewById(R.id.sEID);
        first = itemView.findViewById(R.id.sEFName);
        middle = itemView.findViewById(R.id.sEMName);
        last = itemView.findViewById(R.id.sELName);
        phone = itemView.findViewById(R.id.sEPhone);
        open = itemView.findViewById(R.id.sEOpen);
        close = itemView.findViewById(R.id.sEClose);
    }
}
