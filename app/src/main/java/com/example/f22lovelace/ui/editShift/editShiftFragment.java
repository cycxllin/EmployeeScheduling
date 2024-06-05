package com.example.f22lovelace.ui.editShift;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.SchedCalendarAdapter;
import com.example.f22lovelace.classes.Schedule;
import com.example.f22lovelace.classes.SpinnerImageArrayAdapter;
import com.example.f22lovelace.databinding.FragmentEditShiftBinding;
import com.example.f22lovelace.ui.home.HomeFragment;
import com.example.f22lovelace.ui.schedule.ScheduleFragment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class editShiftFragment extends Fragment implements Backpressedlistener, SchedCalendarAdapter.CalendarClickListener {

    private EditShiftViewModel mViewModel;
    public static Backpressedlistener backpressedlistener;
    public FragmentEditShiftBinding binding;

    public static editShiftFragment newInstance() {
        return new editShiftFragment();
    }

    public Button saveShift, noCancel, yes;
    public Spinner morn1Spinner, morn2Spinner, morn3Spinner, eve1Spinner, eve2Spinner, eve3Spinner;
    public CheckBox busyDay;
    public TextView monthYear, sunText, monText, tueText, wedText, thuText, friText, satText;
    public TextView needOpenTrain, needCloseTrain, needCloseTrain2, needBothTrain, dupWarn;

    public ImageView open, close1, close2;
    ImageButton back, forward;

    private LocalDate selectedDate;


    //private RecyclerView weeklyRecyclerView;

    //create lists of avail emps per shift - altered by spinners as changes made
    private ArrayList<Employee> shift1Emps = new ArrayList<>();
    private ArrayList<Employee> shift2Emps = new ArrayList<>();
    private ArrayList<Employee> morn3Emps = new ArrayList<>();
    private ArrayList<Employee> eve1Emps = new ArrayList<>();
    private ArrayList<Employee> eve2Emps = new ArrayList<>();
    private ArrayList<Employee> eve3Emps = new ArrayList<>();

    //create lists of avail emps per shift - altered by spinners as changes made
    private final ArrayList<Employee> dbshift1Emps = new ArrayList<>();
    private final ArrayList<Employee> dbshift2Emps = new ArrayList<>();
    private final ArrayList<Employee> dbmorn3Emps = new ArrayList<>();
    private final ArrayList<Employee> dbeve1Emps = new ArrayList<>();
    private final ArrayList<Employee> dbeve2Emps = new ArrayList<>();
    private final ArrayList<Employee> dbeve3Emps = new ArrayList<>();

    ArrayList<ArrayList<Employee>> allEmpLists = new ArrayList<>();
    ArrayList<ArrayList<Employee>> dbAllEmpLists;

    private ArrayList<Integer> openList = new ArrayList<>();
    private ArrayList<Integer> closeList = new ArrayList<>();

    int loadedOnceFlag;

    Schedule today;
    Schedule imported; //used to compare db schedule to current in order to determine if pop up required
    Employee tempEmp;

    private final ArrayList<Spinner> spinners = new ArrayList<>(); //used in error schecking



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        EditShiftViewModel editShiftViewModel =
                new ViewModelProvider(this).get(EditShiftViewModel.class);

        View view = inflater.inflate(R.layout.fragment_edit_shift, container, false);

        //binding = FragmentEditShiftBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();

        monthYear = view.findViewById(R.id.curMonth);
        busyDay = view.findViewById(R.id.busyDay);
        morn1Spinner = view.findViewById(R.id.asgnMornEmp1);
        morn2Spinner = view.findViewById(R.id.asgnMornEmp2);
        morn3Spinner = view.findViewById(R.id.asgnMornEmp3);
        eve1Spinner = view.findViewById(R.id.asgnEveEmp1);
        eve2Spinner = view.findViewById(R.id.asgnEveEmp2);
        eve3Spinner = view.findViewById(R.id.asgnEveEmp3);

        spinners.add(morn1Spinner);
        spinners.add(morn2Spinner);
        spinners.add(morn3Spinner);
        spinners.add(eve1Spinner);
        spinners.add(eve2Spinner);
        spinners.add(eve3Spinner);

        open = view.findViewById(R.id.open);
        close1 = view.findViewById(R.id.close);
        close2 = view.findViewById(R.id.close2);
        //weeklyRecyclerView = root.findViewById(R.id.weekrecyclerView);

        sunText = view.findViewById(R.id.sun);
        monText = view.findViewById(R.id.mon);
        tueText = view.findViewById(R.id.tue);
        wedText = view.findViewById(R.id.wed);
        thuText = view.findViewById(R.id.thu);
        friText = view.findViewById(R.id.fri);
        satText = view.findViewById(R.id.sat);

        needOpenTrain = view.findViewById(R.id.openTrainWarn);
        needCloseTrain = view.findViewById(R.id.closeTrainWarn);
        needCloseTrain2 = view.findViewById(R.id.closeTrainWarnWeekend);
        needBothTrain = view.findViewById(R.id.bothTrainWarn);
        dupWarn = view.findViewById(R.id.dupWarn);

        saveShift = view.findViewById(R.id.saveShift);

        forward = view.findViewById(R.id.forwardDayWBtn);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDayAction(view);
            }
        });

        back = view.findViewById(R.id.backDayWBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousDayAction(view);
            }
        });

        //Get info from selected date
        Bundle bundle = this.getArguments();
        LocalDate selected = (LocalDate) bundle.getSerializable("day");
        selectedDate = selected;

        setWeekView(selectedDate.getDayOfMonth(), selected.getMonthValue(), selectedDate.getYear());

        //set spinner selection listeners
        //region
        morn1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempEmp = (Employee) adapterView.getItemAtPosition(i);
                if (loadedOnceFlag == 1) {
                    //user has changed selection
                    if (tempEmp.getID() != today.getShift1()){
                        today.setShift1(tempEmp.getID());
                        resetAllSpinners();
                        seeTrainWarn();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        morn2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempEmp = (Employee) adapterView.getItemAtPosition(i);
                if (loadedOnceFlag == 1) {
                    //user has changed selection
                    if (tempEmp.getID() != today.getShift2()){
                        today.setShift2(tempEmp.getID());
                        resetAllSpinners();
                        seeTrainWarn();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        morn3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempEmp = (Employee) adapterView.getItemAtPosition(i);
                if (loadedOnceFlag == 1) {
                    //user has changed selection
                    if (tempEmp.getID() != today.getMornBusy()){
                        today.setMornBusy(tempEmp.getID());
                        resetAllSpinners();
                        seeTrainWarn();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        eve1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempEmp = (Employee) adapterView.getItemAtPosition(i);
                if (loadedOnceFlag == 1) {
                    //user has changed selection
                    if (tempEmp.getID() != today.getEve1()){
                        today.setEve1(tempEmp.getID());
                        resetAllSpinners();
                        seeTrainWarn();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        eve2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempEmp = (Employee) adapterView.getItemAtPosition(i);
                if (loadedOnceFlag == 1) {
                    //user has changed selection
                    if (tempEmp.getID() != today.getEve2()){
                        today.setEve2(tempEmp.getID());
                        resetAllSpinners();
                        seeTrainWarn();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        eve3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempEmp = (Employee) adapterView.getItemAtPosition(i);
                if (loadedOnceFlag == 1) {
                    //user has changed selection
                    if (tempEmp.getID() != today.getEveBusy()){
                        today.setEveBusy(tempEmp.getID());
                        resetAllSpinners();
                        seeTrainWarn();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        //endregion

        busyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!busyDay.isChecked()){
                    today.setMornBusy(-1);
                    today.setEveBusy(-1);
                    today.setBusy(0);
                    seeTrainWarn();
                }

                if (selectedDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    busyWeekendStatus(busyDay.isChecked());
                    resetAllSpinners();
                } else {
                    busyWeekDayStatus(busyDay.isChecked());
                    resetAllSpinners();
                }
            }
        });
        saveShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSched();
                    DBHandler db = new DBHandler(getContext());
                    db.addEditSched(today);
                    setWeekView(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear());
                    //Toast messages if done or not
                    if (today.getStatus() == 1) {//Done
                        Toast.makeText(getContext(), "FINISHED shift saved for " + selectedDate.getMonth()
                                + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getYear(), Toast.LENGTH_SHORT).show();
                    } else {//today.getStatus() == 2
                        Toast.makeText(getContext(), "UNFINISHED shift saved for " + selectedDate.getMonth()
                                + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getYear(), Toast.LENGTH_SHORT).show();
                    }

            }
        });

        return view;
    }

    private void dbTodaySched(int day, int mon, int year) {
        //import sched from db if avail
        DBHandler db = new DBHandler(getContext());
        today = db.getSchedbyDay(day, mon, year);
        imported = db.getSchedbyDay(day, mon, year);//Sched from db, to be compared to in checkDiff

        //initialize sched object if nothing in db; all other options set to default (-1 for employees, 0 for busy)
        if (today == null) {
            today = new Schedule(day, mon, year);
        }
    }

    private int getDayOfWeekInt() {
        DayOfWeek dayOfWeek = selectedDate.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    private void setWeekView(int day, int month, int year) {
        //Month day Year
        monthYear.setText(monthYearFromDate(selectedDate));

        //update todays schedule
        dbTodaySched(day, month, year);

        getAllAvailEmps();

        //TODO make day of week clickable to go to edit that day
        //highlight text based on day of week
        int dayofweek = getDayOfWeekInt();
        highlightDay(dayofweek);

        //set busy box
        if (today.getBusy() == 1) {
            busyDay.setChecked(true);
            if (dayofweek > 5) {
                busyWeekendStatus(true);
            } else {
                busyWeekDayStatus(true);
            }
        } else {
            busyDay.setChecked(false);
        }

        //Set visibility stuff
        //Weekend Shifts are different from Week shifts
        if (dayofweek > 5) {
            weekendView();
        } else {//If spinners and stuff have been hidden, show them again
            weekView();
        }
        setAllSpinners();
        seeTrainWarn();
    }

    private void resetSpinnerBG() {
        //reset all spinner backgrounds
        int j;
        for (j = 0; j < spinners.size(); j++) {
            spinners.get(j).setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_custom_spinner));
        }
    }

    private void highlightDay(int dayofweek) {
        //clear background of all days
        monText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        tueText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        wedText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        thuText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        friText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        sunText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        satText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));

        //given day of week highlight the appropriate day in color of schedule progress
        switch (dayofweek) {
            case 1: //monday
                setBackgroundforDay(monText);
                break;
            case 2: //tuesday
                setBackgroundforDay(tueText);
                break;
            case 3: //wednesday
                setBackgroundforDay(wedText);
                break;
            case 4: //thursday
                setBackgroundforDay(thuText);
                break;
            case 5: //friday
                setBackgroundforDay(friText);
                break;
            case 6: //saturday
                setBackgroundforDay(satText);
                break;
            case 7: //sunday
                setBackgroundforDay(sunText);
        }
    }

    private void setBackgroundforDay(TextView box) {
        if (today.getStatus() == 1) {
            box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.madang));
        } else {
            box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chardonnay));
        }
    }

    private void weekendView() {
        if (close2.getVisibility() == getView().INVISIBLE) {
            close2.setVisibility(getView().VISIBLE);
        }
        //Hide evening shift information
        if (close1.getVisibility() == getView().VISIBLE) {
            close1.setVisibility(getView().INVISIBLE);
        }

        if (eve1Spinner.getVisibility() == getView().VISIBLE) {
            eve1Spinner.setVisibility(getView().INVISIBLE);
            eve2Spinner.setVisibility(getView().INVISIBLE);
        }
        //For some reason 3rd shift is being shown even though on the xml its automatically set to
        //invisible so this is just a quick fix
        if (morn3Spinner.getVisibility() == getView().VISIBLE & !busyDay.isChecked()) {
            morn3Spinner.setVisibility(getView().INVISIBLE);
            eve3Spinner.setVisibility(getView().INVISIBLE);
        }
    }

    private void weekView() {
        if (close2.getVisibility() == getView().VISIBLE) {
            close2.setVisibility(getView().INVISIBLE);
        }
        if (close1.getVisibility() == getView().INVISIBLE) {
            close1.setVisibility(getView().VISIBLE);
        }
        if (eve1Spinner.getVisibility() == getView().INVISIBLE) {
            eve1Spinner.setVisibility(getView().VISIBLE);
            eve2Spinner.setVisibility(getView().VISIBLE);
        }
        //For some reason 3rd shift is being shown even though on the xml its automatically set to
        //invisible so this is just a quick fix
        if (morn3Spinner.getVisibility() == getView().VISIBLE & !busyDay.isChecked()) {
            morn3Spinner.setVisibility(getView().INVISIBLE);
            eve3Spinner.setVisibility(getView().INVISIBLE);
        }

    }

    private void busyWeekDayStatus(boolean busy) {
        if (busy) {
            morn3Spinner.setVisibility(getView().VISIBLE);
            eve3Spinner.setVisibility(getView().VISIBLE);
            today.setBusy(1);
        } else {
            morn3Spinner.setVisibility(getView().INVISIBLE);
            eve3Spinner.setVisibility(getView().INVISIBLE);
            today.setBusy(0);
        }
    }

    private void busyWeekendStatus(boolean busy) {
        if (busy) {
            morn3Spinner.setVisibility(getView().VISIBLE);
            eve3Spinner.setVisibility(getView().INVISIBLE);
            today.setBusy(1);
        } else {
            morn3Spinner.setVisibility(getView().INVISIBLE);
            today.setBusy(0);
        }
    }

    private SpinnerAdapter getAdapter(ArrayList<Employee> employeesAvail) {
        //Adapter for spinner
        //ArrayAdapter<Employee> dataAdapter = new ArrayAdapter<Employee>(getContext(), android.R.layout.simple_spinner_item, employeesAvail);

        SpinnerAdapter dataAdapter = new SpinnerImageArrayAdapter(getContext(),
                R.layout.spinner_layout, employeesAvail);

        // Drop down layout style
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return dataAdapter;
    }

    public void addToAvailListByStatus(Employee emp, int status) {
        switch (status) {
            case 1: //emp avail all day
                dbshift1Emps.add(emp);
                dbshift2Emps.add(emp);
                dbmorn3Emps.add(emp);
                dbeve1Emps.add(emp);
                dbeve2Emps.add(emp);
                dbeve3Emps.add(emp);
                break;
            case 2: //only morn
                dbshift1Emps.add(emp);
                dbshift2Emps.add(emp);
                dbmorn3Emps.add(emp);
                break;
            case 3: //only eves
                dbeve1Emps.add(emp);
                dbeve2Emps.add(emp);
                dbeve3Emps.add(emp);
                break;
            case 0: //not avail so don't add
                break;
        }
    }

    /**
     * Loads avail lists from database
     */
    public void getAllAvailEmps() {
        dbshift1Emps.clear();
        dbshift2Emps.clear();
        dbmorn3Emps.clear();
        dbeve1Emps.clear();
        dbeve2Emps.clear();
        dbeve3Emps.clear();

        ArrayList<Employee> allEmps;

        DBHandler db = new DBHandler(getContext());
        allEmps = db.listAllEmps();

        int def;
        int changed;

        int i;
        for (i = 0; i < allEmps.size(); i++) {
            Employee temp = allEmps.get(i);
            def = db.checkDefAvail(temp.getID(), selectedDate);
            changed = db.checkChangedAvail(temp.getID(), selectedDate);

                if (changed < 4) {
                    addToAvailListByStatus(temp, changed);
                }
                //else changed is 4 so do whatever def is
                else {
                    addToAvailListByStatus(temp, def);
                }
            }
        }

    /**adds any employees scheduled for the day to db avail lists if the day is in the past*/
    private void addPastEmps() {
        DBHandler db = new DBHandler(getContext());
        LocalDate now = LocalDate.now();
        int empID;
        Employee temp;

        if (selectedDate.isEqual(now) || selectedDate.isBefore(now)) {
            empID = today.getShift1();
            temp = db.getEmpById(empID);
            if (empID > 0) {
                shift1Emps.add(temp);
            }
            empID = today.getShift2();
            temp = db.getEmpById(empID);
            if (empID > 0) {
                shift2Emps.add(temp);
            }
            empID = today.getMornBusy();
            temp = db.getEmpById(empID);
            if (empID > 0) {
                morn3Emps.add(temp);
            }
            empID = today.getEve1();
            temp = db.getEmpById(empID);
            if (empID > 0) {
                eve1Emps.add(temp);
            }
            empID = today.getEve2();
            temp = db.getEmpById(empID);
            if (empID > 0) {
                eve2Emps.add(temp);
            }
            empID = today.getEveBusy();
            temp = db.getEmpById(empID);
            if (empID > 0) {
                eve3Emps.add(temp);
            }
        }
    }

    private void resetAllSpinners() {
        resetSpinnerBG();
        //reset spinner lists to db
        shift1Emps.clear();
        shift2Emps.clear();
        morn3Emps.clear();
        eve1Emps.clear();
        eve2Emps.clear();
        eve3Emps.clear();
        allEmpLists.clear();

        shift1Emps.addAll(dbshift1Emps);
        shift2Emps.addAll(dbshift2Emps);
        morn3Emps.addAll(dbmorn3Emps);
        eve1Emps.addAll(dbeve1Emps);
        eve2Emps.addAll(dbeve2Emps);
        eve3Emps.addAll(dbeve3Emps);

        addPastEmps();

        //sort spinner list alphabetically
        Collections.sort(shift1Emps);
        Collections.sort(shift2Emps);
        Collections.sort(morn3Emps);
        Collections.sort(eve1Emps);
        Collections.sort(eve2Emps);
        Collections.sort(eve3Emps);

        allEmpLists = new ArrayList<>(Arrays.asList(shift1Emps, shift2Emps,
                morn3Emps, eve1Emps, eve2Emps, eve3Emps));

        //spinners invisible depending on day of week so no need to check day of week here
        int i;
        for (i=0;i<spinners.size();i++)
        {
            setSpinner(allEmpLists.get(i), spinners.get(i), i);}
    }

    private void setAllSpinners() {
        loadedOnceFlag = 1;

        resetSpinnerBG();

        shift1Emps.clear();
        shift2Emps.clear();
        morn3Emps.clear();
        eve1Emps.clear();
        eve2Emps.clear();
        eve3Emps.clear();
        allEmpLists.clear();

        shift1Emps.addAll(dbshift1Emps);
        shift2Emps.addAll(dbshift2Emps);
        morn3Emps.addAll(dbmorn3Emps);
        eve1Emps.addAll(dbeve1Emps);
        eve2Emps.addAll(dbeve2Emps);
        eve3Emps.addAll(dbeve3Emps);

        addPastEmps();

        //sort spinner list alphabetically
        Collections.sort(shift1Emps);
        Collections.sort(shift2Emps);
        Collections.sort(morn3Emps);
        Collections.sort(eve1Emps);
        Collections.sort(eve2Emps);
        Collections.sort(eve3Emps);

        allEmpLists = new ArrayList<>(Arrays.asList(shift1Emps, shift2Emps,
                morn3Emps, eve1Emps, eve2Emps, eve3Emps));

        //spinners invisible depending on day of week so no need to check day of week here
        int i;
        for (i=0;i<spinners.size();i++)
        {
            setSpinner(allEmpLists.get(i), spinners.get(i), i);}
    }

    /**
     * @return the index of the id on the list given; 0 if ID is not on the list
     * 0 corresponds to Choose employee option
     */
    private int indexOfSavedEmp(ArrayList<Employee> emps, int empID) {

        int i;
        for (i = 0; i < emps.size(); i++) {
            //go through all emps to find index of correct emp
            if (emps.get(i).getID() == empID) {
                return i;
            }
        }
        return 0;
    }

    private void nextDayAction(View view) {
        if (checkDiff()) {
            resetSpinnerBG();
            selectedDate = selectedDate.plusDays(1);
            setWeekView(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear());
        } else {
            openPopup();
        }
    }

    private void previousDayAction(View view) {
        if (checkDiff()) {
            resetSpinnerBG();
            selectedDate = selectedDate.minusDays(1);
            setWeekView(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear());
        } else {
            openPopup();
        }
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        return date.format(formatter);
    }


    public boolean checkDiff() {
        if (imported != null) {
            return today.equals(imported);
        } else {
            if (today.equals(new Schedule(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear()))) {
                //If nothing has been done to the schedule and there is no info in the database
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Save Shift Popup
     */
    public boolean openPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.edit_shift_popup, null, false);
        PopupWindow popupWindow = new PopupWindow(popup, 525, 200, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        noCancel = (Button) popup.findViewById(R.id.noCancelbtn);
        noCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                setWeekView(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear());
            }
        });

        yes = (Button) popup.findViewById(R.id.yesbtn);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSched();
                DBHandler db = new DBHandler(getContext());
                db.addEditSched(today);
                setWeekView(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear());
                //Toast messages if done or not
                if (today.getStatus() == 1){//Done
                    Toast.makeText(getContext(), "FINISHED shift saved for " + selectedDate.getMonth()
                            + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getYear(), Toast.LENGTH_SHORT).show();
                }else {//today.getStatus() == 2
                    Toast.makeText(getContext(), "UNFINISHED shift saved for " + selectedDate.getMonth()
                            + " " + selectedDate.getDayOfMonth() + " " + selectedDate.getYear(), Toast.LENGTH_SHORT).show();
                }

                popupWindow.dismiss();
            }
        });
        return false;
    }

    @Override
    public void onItemClick(String dayTxt, View view) {

    }

    public void onItemClick(int position, String dayText, View view) {

    }

    /**
     * error checking and change status when done
     */
    public void checkSched() {
        openList.clear();
        closeList.clear();

        boolean train = checkTraining();
        boolean noDups = checkRepeatEmps();
        boolean full = checkAllEmpsFilled();

        if (train && noDups && full){
            today.setStatus(1);//Finished schedule
            dupWarn.setVisibility(getView().GONE);
        }
        else{
            today.setStatus(2);//Unfinished schedule
            seeTrainWarn();
        }
    }

    /**
     * return the employee id of the selected employee in the given spinner
     */
    public int getEmpIDofSel(Spinner spinner) {

        Employee selEmp = (Employee) spinner.getSelectedItem();
        return selEmp.getID();
    }

    /** Not in use
     */
    public boolean checkRepeatEmps() {

        resetSpinnerBG();
        //check for employee in multiple shifts
        //for spinners that are visible, get employee info, if id in one == id in any others --> red box around spinner?
        HashMap<Integer, Spinner> empSched = new HashMap<>();

        int id, i;
        boolean dups = true;
        Spinner spinner;

        for (i = 0; i < spinners.size(); i++) {
            if (spinners.get(i).getVisibility() == View.VISIBLE) {
                spinner = spinners.get(i);
                id = getEmpIDofSel(spinner);

                if (id > 0) {
                    if (!empSched.containsKey(id)) { //first time employee appears
                        empSched.put(id, spinner);
                    } else { //employee appears more than once on schedule and is not default emp (-1)
                        dups = false;
                        //set background of spinner to red
                        spinner.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_spinner));
                        empSched.get(id).setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_spinner));
                        Toast.makeText(getContext(), "Cannot schedule employee for both shifts!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return dups;
    }

    public boolean checkAllEmpsFilled() {
        int id, i;
        Spinner spinner;

        for (i = 0; i < spinners.size(); i++) {
            spinner = spinners.get(i);
            if (spinners.get(i).getVisibility() == View.VISIBLE) {
                id = getEmpIDofSel(spinner);

                if (id < 0) {
                    //a visible spinner is not filled out
                    return false;
                }
            }
            else {
                spinner.setSelection(0);
            }
        }
        return true;
    }

    public boolean checkTraining() {
        int dayInt = getDayOfWeekInt();

        switch (dayInt) {
            case 6: //Saturday
            case 7: //Sunday
                if (today.getBusy() == 0) {
                    openClose(3, morn1Spinner);
                    openClose(3, morn2Spinner);
                } else { //busy weekend
                    openClose(3, morn1Spinner);
                    openClose(3, morn2Spinner);
                    openClose(3, morn3Spinner);
                }
                break;
            default: //weekday
                if (today.getBusy() == 0) {
                    openClose(0, morn1Spinner);
                    openClose(0, morn2Spinner);
                    openClose(1, eve1Spinner);
                    openClose(1, eve2Spinner);
                } else { //busy week
                    openClose(0, morn1Spinner);
                    openClose(0, morn2Spinner);
                    openClose(0, morn3Spinner);
                    openClose(1, eve1Spinner);
                    openClose(1, eve2Spinner);
                    openClose(1, eve3Spinner);
                }
        }
        return !openList.isEmpty() && !closeList.isEmpty();
    }

    /**
     * checks employee selected in spinner and adds to open or close list if trained
     * For use in error checking
     */
    private void openClose(int training, Spinner spinner) {
        Employee selEmp = (Employee) spinner.getSelectedItem();

        switch (training) {
            case 0: //looking for open
                if (selEmp.getOpen() > 0) {
                    openList.add(selEmp.getID());
                    break;
                }
                break;
            case 1: //looking for close
                if (selEmp.getClose() > 0) {
                    closeList.add(selEmp.getID());
                    break;
                }
                break;
            case 3: //looking for both
                if (selEmp.getOpen() > 0) {
                    openList.add(selEmp.getID());
                }
                if (selEmp.getClose() >0) {
                    closeList.add(selEmp.getID());
                }
                break;
        }
    }

    public void seeTrainWarn(){
        //See what kind of training manager needs
        openList.clear();
        closeList.clear();
        checkTraining();

        //set all text boxes to hide
        needBothTrain.setVisibility(getView().GONE);
        needOpenTrain.setVisibility(getView().GONE);
        needCloseTrain.setVisibility(getView().GONE);
        needCloseTrain2.setVisibility(getView().GONE);

        if (selectedDate.getDayOfWeek().getValue() > 5){
            //Weekend
            if (openList.isEmpty()){
                open.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_box));
            }
            else{
                open.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_box));
            }
            if (closeList.isEmpty()){
                close2.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_box));
            }
            else{
                close2.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_box));
            }


        }else{
            //Week
            if (openList.isEmpty()){
                open.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_box));
            }
            else{
                open.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_box));
            }
            if (closeList.isEmpty()){
                close1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_box));
            }
            else{
                close1.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_box));
            }
        }
    }

    public void setInvis(){
        monthYear.setVisibility(getView().GONE);
        busyDay.setVisibility(getView().GONE);
        morn1Spinner.setVisibility(getView().GONE);
        morn2Spinner.setVisibility(getView().GONE);
        morn3Spinner.setVisibility(getView().GONE);
        eve1Spinner.setVisibility(getView().GONE);
        eve2Spinner.setVisibility(getView().GONE);
        eve3Spinner.setVisibility(getView().GONE);

        morn1Spinner.setVisibility(getView().GONE);
        morn2Spinner.setVisibility(getView().GONE);
        morn3Spinner.setVisibility(getView().GONE);
        eve1Spinner.setVisibility(getView().GONE);
        eve2Spinner.setVisibility(getView().GONE);
        eve3Spinner.setVisibility(getView().GONE);

        close1.setVisibility(getView().GONE);
        close2.setVisibility(getView().GONE);

        sunText.setVisibility(getView().GONE);
        monText.setVisibility(getView().GONE);
        tueText.setVisibility(getView().GONE);
        wedText.setVisibility(getView().GONE);
        thuText.setVisibility(getView().GONE);
        friText.setVisibility(getView().GONE);
        satText.setVisibility(getView().GONE);

        needOpenTrain.setVisibility(getView().GONE);
        needCloseTrain.setVisibility(getView().GONE);
        needCloseTrain2.setVisibility(getView().GONE);
        needBothTrain.setVisibility(getView().GONE);
        dupWarn.setVisibility(getView().GONE);

        saveShift.setVisibility(getView().GONE);
        back.setVisibility(getView().GONE);
        forward.setVisibility(getView().GONE);
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
    public void onDestroy(){
        super.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onBackPressed(){
        if (this.getTag() == "editShiftSchedule"){
            this.onDestroy();
            this.onDestroyView();
            Bundle bundle = new Bundle();
            bundle.putSerializable("today", selectedDate);
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            scheduleFragment.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .detach(this)
                    .replace(R.id.fragmentContainerMain, scheduleFragment, null)
                    .commitNow();}
        
        if (this.getTag() == "editShiftHome"){
            this.onDestroy();
            this.onDestroyView();
            HomeFragment homefragment = new HomeFragment();
            Bundle bundle = new Bundle();
            homefragment.setArguments(bundle);
            bundle.putSerializable("today", selectedDate);
            getParentFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .detach(this)
                    .replace(R.id.fragmentContainerMain, homefragment, null)
                    .commitNow();}
    }

    public void setSpinner(ArrayList<Employee> empList, Spinner spinner, int shift){
        int empID = -1; //set empID to default

        Employee defEmp = new Employee(-1, "Choose Employee", "", "",
                null, null, 0, 0, 0);

        switch (shift){
            case 0:
                empID = today.getShift1();
                empList.removeIf(e -> e.getID() == today.getShift2());
                empList.removeIf(e -> e.getID() == today.getMornBusy());
                empList.removeIf(e -> e.getID() == today.getEve1());
                empList.removeIf(e -> e.getID() == today.getEve2());
                empList.removeIf(e -> e.getID() == today.getEveBusy());
                break;
            case 1:
                empID = today.getShift2();
                empList.removeIf(e -> e.getID() == today.getShift1());
                empList.removeIf(e -> e.getID() == today.getMornBusy());
                empList.removeIf(e -> e.getID() == today.getEve1());
                empList.removeIf(e -> e.getID() == today.getEve2());
                empList.removeIf(e -> e.getID() == today.getEveBusy());
                break;
            case 2:
                empID = today.getMornBusy();
                empList.removeIf(e -> e.getID() == today.getShift1());
                empList.removeIf(e -> e.getID() == today.getShift2());
                empList.removeIf(e -> e.getID() == today.getEve1());
                empList.removeIf(e -> e.getID() == today.getEve2());
                empList.removeIf(e -> e.getID() == today.getEveBusy());
                break;
            case 3:
                empID = today.getEve1();
                empList.removeIf(e -> e.getID() == today.getShift1());
                empList.removeIf(e -> e.getID() == today.getShift2());
                empList.removeIf(e -> e.getID() == today.getMornBusy());
                empList.removeIf(e -> e.getID() == today.getEve2());
                empList.removeIf(e -> e.getID() == today.getEveBusy());
                break;
            case 4:
                empID = today.getEve2();
                empList.removeIf(e -> e.getID() == today.getShift1());
                empList.removeIf(e -> e.getID() == today.getShift2());
                empList.removeIf(e -> e.getID() == today.getMornBusy());
                empList.removeIf(e -> e.getID() == today.getEve1());
                empList.removeIf(e -> e.getID() == today.getEveBusy());
                break;
            case 5:
                empID = today.getEveBusy();
                empList.removeIf(e -> e.getID() == today.getShift1());
                empList.removeIf(e -> e.getID() == today.getShift2());
                empList.removeIf(e -> e.getID() == today.getMornBusy());
                empList.removeIf(e -> e.getID() == today.getEve1());
                empList.removeIf(e -> e.getID() == today.getEve2());
                break;
        }

        empList.add(0, defEmp);

        //Order list so that selected employee will always be at top of list, rest will
        //then be sorted alphabetically
        Employee toMove;
        toMove = empList.remove(indexOfSavedEmp(empList,empID));
        empList.add(0,toMove);

        spinner.setAdapter(getAdapter(empList));
        spinner.setSelection(0);
        //System.out.println("Employee List: "+empList);
        if (empID == -1){ //set background to red to emphasize this needs to change)
            spinner.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_spinner));
        }
    }
}
