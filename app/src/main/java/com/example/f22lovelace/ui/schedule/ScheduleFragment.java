package com.example.f22lovelace.ui.schedule;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.LocalDate.parse;
import static java.time.temporal.TemporalAdjusters.lastInMonth;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;
import com.example.f22lovelace.classes.Backpressedlistener;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.EmployeeAdapter;
import com.example.f22lovelace.classes.SchedCalendarAdapter;
import com.example.f22lovelace.classes.Schedule;
import com.example.f22lovelace.classes.ScheduleMonthByStatus;
import com.example.f22lovelace.classes.ShiftAdapter;
import com.example.f22lovelace.classes.SimilarEmployeeAdapter;
import com.example.f22lovelace.classes.UnschedEmpAdapter;
import com.example.f22lovelace.databinding.FragmentScheduleBinding;
import com.example.f22lovelace.ui.editShift.editShiftFragment;
import com.example.f22lovelace.ui.home.HomeFragment;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.ContextCompat.getSystemService;

public class ScheduleFragment extends Fragment implements SchedCalendarAdapter.CalendarClickListener,
        SchedCalendarAdapter.IconClickListener, Backpressedlistener, EmployeeAdapter.EmployeeClickListener {

    private FragmentScheduleBinding binding;
    public static Backpressedlistener backpressedlistener;
    SchedCalendarAdapter.CalendarClickListener calClick;
    SchedCalendarAdapter.IconClickListener iconClick;

    //variables for a calendar view and text view object
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Schedule sched;

    private ArrayList<Integer> done = new ArrayList<>();
    private ArrayList<Integer> inProgress = new ArrayList<>();
    private ArrayList<ArrayList<Schedule>> forWeekCheck = new ArrayList<>();
    private ArrayList<ArrayList> unschedAvailEmps = new ArrayList<ArrayList>();
    private ArrayList<String> iconSaturdays = new ArrayList<>(); //Saturdays with the icon shown
    ScheduleMonthByStatus schedule;

    private ImageView checkIcon;


    PopupWindow popupWindow;
    View popup;

    Button export, overwrite, cancel, newsched, yes;

    //width and height for pdf
    int pageHeight = 1440;
    int pageWidth = 2560;

    //variables that store images
    Bitmap bmp, scaledbmp, sun, moon, scaledsun, scaledmoon;

    //code for runtime permissions
    private static final int PERM_REQ_CODE = 200;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ScheduleViewModel ScheduleViewModel =
                new ViewModelProvider(this).get(ScheduleViewModel.class);

        //View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        export = (Button) root.findViewById(R.id.export);
        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYear);

        System.out.println("Added? = " + isAdded());

        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher_logo);
        Canvas canvas = new Canvas();
        bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bmp);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        scaledbmp = Bitmap.createScaledBitmap(bmp,140, 140, false);

        drawable = getResources().getDrawable(R.drawable.ic_menu_sun);
        canvas = new Canvas();
        sun = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(sun);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        scaledsun = Bitmap.createScaledBitmap(sun,20, 20, false);

        drawable = getResources().getDrawable(R.drawable.ic_menu_moon);
        canvas = new Canvas();
        moon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(moon);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        scaledmoon = Bitmap.createScaledBitmap(moon,20, 20, false);

        if (checkPermission()){Toast.makeText(getContext(), "ACCESS GRANTED!!", Toast.LENGTH_SHORT).show();}
        else{requestPermission();}

        Bundle bundle = this.getArguments();
        if (bundle != null){
            selectedDate = (LocalDate) bundle.getSerializable("today");
        }else {selectedDate = LocalDate.now();}
        setMonthView();

        //Forward month button
        ImageButton forward = root.findViewById(R.id.forwardBtn);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthAction(view);
            }
        });

        //Back month button
        ImageButton back = root.findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthAction(view);
            }
        });

        //exports that current months schedule as a pdf
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ArrayList> list;
                list = getUnschedAvailEmps();
                for (int i = 0;i<list.size();i++){
                    if(!((list.get(i)).isEmpty())){weekPopup();
                        return;}
                }
                String fileName;
                String month = selectedDate.getMonth().toString();
                month = month.toLowerCase();
                month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
                fileName = Integer.toString(selectedDate.getYear()) + month + "Schedule.pdf";
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, fileName);
                if (!file.exists()) {generatePDF(selectedDate, fileName);}
                else {exportPopup();}
                }
            });

        return root;
    }

    private void setMonthView()
    {
        done.clear();
        inProgress.clear();
        unschedAvailEmps.clear();
        iconSaturdays.clear();

        weekLists();
        unschedAvailEmps = getUnschedAvailEmps();

        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        ArrayList<String> saturdaysInMonth = getSaturdays(selectedDate, unschedAvailEmps);
        System.out.println(saturdaysInMonth);

        DBHandler db = new DBHandler(getContext());
        try{
            schedule = db.getSchedMonthByStatusList(selectedDate);
            done = schedule.getDone();
            inProgress = schedule.getInProgress();
        }
        catch (NullPointerException ignored){}

        SchedCalendarAdapter calendarAdapter = new SchedCalendarAdapter(this.getContext(), daysInMonth,
                this,this, done, inProgress, saturdaysInMonth);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    /**
     * Returns a list of all the saturdays in this month
     * @param date
     * @return
     */
    public ArrayList<String> getSaturdays(LocalDate date, ArrayList unschedAvailEmps){
        ArrayList<String> saturdays = new ArrayList<String>();

        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int num = getFirstSaturday(firstOfMonth);
        LocalDate firstSaturday = firstOfMonth.plusDays(num);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int count = 0;//To count the saturdays that have been parsed through and used to check
        // unschedAvailEmps list

        for(int i = 1; i <= 42; i++)
        {//Empty cells at the start and end of month
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                saturdays.add("");
            }//Actual month cells, only need the saturdays
            else{

                if ((i - dayOfWeek) == firstSaturday.getDayOfMonth() ){
                    ArrayList<Employee> empList = (ArrayList<Employee>) unschedAvailEmps.get(count);
                    //System.out.println("EmpList: " + empList);
                    //System.out.println("Big list: " + unschedAvailEmps);
                    if (empList.size() != 0){
                        saturdays.add(String.valueOf(i - dayOfWeek));
                        iconSaturdays.add(String.valueOf(i - dayOfWeek));
                    }else{
                        saturdays.add("");
                        iconSaturdays.add(String.valueOf(i - dayOfWeek));
                    }
                    count ++;
                    firstSaturday = firstSaturday.plusDays(7);
                }else{
                    saturdays.add("");
                }
            }
        }
        //System.out.println("Saturdays: " + saturdays);
        return saturdays;
    }

    /**
     * Returns the first saturday of the month
     * @param firstOfMonth
     * @return
     */
    public int getFirstSaturday(LocalDate firstOfMonth){
        LocalDate firstSaturday = firstOfMonth;
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int num = 0;
        switch(dayOfWeek) {
            case 1:
                num = 5;
                break;
            case 2:
                num = 4;
                break;
            case 3:
                num = 3;
                break;
            case 4:
                num = 2;
                break;
            case 5:
                num = 1;
                break;
            case 6:
                break;
            case 7:
                num = 6;
                break;
        }
        return num;
    }

    public void weekLists(){

        forWeekCheck.clear();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        ArrayList<Schedule> tempList = new ArrayList<>();

        LocalDate firstSunday = firstOfMonth.with(previousOrSame(SUNDAY));
        LocalDate lastSaturday = firstOfMonth.with(lastInMonth(SATURDAY));
        //System.out.println("Sunday: " + firstSunday);
        //System.out.println("Saturday: " + lastSaturday);

        DBHandler db = new DBHandler(getContext());

        int days = (int) firstSunday.until(lastSaturday, ChronoUnit.DAYS);
        int counter = 0;

        while (counter < days) {
            tempList.clear();
            for (int cat = 0; cat < 7; cat++) { //cat is a random variable name to keep track (instead of i)
                if (counter == 0 ) {
                    tempList.add(db.getSchedbyDay(firstSunday));
                }
                else {
                    tempList.add(db.getSchedbyDay(firstSunday.plusDays(counter)));
                }

                counter++;
                //System.out.println(firstSunday.plusDays(counter) + ": " + db.getSchedbyDay(firstSunday.plusDays(counter)));
            }
            forWeekCheck.add(new ArrayList<>(tempList));
        }
        //System.out.println("forWeekCheck: "+forWeekCheck);
    }

    //Unused
    public boolean weekComplete(ArrayList<Schedule> weekScheds){
        Schedule temp;
        for (int i = 0; i<weekScheds.size(); i++){
            temp = weekScheds.get(i);
            if ( temp == null || temp.getStatus() != 1)
                return false;
        }
        //gets here if all sched status are 1
        return true;
    }

    public ArrayList<ArrayList> getUnschedAvailEmps(){
        DBHandler db = new DBHandler(getContext());
        ArrayList<ArrayList> weeksUnschedAvailEmps = new ArrayList<>();
        ArrayList<Employee> allEmps = new ArrayList<>(); //only want to check active employees
        ArrayList<Schedule> temp;
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        LocalDate start;
        Schedule sched;
        int id;
        //System.out.println("How many complete weeks in month? " + forWeekCheck.size());

        //for each week in forWeekCheck
        for (int liz = 0; liz<forWeekCheck.size(); liz++){
            //Need to remake list here otherwise it updates the other lists inserted
            ArrayList<Employee> empsAvail = new ArrayList<>();
            allEmps.clear();
            allEmps = db.listActiveEmps();
            //System.out.println(liz);
            //System.out.println(weekComplete(forWeekCheck.get(liz)) + "  " + forWeekCheck.get(liz));
            temp = forWeekCheck.get(liz);

            LocalDate firstSunday = firstOfMonth.with(previousOrSame(SUNDAY));
            //System.out.println("Sunday = " + firstSunday);
            //for all active employees
            for (int snek = 0; snek < allEmps.size(); snek++) {
                //System.out.println("Employee " + allEmps.get(snek));
                //System.out.println("Is avail this week? " + db.empIsAvailInWeek(firstSunday, allEmps.get(snek)));
                //if employee is avail that week
                if (db.empIsAvailInWeek(firstSunday, allEmps.get(snek))) {
                    //add to emp set
                    empsAvail.add(allEmps.get(snek));
                }
            }
            //System.out.println("Before second if:" + empsAvail);
            //for every schedule, if emp.getID and remove emp from empsAvail
            for (int bat = 0; bat < temp.size(); bat++) {
                sched = temp.get(bat);
                Schedule finalSched = sched;
                if (finalSched != null) {
                    empsAvail.removeIf(employee -> employee.getID() == finalSched.getShift1());
                    empsAvail.removeIf(employee -> employee.getID() == finalSched.getShift2());
                    empsAvail.removeIf(employee -> employee.getID() == finalSched.getMornBusy());
                    empsAvail.removeIf(employee -> employee.getID() == finalSched.getEve1());
                    empsAvail.removeIf(employee -> employee.getID() == finalSched.getEve2());
                    empsAvail.removeIf(employee -> employee.getID() == finalSched.getEveBusy());
                }
            }
            //System.out.println("After taken out EmpsAvail: " + empsAvail);
            weeksUnschedAvailEmps.add(empsAvail);
            //System.out.println("WeeksUnsched " + weeksUnschedAvailEmps);
        }
        System.out.println(weeksUnschedAvailEmps);
        return weeksUnschedAvailEmps;
    }

    public ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    /**
     * Click icon to show popup of unscheduled available employees
     * @param daystr
     * @param view
     */
    @Override
    public void onItemClick(String daystr, View view){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        popup = layoutInflater.inflate(R.layout.week_check_popup, null, false);
        popupWindow = new PopupWindow(popup, ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0 ,0);

        LocalDate first = selectedDate.withDayOfMonth(1);
        LocalDate sunday = first.with(previousOrSame(SUNDAY));

        //Based on saturday clicked show unscheduled avail emps that week
        //System.out.println(unschedAvailEmps);
        int weekIndex = 0;
        for (String day : iconSaturdays){
            //System.out.println("Day: " + day + "Daystr: " + daystr + "weekI: " + weekIndex);
            if (day == daystr) {break;}
            weekIndex ++;
            sunday = sunday.plusDays(7);
        }

        TextView weekOf = popup.findViewById(R.id.weekOf);
        weekOf.setText(sunday.toString());

        ArrayList<Employee> empList = unschedAvailEmps.get(weekIndex);
        System.out.println("Emplist:" + empList);
        RecyclerView recyclerView = popup.findViewById(R.id.unschedEmpsRV);
        recyclerView.setHasFixedSize(true);
        UnschedEmpAdapter empAd = new UnschedEmpAdapter(this.getContext(), empList);
        recyclerView.setAdapter(empAd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button cancel = (Button) popup.findViewById(R.id.okBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

    }

    @Override
    public void onItemClick(int position, String dayText, View view){
        if (!dayText.isEmpty()) {

            //Toast.makeText(getContext(), "Clicked " + day, Toast.LENGTH_SHORT).show();
            //Pass full date text (form yyyy-mm-dd) -> In fragment look in database for that schedules info
            String yearText, monthText, full;
            DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern("yyyy");
            yearText = selectedDate.format(formatter);
            formatter = DateTimeFormatter.ofPattern("MM");
            monthText = selectedDate.format(formatter);

            if (dayText.length() == 1){
                dayText = "0" + dayText;
            }

            full = yearText + "-" + monthText + "-" + dayText;

            LocalDate today = parse(full);

            Bundle bundle = new Bundle();
            bundle.putSerializable("day", today);

            //Set a bunch of stuff from prev screen to Gone
            //Calendar days
            RecyclerView recyclerView = getView().findViewById(R.id.calendarRecyclerView);
            recyclerView.setVisibility(getView().GONE);
            //Forward and back buttons
            ImageButton forward = getView().findViewById(R.id.forwardBtn);
            ImageButton back = getView().findViewById(R.id.backBtn);
            forward.setVisibility(getView().GONE);
            back.setVisibility(getView().GONE);
            export.setVisibility(getView().GONE);
            //Month year text
            monthYearText.setVisibility(getView().GONE);
            //Label texts
            TextView done = getView().findViewById(R.id.doneLbl);
            TextView inprog = getView().findViewById(R.id.inProgLbl);
            done.setVisibility(getView().GONE);
            inprog.setVisibility(getView().GONE);
            //Week text
            View week = getView().findViewById(R.id.weekLabel);
            week.setVisibility(getView().GONE);

            editShiftFragment editshiftfragment = new editShiftFragment();
            editshiftfragment.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainerMain, editshiftfragment, "editShiftSchedule")
                    .commitNow();

        }
        //else --> Do nothing cause its a blank cell
    }

    private void generatePDF(LocalDate selectedDate, String fileName){
        PdfDocument pdf = new PdfDocument();

        String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String empName;
        //String fileName;
        String month = selectedDate.getMonth().toString();
        month = month.toLowerCase();
        month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
        //fileName = Integer.toString(selectedDate.getYear()) + month + "Schedule.pdf";

        String year = fileName.substring(0,4);

        LocalDate startDate = selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = selectedDate.with(TemporalAdjusters.lastDayOfMonth());

        DBHandler db = new DBHandler(getContext());

        Employee employee = null;

        Paint paint = new Paint();
        Paint title = new Paint();
        Paint emp = new Paint();
        Paint day = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page myPage = pdf.startPage(mypageInfo);

        day.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        day.setTextSize(40);
        day.setColor(ContextCompat.getColor(getContext(), R.color.black));

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(120);
        title.setColor(ContextCompat.getColor(getContext(), R.color.black));

        emp.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        emp.setTextSize(20);
        emp.setColor(ContextCompat.getColor(getContext(), R.color.black));

        Canvas canvas = myPage.getCanvas();
        canvas.drawText("Lovelace: " + month + " " +  year + " " + "Schedule", 200, 140, title);

        for (int i = 0; i < 7; i++){
            canvas.drawText(weekdays[i], 10+(i*364), 210, day);
        }

        for (int i = 0; i<8;i++){
            canvas.drawLine(6+(i*364), 220, 6+(i*364), 1420, paint);
        }
        for (int i = 0; i<7;i++){
            canvas.drawLine(6, 220+(i*200), 2554, 220+(i*200), paint);
        }

        int k = 0;
        int week = 0;
        int validDay = 0;

        DayOfWeek dayOfWeek = startDate.getDayOfWeek();
        int weekint = dayOfWeek.getValue();
        if (weekint == 1){k=1;}
        if (weekint == 2){k=2;}
        if (weekint == 3){k=3;}
        if (weekint == 4){k=4;}
        if (weekint == 5){k=5;}
        if (weekint == 6){k=6;}
        if (weekint == 7){k=0;}
        //LOOP THIS
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1), k++) {
            sched = db.getSchedbyDay(date);
            validDay = 0;
            if (k == 7){k=0;week++;}
            if (k==0 || k==6){
                for (int i = 0; i<4;i++){
                    if (sched == null){
                        if (i == 0) {
                            canvas.drawBitmap(scaledsun, 10+(k*364), 222+(week*200), paint);
                            canvas.drawBitmap(scaledmoon, 35+(k*364), 222+(week*200), paint);
                            canvas.drawText(Integer.toString(date.getDayOfMonth()), 320+(k*364), 260+(week*200), day);
                        }
                        else{
                            empName = "empty shift";
                            if ((i == 3)) {
                                empName = "";
                            }
                            canvas.drawText(empName, 10+(k*364), 262 + ((i - 1) * 25)+(week*200), emp);
                            validDay = 1;}
                    }
                    if (validDay == 0){
                        if (i == 0) {
                            canvas.drawBitmap(scaledsun, 10+(k*364), 222+(week*200), paint);
                            canvas.drawBitmap(scaledmoon, 35+(k*364), 222+(week*200), paint);
                            canvas.drawText(Integer.toString(date.getDayOfMonth()), 320+(k*364), 260+(week*200), day);
                        }
                        if (i == 1) {
                            if (sched.getShift1() != -1) {
                                employee = db.getEmpById(sched.getShift1());
                            }
                        }
                        if (i == 2) {
                            if (sched.getShift2() != -1) {
                                employee = db.getEmpById(sched.getShift2());
                            }
                        }
                        if (i == 3) {
                            if (sched.getMornBusy() != -1) {
                                employee = db.getEmpById(sched.getMornBusy());
                            }
                        }
                        if (i != 0) {
                            if (employee == null) {
                                empName = "empty shift";
                                if ((i == 3 && (sched.getBusy() != 1))) {
                                    empName = "";
                                }
                                canvas.drawText(empName, 10+(k*364), 262 + ((i - 1) * 25)+(week*200), emp);
                            } else {
                                empName = employee.getFullName();
                                canvas.drawText(empName, 10+(k*364), 262 + ((i - 1) * 25)+(week*200), emp);
                                employee = null;
                            }
                        }}
                }
            }
            else {
            for (int i = 0; i < 8; i++) {
                if (sched == null){
                    if (i == 0) {
                        canvas.drawBitmap(scaledsun, 10+(k*364), 222+(week*200), paint);
                        canvas.drawText(Integer.toString(date.getDayOfMonth()), 320+(k*364), 260+(week*200), day);
                    }
                    else if (i == 4) {
                        canvas.drawBitmap(scaledmoon, 10+(k*364), 322+(week*200), paint);
                    }else{
                    empName = "empty shift";
                    if ((i == 3) || (i == 7)) {
                        empName = "";
                    }
                    canvas.drawText(empName, 10+(k*364), 262 + ((i - 1) * 25)+(week*200), emp);
                    validDay = 1;}
                }
                if (validDay == 0){
                if (i == 0) {
                    canvas.drawBitmap(scaledsun, 10+(k*364), 222+(week*200), paint);
                    canvas.drawText(Integer.toString(date.getDayOfMonth()), 320+(k*364), 260+(week*200), day);
                }
                if (i == 1) {
                    if (sched.getShift1() != -1) {
                        employee = db.getEmpById(sched.getShift1());
                    }
                }
                if (i == 2) {
                    if (sched.getShift2() != -1) {
                        employee = db.getEmpById(sched.getShift2());
                    }
                }
                if (i == 3) {
                    if (sched.getMornBusy() != -1) {
                        employee = db.getEmpById(sched.getMornBusy());
                    }
                }
                if (i == 4) {
                    canvas.drawBitmap(scaledmoon, 10+(k*364), 322+(week*200), paint);
                }
                if (i == 5) {
                    if (sched.getEve1() != -1) {
                        employee = db.getEmpById(sched.getEve1());
                    }
                }
                if (i == 6) {
                    if (sched.getEve2() != -1) {
                        employee = db.getEmpById(sched.getEve2());
                    }
                }
                if (i == 7) {
                    if (sched.getEveBusy() != -1) {
                        employee = db.getEmpById(sched.getEveBusy());
                    }
                }

                if (i != 0 && i != 4) {
                    if (employee == null) {
                        empName = "empty shift";
                        if ((i == 3 && (sched.getBusy() != 1)) || (i == 7 && (sched.getBusy() != 1))) {
                            empName = "";
                        }
                        canvas.drawText(empName, 10+(k*364), 262 + ((i - 1) * 25)+(week*200), emp);
                    } else {
                        empName = employee.getFullName();
                        canvas.drawText(empName, 10+(k*364), 262 + ((i - 1) * 25)+(week*200), emp);
                        employee = null;
                    }
                }}
            }
        }
        }
        //THE DAY LOOP ENDS HERE

        canvas.drawBitmap(scaledbmp, 6, 10, paint);
        pdf.finishPage(myPage);

        File root = Environment.getExternalStorageDirectory();
        File file = new File(root, fileName);
        try{
            if (file.exists()){file.delete();file.createNewFile();}

            pdf.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "Schedule made!!", Toast.LENGTH_SHORT).show();
        } catch(IOException e){
            Toast.makeText(getContext(), "FLOPP!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        pdf.close();
    }

    private boolean checkPermission() {
    // checking of permissions.
    int permission1 = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE);
    int permission2 = ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE);
    return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED && Environment.isExternalStorageManager();}

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERM_REQ_CODE);
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.fromParts("package", "com.example.f22lovelace", null);
        intent.setData(uri);
        startActivity(intent);
    }

    public int weekPopup(){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.validweekpopup, null, false);
        PopupWindow popupWindow = new PopupWindow(popup, 600, 300, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);


        cancel = (Button) popup.findViewById(R.id.flopbtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {popupWindow.dismiss();}});

        yes = (Button) popup.findViewById(R.id.slaybtn);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName;
                String month = selectedDate.getMonth().toString();
                month = month.toLowerCase();
                month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
                fileName = Integer.toString(selectedDate.getYear()) + month + "Schedule.pdf";
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, fileName);
                if (!file.exists()) {generatePDF(selectedDate, fileName);}
                else {exportPopup();}
                popupWindow.dismiss();
            }});



        return 1;
    }

    public int exportPopup(){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(getContext(), LayoutInflater.class);
        View popup = layoutInflater.inflate(R.layout.schedule_popup, null, false);
        PopupWindow popupWindow = new PopupWindow(popup, 450, 500, true);
        popupWindow.setSplitTouchEnabled(false);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);


        cancel = (Button) popup.findViewById(R.id.cancelbtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {popupWindow.dismiss();}});

        newsched = (Button) popup.findViewById(R.id.newbtn);
        newsched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName;
                String month = selectedDate.getMonth().toString();
                month = month.toLowerCase();
                month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
                fileName = Integer.toString(selectedDate.getYear()) + month + "Schedule.pdf";
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, fileName);
                int cnt = 1;
                fileName = fileName.substring(0, fileName.length() - 4);
                while (new File(root, fileName + cnt + ".pdf").exists()){
                    cnt++;
                }
                fileName = fileName + cnt + ".pdf";
                generatePDF(selectedDate, fileName);
                popupWindow.dismiss();
            }});

        overwrite = (Button) popup.findViewById(R.id.overwritebtn);
        overwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName;
                String month = selectedDate.getMonth().toString();
                month = month.toLowerCase();
                month = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase();
                fileName = Integer.toString(selectedDate.getYear()) + month + "Schedule.pdf";
                File root = Environment.getExternalStorageDirectory();
                File file = new File(root, fileName);
                generatePDF(selectedDate, fileName);
                popupWindow.dismiss();
            }});



        return 1;
    }

    @Override
    public void onPause() {
        backpressedlistener=null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener = this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }

    @Override
    public void onBackPressed(){
        this.onDestroyView();
        HomeFragment homefragment = new HomeFragment();
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainerMain, homefragment, null)
                .commitNow();
    }

    @Override
    public void selectedEmployee(Employee employee) {

    }
}