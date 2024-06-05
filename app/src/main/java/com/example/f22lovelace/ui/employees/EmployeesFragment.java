package com.example.f22lovelace.ui.employees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.EmployeeAdapter;
import com.example.f22lovelace.ui.Edit.ViewEditEmployeeFragment;
import com.example.f22lovelace.ui.addemployees.AddEmployeesFragment;
import com.example.f22lovelace.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EmployeesFragment extends Fragment implements EmployeeAdapter.EmployeeClickListener, Backpressedlistener {

    EmployeeAdapter employeeAdapter;
    public static Backpressedlistener backpressedlistener;
    EmployeeAdapter.EmployeeClickListener empClick;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        //create handler, get all employees from the database into a list
        DBHandler dbHandler = new DBHandler(getContext());
        ArrayList<Employee> allEmps = dbHandler.listActiveEmps();

        //Sort employees by first name
        Collections.sort(allEmps, new Comparator<Employee>() {
            @Override
            public int compare(Employee employee, Employee t1) {
                return employee.getFirstName().compareToIgnoreCase(t1.getFirstName());
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }
        });

/*
        // info to bypass getting info from handler; used to test UI recyclerview
        //create emps and list to display
        Employee emp = new Employee(1,"Jordan", "Bothwell", "Peterson",
                "jbpeterson@fake.com", "1234567890", 0, 1);
        Employee emp2 = new Employee(2, "Jamil", null, "Meetwood",
                "jmeetwood@fake.com", "2345678901", 1, 0);

        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2); */

        RecyclerView recyclerView = view.findViewById(R.id.employeeRV);
        recyclerView.setHasFixedSize(true);
        employeeAdapter = new EmployeeAdapter(this.getActivity(), allEmps, this);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.fragmentContainerMain, new AddEmployeesFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fab.hide(); /*hides the action button otherwise it shows on the next screen*/
                recyclerView.setVisibility(v.GONE);
                fr.commit();
            }
        });

        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void selectedEmployee(Employee employee) {
        //Message (optional to keep)
        Toast.makeText(this.getContext(), "Selected User "+employee.getFirstName(), Toast.LENGTH_SHORT).show();
        //Pass employee info to ViewEditEmployee fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", employee);

        ViewEditEmployeeFragment blank = new ViewEditEmployeeFragment();
        blank.setArguments(bundle);

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainerMain, blank)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        FloatingActionButton fab = (FloatingActionButton)getView().findViewById(R.id.fab);
        fab.hide();

        //Keyanna's fix no idea why it works
        DBHandler dbHandler = new DBHandler(getContext());
        ArrayList<Employee> allEmps = dbHandler.listActiveEmps();

        RecyclerView recyclerView = getView().findViewById(R.id.employeeRV);
        recyclerView.setHasFixedSize(true);
        employeeAdapter = new EmployeeAdapter(this.getActivity(), allEmps, this);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));

        recyclerView.setVisibility(getView().GONE);

        ft.commitNow();

    }
    @Override
    public void onPause() {
        backpressedlistener=null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener=this;
    }

    @Override
    public void onBackPressed(){
        HomeFragment homefragment = new HomeFragment();
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerMain, homefragment, null)
                .commitNow();
    }
}
