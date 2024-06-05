package com.example.f22lovelace.ui.defavail;

import static androidx.core.content.ContextCompat.getSystemService;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Avail;
import com.example.f22lovelace.classes.AvailByStatus;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.Schedule;
import com.example.f22lovelace.databinding.FragmentDefaultAvailabilityBinding;
import com.example.f22lovelace.ui.Edit.ViewEditEmployeeFragment;
import com.example.f22lovelace.ui.defavail.DefAvailViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DefAvailFragment extends Fragment implements Backpressedlistener {
    private FragmentDefaultAvailabilityBinding binding;
    public static Backpressedlistener backpressedlistener;

    Employee employee;

    Button apply, select, deselect, dismiss;
    ImageButton info;

    int id;

    CheckBox sun, monOpen, monClose, tuesOpen, tuesClose, wedOpen, wedClose,
            thursOpen, thursClose, friOpen, friClose, sat;

    TextView empName;

    //a list to store all the checkboxes
    List<CheckBox> boxes = new ArrayList();

    ArrayList<Integer> defAvail = new ArrayList<>();
    ArrayList<Integer> editedDefAvail = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DefAvailViewModel DefAvailViewModel =
                new ViewModelProvider(this).get(DefAvailViewModel.class);

        binding = FragmentDefaultAvailabilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle bundle = this.getArguments();

        //initialising and setting name textview
        empName = (TextView) root.findViewById(R.id.empName);

        //
        info = (ImageButton) root.findViewById(R.id.definfo);

        //Initialising checkboxes then adding them to the list
        sun = (CheckBox) root.findViewById(R.id.SunFullDay);
        boxes.add(sun);
        sat = (CheckBox) root.findViewById(R.id.SatFullDay);
        boxes.add(sat);
        monOpen = (CheckBox) root.findViewById(R.id.MonOpen);
        boxes.add(monOpen);
        monClose = (CheckBox) root.findViewById(R.id.MonClose);
        boxes.add(monClose);
        tuesOpen = (CheckBox) root.findViewById(R.id.TuesOpen);
        boxes.add(tuesOpen);
        tuesClose = (CheckBox) root.findViewById(R.id.TuesClose);
        boxes.add(tuesClose);
        wedOpen = (CheckBox) root.findViewById(R.id.WedOpen);
        boxes.add(wedOpen);
        wedClose = (CheckBox) root.findViewById(R.id.WedClose);
        boxes.add(wedClose);
        thursOpen = (CheckBox) root.findViewById(R.id.ThursOpen);
        boxes.add(thursOpen);
        thursClose = (CheckBox) root.findViewById(R.id.ThursClose);
        boxes.add(thursClose);
        friOpen = (CheckBox) root.findViewById(R.id.FriOpen);
        boxes.add(friOpen);
        friClose = (CheckBox) root.findViewById(R.id.FriClose);
        boxes.add(friClose);

        if (bundle != null) {
            employee = (Employee) bundle.getSerializable("data");
            id = employee.getID();
            empName.setText(employee.getFullName());
        }
        DBHandler dbHandler = new DBHandler(getContext());
        defAvail = dbHandler.defAvailStatusList(id);
        //if (dbHandler.defAvailStatusList(id) == null){System.out.println("SLAY");}

        //Filling out the checkboxes based off of the saved default availability
        if (defAvail.get(0) == 1){sun.setChecked(true);}
        if (defAvail.get(6) == 1){sat.setChecked(true);}
        if (defAvail.get(1) == 1){monOpen.setChecked(true);monClose.setChecked(true);}
        if (defAvail.get(1) == 2){monOpen.setChecked(true);}
        if (defAvail.get(1) == 3){monClose.setChecked(true);}
        if (defAvail.get(2) == 1){tuesOpen.setChecked(true);tuesClose.setChecked(true);}
        if (defAvail.get(2) == 2){tuesOpen.setChecked(true);}
        if (defAvail.get(2) == 3){tuesClose.setChecked(true);}
        if (defAvail.get(3) == 1){wedOpen.setChecked(true);wedClose.setChecked(true);}
        if (defAvail.get(3) == 2){wedOpen.setChecked(true);}
        if (defAvail.get(3) == 3){wedClose.setChecked(true);}
        if (defAvail.get(4) == 1){thursOpen.setChecked(true);thursClose.setChecked(true);}
        if (defAvail.get(4) == 2){thursOpen.setChecked(true);}
        if (defAvail.get(4) == 3){thursClose.setChecked(true);}
        if (defAvail.get(5) == 1){friOpen.setChecked(true);friClose.setChecked(true);}
        if (defAvail.get(5) == 2){friOpen.setChecked(true);}
        if (defAvail.get(5) == 3){friClose.setChecked(true);}

        apply = (Button) root.findViewById(R.id.applyAvail);
        select = (Button) root.findViewById(R.id.selectall);
        deselect = (Button) root.findViewById(R.id.deselectall);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i<12; i++){
                    boxes.get(i).setChecked(true);
                }
            }
        });

        deselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i<12; i++){
                    boxes.get(i).setChecked(false);
                }
            }
        });

        //info button
        ImageButton info = root.findViewById(R.id.definfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoPopup();
            }
        });

        apply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Edits default availability based off of the selected checkboxes
                if (sun.isChecked() == false){editedDefAvail.add(0);}
                else {editedDefAvail.add(1);}

                if (monOpen.isChecked() == true && monClose.isChecked() == true){
                    editedDefAvail.add(1);}
                else if (monOpen.isChecked() == true && monClose.isChecked() == false) {
                    editedDefAvail.add(2);}
                else if (monOpen.isChecked() == false && monClose.isChecked() == true) {
                    editedDefAvail.add(3);}
                else {editedDefAvail.add(0);}

                if (tuesOpen.isChecked() == true && tuesClose.isChecked() == true){
                    editedDefAvail.add(1);}
                else if (tuesOpen.isChecked() == true && tuesClose.isChecked() == false) {
                    editedDefAvail.add(2);}
                else if (tuesOpen.isChecked() == false && tuesClose.isChecked() == true) {
                    editedDefAvail.add(3);}
                else {editedDefAvail.add(0);}

                if (wedOpen.isChecked() == true && wedClose.isChecked() == true){
                    editedDefAvail.add(1);}
                else if (wedOpen.isChecked() == true && wedClose.isChecked() == false) {
                    editedDefAvail.add(2);}
                else if (wedOpen.isChecked() == false && wedClose.isChecked() == true) {
                    editedDefAvail.add(3);}
                else {editedDefAvail.add(0);}

                if (thursOpen.isChecked() == true && thursClose.isChecked() == true){
                    editedDefAvail.add(1);}
                else if (thursOpen.isChecked() == true && thursClose.isChecked() == false) {
                    editedDefAvail.add(2);}
                else if (thursOpen.isChecked() == false && thursClose.isChecked() == true) {
                    editedDefAvail.add(3);}
                else {editedDefAvail.add(0);}

                if (friOpen.isChecked() == true && friClose.isChecked() == true){
                    editedDefAvail.add(1);}
                else if (friOpen.isChecked() == true && friClose.isChecked() == false) {
                    editedDefAvail.add(2);}
                else if (friOpen.isChecked() == false && friClose.isChecked() == true) {
                    editedDefAvail.add(3);}
                else {editedDefAvail.add(0);}

                if (sat.isChecked() == false){editedDefAvail.add(0);}
                else {editedDefAvail.add(1);}

                dbHandler.editDefAvail(id, editedDefAvail);

                //def avail was changed so must check scheds from today
                LocalDate today = LocalDate.now();

                //get date range to check avail for: between now and the end of schedlued shifts.
                LocalDate endDay = dbHandler.getScheduleEndDate(today);

                if (endDay != null && endDay.isAfter(today)){
                    //possible that some scheds may be affected
                    ArrayList <Schedule> empSched = dbHandler.empsSchedsFromDay(today, employee);

                    if (!empSched.isEmpty()){
                        //employee is scheduled on some days
                        ArrayList<Integer> defAvailList = dbHandler.defAvailStatusList(employee.getID());
                        Avail tempAvail;
                        Schedule tSched;

                        //for each day on empSched check if avail changes, then def if not
                        int pig; // pig is just a name for the interative variable
                        for (pig = 0; pig<empSched.size(); pig++){
                            tSched = empSched.get(pig);
                            LocalDate tempDate = LocalDate.of((tSched.getYear()),
                                    tSched.getMonth(), tSched.getDay());
                            int status;

                            tempAvail = dbHandler.getAvailbyDayByEmp(employee, tempDate);
                            if (tempAvail == null) {
                                //do what def avail says
                                status = dbHandler.statusFromDayOfWeek(tempDate, defAvailList);
                            } else {
                                status = tempAvail.getStatus();
                            }

                            switch (status) {
                                case 0: //emp was scheduled but now not avail at all
                                    tSched.removeEmp(employee.getID());
                                    //System.out.println("Now not avail");
                                    tSched.setStatus(2);
                                    break;
                                case 2: //emp was scheduled but now only avail morning
                                    //System.out.println("Only Avail Morning");
                                    if (tSched.getEve1() == employee.getID()) {
                                        tSched.setEve1(-1);
                                        tSched.setStatus(2);
                                        //System.out.println("Was Sched on eve1");
                                    }
                                    if (tSched.getEve2() == employee.getID()) {
                                        tSched.setEve2(-1);
                                        tSched.setStatus(2);
                                        //System.out.println("Was Sched on eve2");
                                    }
                                    if (tSched.getEveBusy() == employee.getID()) {
                                        tSched.setEveBusy(-1);
                                        tSched.setStatus(2);
                                        //System.out.println("Was Sched on eve3");
                                    }
                                    break;
                                case 3: //emp was scheduled but now only avail evening
                                    //System.out.println("Only Avail Evening");
                                    if (tSched.getShift1() == employee.getID()) {
                                        tSched.setShift1(-1);
                                        tSched.setStatus(2);
                                        //System.out.println("Was Sched on Morn1");
                                    }
                                    if (tSched.getShift2() == employee.getID()) {
                                        tSched.setShift2(-1);
                                        tSched.setStatus(2);
                                        //System.out.println("Was Sched on Morn2");
                                    }
                                    if (tSched.getMornBusy() == employee.getID()) {
                                        tSched.setMornBusy(-1);
                                        tSched.setStatus(2);
                                        //System.out.println("Was Sched on Morn3");
                                    }
                                    break;
                                default: //avail all day so no need to change shifts
                                    break;
                            }
                            dbHandler.addEditSched(tSched);
                        }
                    }
                }

                Toast.makeText(getContext(), "Default Availabity Saved", Toast.LENGTH_SHORT).show();
                ViewEditEmployeeFragment blank = new ViewEditEmployeeFragment();
                blank.setArguments(bundle);

                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerMain, blank, null)
                        .commitNow();
            }
        });
        return root;
    }

    private void infoPopup() {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.info_popup, null, false);
        PopupWindow infoPopup = new PopupWindow(popup, 500, 375, true);
        infoPopup.setSplitTouchEnabled(false);
        infoPopup.showAtLocation(getView(), Gravity.CENTER, 0 ,0);

        dismiss = (Button) popup.findViewById(R.id.dismissBtn);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoPopup.dismiss();
            }
        });
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", employee);

        ViewEditEmployeeFragment blank = new ViewEditEmployeeFragment();
        blank.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerMain, blank, null)
                .commitNow();
    }
}
