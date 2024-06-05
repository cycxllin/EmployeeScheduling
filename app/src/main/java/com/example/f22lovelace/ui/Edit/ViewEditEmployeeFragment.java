package com.example.f22lovelace.ui.Edit;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.EmployeeAdapter;
import com.example.f22lovelace.classes.Schedule;
import com.example.f22lovelace.databinding.FragmentVieweditemployeeBinding;
import com.example.f22lovelace.ui.defavail.DefAvailFragment;
import com.example.f22lovelace.ui.employees.EmployeesFragment;
import com.example.f22lovelace.ui.editavailability.EditAvailabilityFragment;
import com.example.f22lovelace.ui.home.HomeFragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewEditEmployeeFragment extends Fragment implements Backpressedlistener {

    private ViewEditEmployeeViewModel mViewModel;
    private FragmentVieweditemployeeBinding binding;

    //used to decide where you r taken to whe  you click yes on the popup. 1 is employee list
    //2 is edit availability and 3 is default availability
    int dest = 0;
    int removeFlag = 0;

    public static Backpressedlistener backpressedlistener;
    public static ViewEditEmployeeFragment newInstance() {
        return new ViewEditEmployeeFragment();
    }

    TextView fReq, lReq, eReq, pReq, pfReq, eVal;
    EditText veFName, veMName, veLName, vePNum, veEmail;
    Switch veOpen, veClose;

    Employee employee, temp;
    EmployeeAdapter employeeAdapter;

    Button button, noCancel, yes;

    Button defAvail;

    LocalDate today;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_blank, container, false);

        today = LocalDate.now();

        ViewEditEmployeeViewModel viewEditEmployeeViewModel =
                new ViewModelProvider(this).get(ViewEditEmployeeViewModel.class);
        binding = FragmentVieweditemployeeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //dest = 0;
        DBHandler dbHandler = new DBHandler(getContext());

        //Initialize fields
        veFName = (EditText) root.findViewById(R.id.edtTxtFName);
        veMName = (EditText) root.findViewById(R.id.edtTxtMName);
        veLName = (EditText) root.findViewById(R.id.edtTxtLName);
        vePNum  = (EditText) root.findViewById(R.id.edtTxtPNum);
        veEmail = (EditText) root.findViewById(R.id.edtTxtEmail);
        veOpen  = (Switch) root.findViewById(R.id.trainOCheck);
        veClose = (Switch) root.findViewById(R.id.trainCCheck);

        Bundle bundle = this.getArguments();

        if(bundle != null){

            today = (LocalDate) bundle.getSerializable("date");

            employee = (Employee) bundle.getSerializable("data");
            temp = new Employee(0,"","","","","", 0,
                    0, 0);
            veFName.setText(employee.getFirstName());
            veMName.setText(employee.getMiddleName());
            veLName.setText(employee.getLastName());
            vePNum.setText(employee.getPhone());
            veEmail.setText(employee.getEmail());
            if (employee.getOpen() == 1){
                veOpen.setChecked(true);
            }
            if (employee.getClose() == 1){
                veClose.setChecked(true);
            }
            //Set temp
            temp.setFirstName(employee.getFirstName());
            temp.setMiddleName(employee.getMiddleName());
            temp.setLastName(employee.getLastName());
            temp.setPhone(employee.getPhone());
            temp.setEmail(employee.getEmail());
            temp.setOpen(employee.getOpen());
            temp.setClose(employee.getClose());
        }
        fReq = (TextView) root.findViewById(R.id.txtVEFNameWarn);
        lReq = (TextView) root.findViewById(R.id.txtVELNameWarn);
        eReq = (TextView) root.findViewById(R.id.txtVEEmailWarn);
        eVal = (TextView) root.findViewById(R.id.txtVEEmailVal);
        pReq = (TextView) root.findViewById(R.id.txtVEPNumWarn);
        pfReq = (TextView) root.findViewById(R.id.txtVEPNumWarnFormat);
        textListeners();

        defAvail = (Button)root.findViewById(R.id.btnDefAvail);
        defAvail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (!checkDiff()){
                    dest = 3;
                    openPopup();
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", employee);

                    DefAvailFragment defavailFragment = new DefAvailFragment();
                    defavailFragment.setArguments(bundle);
                    getParentFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerMain, defavailFragment, null)
                            .commitNow();
                }
            }
        });



        //Reference and initialize button
        button = (Button) root.findViewById(R.id.eAction);
        //Set onClick behavior to button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialize popup menu
                PopupMenu popupMenu = new PopupMenu(getContext(), button);
                //Inflate popup menu from xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem){
                        //Toast message (Can be taken out)
                        //Toast.makeText(getContext(), "Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        //Switch between chosen user action
                        switch(menuItem.getItemId()){
                            case R.id.Apply:
                                checkfields();
                                if (fReq.getVisibility() != View.VISIBLE &
                                        lReq.getVisibility() != View.VISIBLE &
                                        eReq.getVisibility() != View.VISIBLE &
                                        eVal.getVisibility() != View.VISIBLE &
                                        pReq.getVisibility() != View.VISIBLE &
                                        pfReq.getVisibility() != View.VISIBLE) {
                                    employee.setFirstName(veFName.getText().toString());
                                    employee.setMiddleName(veMName.getText().toString());
                                    employee.setLastName(veLName.getText().toString());
                                    employee.setEmail(veEmail.getText().toString());
                                    employee.setPhone(vePNum.getText().toString());
                                    //set open based on switch button
                                    if (veOpen.isChecked()) {
                                        employee.setOpen(1);
                                    } else employee.setOpen(0);
                                    //set closed based on switch button
                                    if (veClose.isChecked()) {
                                        employee.setClose(1);
                                    } else employee.setClose(0);

                                    //Apply changes to database
                                    dbHandler.editEmp(employee);
                                    Toast.makeText(getContext(), "Saved changes to: "+employee.getFirstName(), Toast.LENGTH_SHORT).show();
                                    //Send back that an employee's data has been changed
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("data", employee);

                                    EmployeesFragment employeesFragment = new EmployeesFragment();
                                    employeesFragment.setArguments(bundle);

                                    getParentFragmentManager()
                                            .beginTransaction()
                                            .setReorderingAllowed(true)
                                            .replace(R.id.fragmentContainerMain, employeesFragment, null)
                                            .commitNow();
                                }
                                return true;
                            case R.id.Avail: {
                                if (!checkDiff()) {
                                    dest = 2;
                                    openPopup();

                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("data", employee);

                                    EditAvailabilityFragment editAvailabilityFragment = new EditAvailabilityFragment();
                                    editAvailabilityFragment.setArguments(bundle);

                                    getParentFragmentManager()
                                            .beginTransaction()
                                            .setReorderingAllowed(true)
                                            .replace(R.id.fragmentContainerMain, editAvailabilityFragment, null)
                                            .commitNow();
                                }
                            }
                            return true;
                            case R.id.Remove:
                                //Remove employee and reflect on database
                                removeEmpPop();
                        }

                        return true;
                    }
                });
                //show popup menu
                popupMenu.show();
            }

        });
        return root;
    }

    public void checkfields(){
        //update employee object based on edited fields
        //Required fields
        if (TextUtils.isEmpty(veFName.getText())) {
            fReq.setVisibility(View.VISIBLE);
        } else{
            fReq.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(veLName.getText())) {
            lReq.setVisibility(View.VISIBLE);
        } else{
            lReq.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(veEmail.getText())) {
            eReq.setVisibility(View.VISIBLE);
        }else{
            eReq.setVisibility(View.INVISIBLE);
            if (!checkEmail(veEmail.getText().toString())){
                eReq.setVisibility(View.INVISIBLE);
                eVal.setVisibility(View.VISIBLE);
            }else{
                eVal.setVisibility(View.INVISIBLE);
            }
        }
        if (TextUtils.isEmpty(vePNum.getText())) {
            pReq.setVisibility(View.VISIBLE);
        }else{
            pReq.setVisibility(View.INVISIBLE);
            PhoneNumberUtils.normalizeNumber(vePNum.getText().toString());
            if (PhoneNumberUtils.normalizeNumber(vePNum.getText().toString()).length() != 10){
                pfReq.setVisibility(View.VISIBLE);
            }
            else {
                pfReq.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Checks for unsaved changes, opens popup if any
     */
    public boolean checkDiff(){
        return employee.equals(temp);
    }

    /**
     * Opens save changes popup
     * @return
     */
    public boolean openPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.edit_emp_popup, null, false);
        PopupWindow popupWindow = new PopupWindow(popup, 525, 200, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        noCancel = (Button) popup.findViewById(R.id.noCancelbtn);
        noCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                //Reset employee info
                employee.setFirstName(temp.getFirstName());
                employee.setMiddleName(temp.getMiddleName());
                employee.setLastName(temp.getLastName());
                employee.setPhone(temp.getPhone());
                employee.setEmail(temp.getEmail());
                employee.setOpen(temp.getOpen());
                employee.setClose(temp.getClose());
                //Reset editTexts
                veFName.setText(employee.getFirstName());
                veMName.setText(employee.getMiddleName());
                veLName.setText(employee.getLastName());
                vePNum.setText(employee.getPhone());
                veEmail.setText(employee.getEmail());
                if (employee.getOpen() == 1){
                    veOpen.setChecked(true);
                }
                if (employee.getClose() == 1){
                    veClose.setChecked(true);
                }
            }
        });

        yes = (Button) popup.findViewById(R.id.yesbtn);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO copy save button code
                checkfields();
                if (fReq.getVisibility() != View.VISIBLE &
                        lReq.getVisibility() != View.VISIBLE &
                        eReq.getVisibility() != View.VISIBLE &
                        pReq.getVisibility() != View.VISIBLE &
                        pfReq.getVisibility() != View.VISIBLE) {
                    employee.setFirstName(veFName.getText().toString());
                    employee.setMiddleName(veMName.getText().toString());
                    employee.setLastName(veLName.getText().toString());
                    employee.setEmail(veEmail.getText().toString());
                    employee.setPhone(vePNum.getText().toString());
                    //set open based on switch button
                    if (veOpen.isChecked()) {
                        employee.setOpen(1);
                    } else employee.setOpen(0);
                    //set closed based on switch button
                    if (veClose.isChecked()) {
                        employee.setClose(1);
                    } else employee.setClose(0);
                    DBHandler db = new DBHandler(getContext());
                    //Apply changes to database
                    db.editEmp(employee);
                    Toast.makeText(getContext(), "Saved changes to: " + employee.getFirstName(), Toast.LENGTH_SHORT).show();
                    if (dest == 1){
                        EmployeesFragment employeesFragment = new EmployeesFragment();
                        getParentFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerMain, employeesFragment, null)
                                .commitNow();
                    }
                    else if (dest == 2){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", employee);

                        EditAvailabilityFragment editAvailabilityFragment = new EditAvailabilityFragment();
                        editAvailabilityFragment.setArguments(bundle);

                        getParentFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerMain, editAvailabilityFragment, null)
                                .commitNow();
                    }
                    else if (dest == 3){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", employee);

                        DefAvailFragment defavailFragment = new DefAvailFragment();
                        defavailFragment.setArguments(bundle);
                        getParentFragmentManager()
                                .beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragmentContainerMain, defavailFragment, null)
                                .commitNow();
                    }
                }
                popupWindow.dismiss();
            }
        });
        return false;
    }

    public void textListeners(){
        //Listeners if any fields were changed
        veFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Why is it changing temp at the same time as employee?
                employee.setFirstName(veFName.getText().toString());
                System.out.println("Changed employee: " + employee.getFirstName());
                System.out.println("Changed temp? " + temp.getFirstName());
            }
        });

        veMName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                employee.setMiddleName(veMName.getText().toString());
            }
        });

        veLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                employee.setLastName(veLName.getText().toString());
            }
        });

        veEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                employee.setEmail(veEmail.getText().toString());
            }
        });

        vePNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                employee.setPhone(vePNum.getText().toString());
            }
        });

        veOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (veOpen.isChecked()){
                    employee.setOpen(1);
                }else {
                    employee.setOpen(0);
                }
            }
        });

        veClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (veClose.isChecked()){
                    employee.setClose(1);
                }else {
                    employee.setClose(0);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewEditEmployeeViewModel.class);
        // TODO: Use the ViewModel
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
        if (!checkDiff()){
            dest = 1;
            openPopup();
        }else {
            //System.out.println(getView(R.id.fragmentContainer));
            if (this.getTag() == "Home"){
                this.onDestroy();
                this.onDestroyView();
                HomeFragment homefragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("today", today);
                homefragment.setArguments(bundle);
                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .detach(this)
                        .replace(R.id.fragmentContainerMain, homefragment, null)
                        .commitNow();
            } else{
            EmployeesFragment employeesFragment = new EmployeesFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerMain, employeesFragment, null)
                    .commitNow();
            }
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /** Remove emp popup
     */
    public boolean removeEmpPop() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.remove_emp_popup, null, false);
        PopupWindow popupWindow = new PopupWindow(popup, 600, 300, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0 ,0);

        noCancel = (Button) popup.findViewById(R.id.noButton);
        noCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        yes = (Button) popup.findViewById(R.id.yesButton);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHandler db = new DBHandler(getContext());
                    ArrayList<Schedule> onScheds = new ArrayList<>(db.empsSchedsFromDay(
                            LocalDate.now(), employee));
                    Schedule temp = new Schedule(0,0,0);

                    if (!onScheds.isEmpty()){
                        //find shifts with the id and change to -1, update db
                        int i;

                        for (i = 0; i<onScheds.size(); i++){
                            temp = onScheds.get(i);
                            temp.removeEmp(employee.getID());

                            db.addEditSched(temp);
                        }
                    }

                    Toast.makeText(getContext(), "Deleted "+employee.getFirstName(), Toast.LENGTH_SHORT).show();
                    db.removeEmp(employee.getID());

                    //Send back that an employee was removed
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", employee);

                    EmployeesFragment employeesFragment = new EmployeesFragment();
                    employeesFragment.setArguments(bundle);

                    getParentFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerMain, employeesFragment, null)
                            .commitNow();

                popupWindow.dismiss();
            }
        });
        return false;
    }

    public boolean checkEmail(String emailStr){ //checks the email for the proper format

        Pattern RFC = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        Matcher mm = RFC.matcher(emailStr);

        return mm.matches();
    }

}