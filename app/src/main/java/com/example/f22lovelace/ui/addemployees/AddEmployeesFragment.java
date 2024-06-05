package com.example.f22lovelace.ui.addemployees;

import static androidx.core.content.ContextCompat.getSystemService;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.SimilarEmployeeAdapter;
import com.example.f22lovelace.databinding.FragmentAddemployeesBinding;
import com.example.f22lovelace.ui.Edit.ViewEditEmployeeFragment;
import com.example.f22lovelace.ui.employees.EmployeesFragment;

import java.lang.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;

public class AddEmployeesFragment extends Fragment implements Backpressedlistener, SimilarEmployeeAdapter.dupEmpCLickListener{
    private FragmentAddemployeesBinding binding;
    public static Backpressedlistener backpressedlistener;

    Button noCancel, yes;
    int check = 0;

    SimilarEmployeeAdapter simEmpAd;
    Employee selectedDupEmp;
    PopupWindow popupWindow;

    View popup;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddEmployeesViewModel AddEmployeesViewModel =
                new ViewModelProvider(this).get(AddEmployeesViewModel.class);

        binding = FragmentAddemployeesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText fName, mName, lName, email, phone;
        TextView fReq, lReq, eReq, pReq, pfReq, eVal;
        Switch open, close;
        Button addEmployee;
        Button defAvail;
        DBHandler dbHandler = new DBHandler(this.getContext());
        fName = (EditText)root.findViewById(R.id.edtATxtFName);
        mName = (EditText)root.findViewById(R.id.edtATxtMName);
        lName = (EditText)root.findViewById(R.id.edtATxtLName);
        email = (EditText)root.findViewById(R.id.edtATxtEmail);
        phone = (EditText)root.findViewById(R.id.edtATxtPNum);
        open = (Switch)root.findViewById(R.id.trainAOCheck);
        close = (Switch)root.findViewById(R.id.trainACCheck);
        fReq = (TextView)root.findViewById(R.id.txtAWarnFName);
        lReq = (TextView)root.findViewById(R.id.txtAWarnLName);
        eReq = (TextView)root.findViewById(R.id.txtAWarnEmail);
        eVal = (TextView)root.findViewById(R.id.txtAWarnEmailVal);
        pReq = (TextView)root.findViewById(R.id.txtAWarnPNum);
        pfReq = (TextView)root.findViewById(R.id.txtAWarnPNumFormat);

        addEmployee = (Button)root.findViewById(R.id.btnApplyChange);
        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Required fields
                if (TextUtils.isEmpty(fName.getText())) {
                    fReq.setVisibility(View.VISIBLE);
                }else{
                    fReq.setVisibility(View.INVISIBLE);
                }
                if (TextUtils.isEmpty(lName.getText())) {
                    lReq.setVisibility(View.VISIBLE);
                }else {
                    lReq.setVisibility(View.INVISIBLE);
                }
                if (TextUtils.isEmpty(email.getText())) {
                    eReq.setVisibility(View.VISIBLE);

                }else {
                    eReq.setVisibility(View.INVISIBLE);
                    checkEmail(email.getText().toString());
                    if (checkEmail(email.getText().toString()) == false){
                        eReq.setVisibility(View.INVISIBLE);
                        eVal.setVisibility(View.VISIBLE);
                    }else{
                        eVal.setVisibility(View.INVISIBLE);
                    }
                }
                if (TextUtils.isEmpty(phone.getText())) {
                    pReq.setVisibility(View.VISIBLE);
                }else {
                    pReq.setVisibility(View.INVISIBLE);
                    PhoneNumberUtils.normalizeNumber(phone.getText().toString());
                    if (PhoneNumberUtils.normalizeNumber(phone.getText().toString()).length() != 10){
                        pfReq.setVisibility(View.VISIBLE);
                    }
                    else{
                        pfReq.setVisibility(View.INVISIBLE);
                    }
                }
                if (fReq.getVisibility() != View.VISIBLE &
                        lReq.getVisibility() != View.VISIBLE &
                        eReq.getVisibility() != View.VISIBLE &
                        eVal.getVisibility() != View.VISIBLE &
                        pReq.getVisibility() != View.VISIBLE &
                        pfReq.getVisibility() != View.VISIBLE) {
                    Employee employee = new Employee(1,fName.getText().toString(),mName.getText().toString(),
                            lName.getText().toString(), email.getText().toString(),
                            PhoneNumberUtils.formatNumber(phone.getText().toString()),
                            0,0, 1);
                    if (open.isChecked()){
                        employee.setOpen(1);
                    }
                    if (close.isChecked()){
                        employee.setClose(1);
                    }

                    //check if there are similar employees in the db
                    ArrayList<Employee> similarEmps = dbHandler.checkDupEmp(employee);
                    if (!similarEmps.isEmpty()) {
                        System.out.println("Similar emps:" + similarEmps);
                        openPopup(similarEmps, employee, dbHandler);
                    }
                    else { //no similar emps in db so add emp
                        dbHandler.addEmp(employee);
                        Toast.makeText(getContext(), "Added " + employee.getFirstName(), Toast.LENGTH_SHORT).show();
                        EmployeesFragment employeesFragment = new EmployeesFragment();
                        getParentFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerMain, employeesFragment, null)
                                .commitNow();
                    }
                }

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState){

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        EmployeesFragment employeesFragment = new EmployeesFragment();
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerMain, employeesFragment, null)
                .commitNow();
    }

    public void openPopup(ArrayList<Employee> similarEmps, Employee employee, DBHandler dbHandler) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        popup = layoutInflater.inflate(R.layout.dup_emp_popup, null, false);
        popupWindow = new PopupWindow(popup, ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0 ,0);

        RecyclerView recyclerView = popup.findViewById(R.id.dupEmpsRV);
        recyclerView.setHasFixedSize(true);
        simEmpAd = new SimilarEmployeeAdapter(this.getActivity(), similarEmps, this);
        recyclerView.setAdapter(simEmpAd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noCancel = (Button) popup.findViewById(R.id.okBtn);
        noCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHandler.addEmp(employee);
                Toast.makeText(getContext(), "Added " + employee.getFirstName(), Toast.LENGTH_SHORT).show();
                EmployeesFragment employeesFragment = new EmployeesFragment();
                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerMain, employeesFragment, null)
                        .commitNow();
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void selectedEmployee(Employee employee) {
        DBHandler dbHandler = new DBHandler(this.getContext());
        ArrayList<Employee> similarEmps = dbHandler.checkDupEmp(employee);

        RecyclerView recyclerView = popup.findViewById(R.id.unschedEmpsRV);
        recyclerView.setHasFixedSize(true);
        simEmpAd = new SimilarEmployeeAdapter(this.getActivity(), similarEmps, this);
        recyclerView.setAdapter(simEmpAd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        employee.setActive(1); //set inactive employee to active
        dbHandler.editEmp(employee);

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", employee);

        ViewEditEmployeeFragment blank = new ViewEditEmployeeFragment();
        blank.setArguments(bundle);

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainerMain, blank)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        recyclerView.setVisibility(getView().GONE);
        popupWindow.dismiss();

        ft.commitNow();

    }

    public boolean checkEmail(String emailStr){ //checks the email for the proper format

        Pattern RFC = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        Matcher mm = RFC.matcher(emailStr);

        return mm.matches();
    }

}
