package com.example.f22lovelace.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

public class EmployeeViewHolder extends RecyclerView.ViewHolder {
    TextView employeeId, first, middle, last, email, phone;
    ImageView open, close;


    public EmployeeViewHolder(@NonNull View itemView) {
        super(itemView);

        employeeId = itemView.findViewById(R.id.employeeId);
        first = itemView.findViewById(R.id.fName);
        middle = itemView.findViewById(R.id.mName);
        last = itemView.findViewById(R.id.lName);
        email = itemView.findViewById(R.id.email);
        phone = itemView.findViewById(R.id.phone);
        open = itemView.findViewById(R.id.open);
        close = itemView.findViewById(R.id.close);
    }
}
