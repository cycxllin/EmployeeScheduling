package com.example.f22lovelace.ui.editavailability;

import static androidx.core.content.ContextCompat.getSystemService;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Avail;
import com.example.f22lovelace.classes.AvailByStatus;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.CalendarAdapter;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.Schedule;
import com.example.f22lovelace.databinding.FragmentEditavailabilityBinding;
import com.example.f22lovelace.ui.Edit.ViewEditEmployeeFragment;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.text.DateFormatSymbols;

public class EditAvailabilityFragment extends Fragment implements CalendarAdapter.CalendarClickListener, Backpressedlistener {

    //-1 == white color id; -4335202 madang; -5452564 aero; chardonnay -16274 --> from system print

    private final int madang = -4335202;
    private final int white = -1;
    private final int chardonnay = -16274;
    private final int aero = -5452564;


    public static Backpressedlistener backpressedlistener;
    private FragmentEditavailabilityBinding binding;

    //variables for a calendar view and text view object
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    private LocalDate selectedDate;
    private LocalDate today; //used to search the schedule from now on if avail changes and schedule already made

    //used to change color and list when changing availability on the calendar
    private int mode = 1; //default mode is all day

    //used for manage days, making avail objects to add to db, checkDiff for popup
    AvailByStatus defAvail;
    AvailByStatus availChanges;
    ArrayList<Avail> avails = new ArrayList<>();

    AvailByStatus totalAvail; //this is for loading the colors onto the cal only

    Employee employee;
    Button noCancel, yes, dismiss;
    ImageButton info;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditAvailabilityViewModel EditAvailabilityViewModel =
                new ViewModelProvider(this).get(EditAvailabilityViewModel.class);

        binding = FragmentEditavailabilityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYear);

        info = (ImageButton) root.findViewById(R.id.availinfo);

        TextView nameText = root.findViewById(R.id.date_view);

        today = LocalDate.now();

        //sets selected date to today and loads current month
        selectedDate = LocalDate.now();

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            employee = (Employee) bundle.getSerializable("data");
            nameText.setText(employee.getFullName());
        }

        setMonthView();

        //FullDay radio button
        RadioButton fullDay = root.findViewById(R.id.rbFullDay);
        fullDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = 1;
            }
        });

        //Morning radio button
        RadioButton morning = root.findViewById(R.id.rbMorning);
        morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = 2;
            }
        });

        //Evening radio button
        RadioButton evening = root.findViewById(R.id.rbEvening);
        evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = 3;
            }
        });

        //Forward month button
        ImageButton forward = root.findViewById(R.id.forwardBtn);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthAction();
            }
        });
        //Back month button
        ImageButton back = root.findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthAction();
            }
        });
        //Save button
        Button saveBtn = root.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAvail(employee);
                Toast.makeText(getContext(), employee.getFirstName() +"'s Availability saved for "
                        + getMonth(selectedDate.getMonthValue()), Toast.LENGTH_SHORT).show();
            }
        });

        //info button
        ImageButton info = root.findViewById(R.id.availinfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoPopup();
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setMonthView() {

        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        //new availability method
        DBHandler db = new DBHandler(getContext());
        try{
            totalAvail = db.getEmpAvailByMonth(employee.getID(), selectedDate);
            defAvail = db.getEmpDefaultAvailByMonth(employee.getID(), selectedDate);
            availChanges = db.getEmpChangedAvailbyMonth(employee.getID(), selectedDate);
        }
        catch (NullPointerException ignored){}

        CalendarAdapter calendarAdapter = new CalendarAdapter(this.getContext(), daysInMonth,
                this, totalAvail.getAllDayAvail(), totalAvail.getMornAvail(), totalAvail.getEveAvail(), totalAvail.getNotAvail(), mode);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction() {
        if (!checkDiff()){
            selectedDate = selectedDate.minusMonths(1);
            setMonthView();
        }else {//checkDiff() == true
            openPopup();
            selectedDate = selectedDate.minusMonths(1);
        }
    }

    public void nextMonthAction() {
        if (!checkDiff()){
            selectedDate = selectedDate.plusMonths(1);
            setMonthView();
        }else {//checkDiff() == true
            openPopup();
            selectedDate = selectedDate.plusMonths(1);}
    }

    @Override
    public void onItemClick(int position, String dayText, View view) {
        int colorId;

        if (!dayText.isEmpty()) {
            int day = Integer.parseInt(dayText);

            try { //if the background color has never been set, then the getColor will return a null object
                colorId = ((ColorDrawable) view.getBackground()).getColor();
                setBackgroundColor(day, view, colorId);
            } catch (NullPointerException e) {
                colorId = R.color.white;
                setBackgroundColor(day, view, colorId);
            }
        }
        //else --> it's an empty cell so don't do anything
    }

    /** @return  0 = change to white, 1 = change to green, 2 = change to yellow, 3 = change to blue
     */
    private int getSwitchCaseForColor(int colorId){
        //current color == mode color, change to white, add to notAvail
        if ((colorId == madang && mode == 1) || (colorId == chardonnay && mode == 2)
                || (colorId == aero && mode == 3)) {return 0;}

        //Mode is 1 & current color != green, add to AllDay
        else if (mode == 1 && (colorId == white || colorId == chardonnay
                || colorId == aero)) {return 1;}

        //Mode is 2 & current color != yellow, add to morn
        else if (mode == 2 && (colorId == madang || colorId == white
                || colorId == aero)) {return 2;}

        //Mode is 3 & current color != blue, add to morn
        else if (mode == 3 && (colorId == madang || colorId == chardonnay
                || colorId == white)) {return 3;}

        //it really shouldn't ever get here.....
        return 4;
    }

    /** removes day from total lists, then adds to appropriate list depending on switch case
     * @param switchCase 0 = add to Not Avail; 1 = add to AllDay; 2 = add to Morn; 3 = add to eve
     */
    public void manageTotalAvail(int day, int switchCase) {
        //remove day from all lists
        totalAvail.getAllDayAvail().removeAll(Collections.singletonList(day));
        totalAvail.getMornAvail().removeAll(Collections.singletonList(day));
        totalAvail.getEveAvail().removeAll(Collections.singletonList(day));
        totalAvail.getNotAvail().removeAll(Collections.singletonList(day));

        //adds day to appropriate list
        switch (switchCase) {
            case 0:
                totalAvail.getNotAvail().add(day);
                break;
            case 1:
                totalAvail.getAllDayAvail().add(day);
                break;
            case 2:
                totalAvail.getMornAvail().add(day);
                break;
            case 3:
                totalAvail.getEveAvail().add(day);
                break;
        }
    }


    public void setBackgroundColor(int day, View view, int colorId) {
        LocalDate temp = LocalDate.of(selectedDate.getYear(), selectedDate.getMonthValue(), day);
        int switchCase = getSwitchCaseForColor(colorId);

        switch (switchCase) {
            case 0: //change to white
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                break;
            case 1: //change to green
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.madang));
                break;
            case 2: //change to yellow
                if ((temp.getDayOfWeek()).getValue() > 5){ //it's saturday or Sunday so change to green
                    //when does selected date change???? needs to be localdate of day selected
                    //TODO popup here? to say weekends can only be green or white, and will be changed accordingly?
                    if (colorId == madang) { //cell was green, is a weekend so turn to not avail
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        switchCase = 0; //move from all day avail to notavail
                    }
                    else {//weekend background was white so change to green and move to alldayavail
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.madang));
                        switchCase = 1;
                    }
                    break;
                }
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.chardonnay));
                break;
            case 3: //change to blue
                if ((temp.getDayOfWeek()).getValue() > 5){ //it's saturday or Sunday so change to green
                    //TODO popup here? to say weekends can only be green or white, and will be changed accordingly?
                    if (colorId == madang) { //cell was green and it is a weekend so turn to not avail
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        switchCase = 0; //move from all day avail to notavail
                    }
                    else {//weekend background was white so change to green and move to alldayavail
                        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.madang));
                        switchCase = 1;
                    }
                    break;

                }
                else {
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.aero));
                    break;
                }
            default: //really shouldn't get here....
                break;
        }
        manageTotalAvail(day, switchCase);

        //make avail object for changes and add to avail list
        Avail changedAvail = new Avail(employee.getID(), selectedDate.getMonthValue(), selectedDate.getYear(),
                day, switchCase);

        avails.removeIf(a -> (a.getDay() == changedAvail.getDay() && a.getMonth() == changedAvail.getMonth()
                && a.getYear() == changedAvail.getYear()));
        avails.add(changedAvail);
    }

    /** Save Availability Popup
     */
    public boolean openPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.popup, null, false);
        PopupWindow popupWindow = new PopupWindow(popup, 450, 225, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0 ,0);

        noCancel = (Button) popup.findViewById(R.id.noCancelbtn);
        noCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                setMonthView();
            }
        });

        yes = (Button) popup.findViewById(R.id.yesbtn);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAvail(employee);
                Toast.makeText(getContext(), employee.getFirstName() +"'s Availability saved for "
                        + getMonth(selectedDate.getMonthValue()), Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                setMonthView();
            }
        });
        return false;
    }

    public void saveAvail(Employee employee){
        int i;
        Avail tempAvail;
        Schedule tempSched = null;
        for (i = 0; i< avails.size(); i++) {
            //add each avail to db
            DBHandler db = new DBHandler(getContext());
            tempAvail = avails.get(i);
            db.addEditAvail(tempAvail);
            LocalDate availDate = LocalDate.of(tempAvail.getYear(), tempAvail.getMonth(), tempAvail.getDay());
            //if emp is scheduled on the day and becomes unavail, update schedule
            try {
                if (availDate.isAfter(LocalDate.now())){ //if avail that is changing is after today then update sched
                    tempSched = db.schedIfEmpOnDay(availDate, employee);
                    //System.out.println("Was scheduled on day: " + tempSched.getDay());
                    //here if emp is scheduled on that day
                    switch (tempAvail.getStatus()) {
                        case 0: //emp was scheduled but now not avail at all
                            tempSched.removeEmp(employee.getID());
                            //System.out.println("Now not avail");
                            tempSched.setStatus(2);
                            break;
                        case 2: //emp was scheduled but now only avail morning
                            //System.out.println("Only Avail Morning");
                            if (tempSched.getEve1() == employee.getID()) {
                                tempSched.setEve1(-1);
                                tempSched.setStatus(2);
                                //System.out.println("Was Sched on eve1");
                            }
                            if (tempSched.getEve2() == employee.getID()) {
                                tempSched.setEve2(-1);
                                tempSched.setStatus(2);
                                //System.out.println("Was Sched on eve2");
                            }
                            if (tempSched.getEveBusy() == employee.getID()) {
                                tempSched.setEveBusy(-1);
                                tempSched.setStatus(2);
                                //System.out.println("Was Sched on eve3");
                            }

                            break;
                        case 3: //emp was scheduled but now only avail evening
                            //System.out.println("Only Avail Evening");
                            if (tempSched.getShift1() == employee.getID()) {
                                tempSched.setShift1(-1);
                                tempSched.setStatus(2);
                                //System.out.println("Was Sched on Morn1");
                            }
                            if (tempSched.getShift2() == employee.getID()) {
                                tempSched.setShift2(-1);
                                tempSched.setStatus(2);
                                //System.out.println("Was Sched on Morn2");
                            }
                            if (tempSched.getMornBusy() == employee.getID()) {
                                tempSched.setMornBusy(-1);
                                tempSched.setStatus(2);
                                //System.out.println("Was Sched on Morn3");
                            }

                            break;
                        default: //avail all day so no need to look at sched
                            break;
                    }
                    db.addEditSched(tempSched);
                    //System.out.println("Sched updated:" + tempSched);
                    //System.out.println("Status:" + tempSched.getStatus());
                }
            } catch (NullPointerException ignored) {} //emp not scheduled on day so no need to update
        }
    }


    /** Check to see if the info in the database is different than the current info
     */
    public boolean checkDiff(){
        //check to see if the popup needs to happen
        HashSet<Integer> currDays = new HashSet<>(totalAvail.getAllDayAvail());
        HashSet<Integer> currMorns = new HashSet<>(totalAvail.getMornAvail());
        HashSet<Integer> currEves = new HashSet<>(totalAvail.getEveAvail());
        HashSet<Integer> currNots = new HashSet<>(totalAvail.getNotAvail());

        HashSet<Integer> dbDays = new HashSet<>();
        HashSet<Integer> dbMorns = new HashSet<>();
        HashSet<Integer> dbEves = new HashSet<>();
        HashSet<Integer> dbNots = new HashSet<>();

        //get info from db
        try{
            DBHandler db = new DBHandler(getContext());
            AvailByStatus checkAvail = db.getEmpAvailByMonth(employee.getID(), selectedDate);
            dbDays.addAll(checkAvail.getAllDayAvail());
            dbMorns.addAll(checkAvail.getMornAvail());
            dbEves.addAll(checkAvail.getEveAvail());
            dbNots.addAll(checkAvail.getNotAvail());
        }
        catch (NullPointerException ignored) {
            //should always be something db (default avail)
        }
        //returns false if one of the lists aren't the same as in the db
        return !currDays.equals(dbDays) || !currMorns.equals(dbMorns) || !currEves.equals(dbEves)
                || !currNots.equals(dbNots);
    }

    public String getMonth(int month){
        return new DateFormatSymbols().getMonths()[month-1];
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
