package com.example.f22lovelace.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

public class ShiftViewHolder extends RecyclerView.ViewHolder {
    TextView employeeId, first, middle, last, phone;
    ImageView open, close;

    public ShiftViewHolder(@NonNull View itemView){
        super(itemView);

        employeeId = itemView.findViewById(R.id.empID);
        first = itemView.findViewById(R.id.eFName);
        middle = itemView.findViewById(R.id.eMName);
        last = itemView.findViewById(R.id.eLName);
        phone = itemView.findViewById(R.id.ePhone);
        open = itemView.findViewById(R.id.sOpen);
        close = itemView.findViewById(R.id.sClose);
    }
}
