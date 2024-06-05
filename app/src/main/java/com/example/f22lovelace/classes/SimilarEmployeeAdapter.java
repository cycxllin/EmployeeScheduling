package com.example.f22lovelace.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class SimilarEmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> {
    Context context;
    ArrayList<Employee> allEmps;
    public dupEmpCLickListener dupClickListen;

    // constructor with Record's data model list and view context
    public SimilarEmployeeAdapter(Context context, ArrayList<Employee> allEmps,
                                  dupEmpCLickListener dupClickListen) {
        this.context = context;
        this.allEmps = allEmps;
        this.dupClickListen = dupClickListen;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_employees,parent,false);
        return  new EmployeeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = allEmps.get(position);

        String empID = String.valueOf(employee.getID());
        String op = "Opening";
        String cl = "Closing";

        holder.employeeId.setText(empID);
        holder.first.setText(employee.getFirstName());
        holder.middle.setText(employee.getMiddleName());
        holder.last.setText(employee.getLastName());
        holder.email.setText(employee.getEmail());
        holder.phone.setText(employee.getPhone());

        //show open and close images if trained
        if (employee.getOpen() == 1)
            holder.open.setVisibility(View.VISIBLE);
        if (employee.getClose() == 1)
            holder.close.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dupClickListen.selectedEmployee(employee);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allEmps.size();
    }

    /*
    public static class EmployeeAdapterVh extends RecyclerView.ViewHolder{
        private EditText FName, MName, LName, Email, PNum, op, cl;

        public EmployeeAdapterVh(@NonNull View itemView) {
            super(itemView);
            FName = itemView.findViewById(R.id.edtTxtFName);
            MName = itemView.findViewById(R.id.edtTxtMName);
            LName = itemView.findViewById(R.id.edtTxtLName);
            Email = itemView.findViewById(R.id.edtTxtEmail);
            PNum = itemView.findViewById(R.id.edtTxtPNum);
        }
    }*/

    public interface dupEmpCLickListener {
        void selectedEmployee(Employee employee);
    }
}
