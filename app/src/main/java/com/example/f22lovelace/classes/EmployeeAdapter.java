package com.example.f22lovelace.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.f22lovelace.R;
import com.example.f22lovelace.ui.employees.EmployeesFragment;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeViewHolder> {
    Context context;
    ArrayList<Employee> allEmps;
    public EmployeeClickListener employeeClickListener;

    // constructor with Record's data model list and view context
    public EmployeeAdapter(Context context, ArrayList<Employee> allEmps,
                           EmployeeClickListener employeeClickListener) {
        this.context = context;
        this.allEmps = allEmps;
        this.employeeClickListener = employeeClickListener;
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
                employeeClickListener.selectedEmployee(employee);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allEmps.size();
    }

    /*
    public static class EmployeeAdapterVh extends RecyclerView.ViewHolder{

        //TODO rest of employee info
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

    public interface EmployeeClickListener{
        void selectedEmployee(Employee employee);
    }
}
