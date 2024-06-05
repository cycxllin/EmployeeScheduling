package com.example.f22lovelace.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.EmployeeAdapter;
import com.example.f22lovelace.classes.Schedule;
import com.example.f22lovelace.classes.ShiftAdapter;
import com.example.f22lovelace.databinding.FragmentHomeBinding;
import com.example.f22lovelace.ui.Edit.ViewEditEmployeeFragment;
import com.example.f22lovelace.ui.editShift.editShiftFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment implements EmployeeAdapter.EmployeeClickListener {

    private FragmentHomeBinding binding;
    private TextView monthDayYear, dayOfWeek, noSched;
    public Button editToday;
    private LocalDate selectedDate;
    private RecyclerView mornShift, eveShift;
    private ImageView sun, moon, weekendMoon;
    LinearLayout top;

    Schedule today;

    ShiftAdapter mornShiftAdapter, eveShiftAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Initialize
        monthDayYear = root.findViewById(R.id.monthDayYear);
        dayOfWeek = root.findViewById(R.id.dayOfWeekTV);
        mornShift = root.findViewById(R.id.empMornShift);
        eveShift = root.findViewById(R.id.empEveShift);
        editToday = root.findViewById(R.id.editSched);
        noSched = root.findViewById(R.id.noSched);
        sun = root.findViewById(R.id.sun);
        moon = root.findViewById(R.id.moon);
        weekendMoon = root.findViewById(R.id.moonWeekend);

        ImageButton forward = root.findViewById(R.id.forwardDayBtn);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDayAction(view);
            }
        });

        ImageButton back = root.findViewById(R.id.backDayBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousDayAction(view);
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null){
            selectedDate = (LocalDate) bundle.getSerializable("today");
        }else {selectedDate = LocalDate.now();}
        setDayView();

        editToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Editing todays shift!", Toast.LENGTH_SHORT).show();
                //Open new fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("day", selectedDate);
                editShiftFragment editshiftfragment = new editShiftFragment();
                editshiftfragment.setArguments(bundle);

                //Set stuff from previous screen to gone
                setInvis();

                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerMain, editshiftfragment, "editShiftHome")
                        .commitNow();
            }
        });

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setDayView(){
        //Month year text
        monthDayYear.setText(monthYearFromDate(selectedDate));
        //Current day
        String dayOfWeektext = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeek.setText(dayOfWeektext);

        dbTodaySched(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear());

        if (today != null){
            if (selectedDate.getDayOfWeek().getValue() > 5){//Weekend
                weekendMoon.setVisibility(getView().VISIBLE);

            }else{//Weekday
                moon.setVisibility(getView().VISIBLE);
            }
            sun.setVisibility(getView().VISIBLE);


            //Morning shift card view
            ArrayList<Employee> empMornShiftList = new ArrayList<>();
            empMornShiftList = getEmpShift(today.getShift1(), today.getShift2(), today.getBusy());

            if (empMornShiftList.size() < 3 & empMornShiftList.size() > 0){
                //Display Unfinished Schedule text
                mornShift.setVisibility(getView().VISIBLE);
                mornShift.setHasFixedSize(true);
                mornShiftAdapter = new ShiftAdapter(this.getContext(), empMornShiftList, this);
                mornShift.setAdapter(mornShiftAdapter);
                mornShift.setLayoutManager(new LinearLayoutManager(this.getContext()));
            }else{
                //No employees scheduled
            }

            if (selectedDate.getDayOfWeek().getValue() <= 5) {//Weekday
                //Evening Shift cardview
                ArrayList<Employee> empEveShiftList = new ArrayList<>();
                empEveShiftList = getEmpShift(today.getEve1(), today.getEve2(), today.getEveBusy());
                //TODO
                if (empEveShiftList.size() < 3 & empEveShiftList.size() > 0) {
                    //Display unfinished schedule text
                    eveShift.setVisibility(getView().VISIBLE);
                    eveShift.setHasFixedSize(true);
                    eveShiftAdapter = new ShiftAdapter(this.getContext(), empEveShiftList, this);
                    eveShift.setAdapter(eveShiftAdapter);
                    eveShift.setLayoutManager(new LinearLayoutManager(this.getContext()));
                } else {
                    //No employees scheduled
                }
            }else {//Weekend
                eveShift.setVisibility(getView().GONE);
                moon.setVisibility(getView().GONE);
            }
        }

    }

    private void dbTodaySched(int day, int mon, int year) {
        //import sched from db if avail
        DBHandler db = new DBHandler(getContext());
        today = db.getSchedbyDay(day, mon, year);

        //No Schedule for today
        if (today == null) {
            //Text when no schedule has been made
            noSched.setVisibility(getView().VISIBLE);
            sun.setVisibility(getView().GONE);
            moon.setVisibility(getView().GONE);
            weekendMoon.setVisibility(getView().GONE);
            mornShift.setVisibility(getView().GONE);
            eveShift.setVisibility(getView().GONE);
            return;
        }
        noSched.setVisibility(getView().GONE);
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        return date.format(formatter);
    }

    public void previousDayAction(View view) {
        selectedDate = selectedDate.minusDays(1);
        setDayView();
    }

    public void nextDayAction(View view) {
        selectedDate = selectedDate.plusDays(1);
        setDayView();
    }

    /**
     * Returns a list of employees given three shift ID's
     * @param ID1, ID2, ID3
     */
    public ArrayList<Employee> getEmpShift(int ID1, int ID2, int ID3){
        ArrayList<Employee> empList = new ArrayList<>();
        DBHandler db = new DBHandler(getContext());
        if (ID1 != -1 & db.getEmpById(ID1) != null) {
            empList.add(db.getEmpById(ID1));
        }
        if (ID2 != -1 & db.getEmpById(ID2) != null) {
            empList.add(db.getEmpById(ID2));
        }
        if (ID3 != -1 & db.getEmpById(ID3) != null) {
            empList.add(db.getEmpById(ID3));
        }
        return empList;
    }

    @Override
    public void selectedEmployee(Employee employee) {
        setInvis();
        //Message (optional to keep)
        Toast.makeText(this.getContext(), "Selected User "+employee.getFirstName(), Toast.LENGTH_SHORT).show();
        //Pass employee info to ViewEditEmployee fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", employee);
        bundle.putSerializable("date", selectedDate);
        ViewEditEmployeeFragment blank = new ViewEditEmployeeFragment();
        blank.setArguments(bundle);

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainerMain, blank, "Home")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitNow();
    }

    public void setInvis(){
        //Set stuff from previous screen to gone
        //Linear layout that holds imagebuttons
        top = getView().findViewById(R.id.top);
        top.setVisibility(getView().GONE);
        //Month year text
        monthDayYear.setVisibility(getView().GONE);

        RecyclerView recyclerView = getView().findViewById(R.id.empMornShift);
        recyclerView.setVisibility(getView().GONE);
        LinearLayout home_line = getView().findViewById(R.id.home_line);
        home_line.setVisibility(getView().GONE);
        //Forward and back buttons
        ImageButton forward = getView().findViewById(R.id.forwardDayBtn);
        ImageButton back = getView().findViewById(R.id.backDayBtn);
        forward.setVisibility(getView().GONE);
        back.setVisibility(getView().GONE);
        //TextViews
        monthDayYear.setVisibility(getView().GONE); //month day year
        dayOfWeek.setVisibility(getView().GONE); //Day of Week text
        noSched.setVisibility(getView().GONE); //No Sched text
        //Edit Shift button
        editToday.setVisibility(getView().GONE);
        //Displayed emp shifts
        mornShift.setVisibility(getView().GONE);
        eveShift.setVisibility(getView().GONE);
        //sun and moon
        sun.setVisibility(getView().GONE);
        moon.setVisibility(getView().GONE);
        weekendMoon.setVisibility(getView().GONE);
    }

    /*@Override It does this already ??
    public void onResume() {
        //This resets this page back to current day if manager viewed other days
        selectedDate = LocalDate.now();
        super.onResume();
    }*/
}