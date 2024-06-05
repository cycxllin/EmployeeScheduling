package com.example.f22lovelace.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class UnschedEmpAdapter extends RecyclerView.Adapter<UnschedEmpViewHolder>{
    Context context;
    ArrayList<Employee> emps;

    public UnschedEmpAdapter(Context context, ArrayList<Employee> emps){
        this.context = context;
        this.emps = emps;
    }

    @NonNull
    @Override
    public UnschedEmpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.simple_employee, parent, false);
        return new UnschedEmpViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UnschedEmpViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {return emps.size();}
}
