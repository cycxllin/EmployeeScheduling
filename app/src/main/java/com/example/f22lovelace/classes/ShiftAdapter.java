package com.example.f22lovelace.classes;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftViewHolder> {
    Context context;
    ArrayList<Employee> emps;
    public EmployeeAdapter.EmployeeClickListener employeeClickListener;

    public ShiftAdapter(Context context, ArrayList<Employee> emps,
                        EmployeeAdapter.EmployeeClickListener employeeClickListener){
        this.context = context;
        this.emps = emps;
        this.employeeClickListener = employeeClickListener;
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_todays_shift, parent, false);
        return new ShiftViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        Employee employee = emps.get(position);

        String empID = String.valueOf(employee.getID());

        holder.employeeId.setText(empID);
        holder.first.setText(employee.getFirstName());
        holder.middle.setText(employee.getMiddleName());
        holder.last.setText(employee.getLastName());
        holder.phone.setText(employee.getPhone());

        //show open and close images if trained
        if (employee.getOpen() == 1)
            holder.open.setVisibility(View.VISIBLE);
        if (employee.getClose() == 1)
            holder.close.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employeeClickListener.selectedEmployee(employee);
            }
        });

    }

    @Override
    public int getItemCount() {return emps.size();}
}
