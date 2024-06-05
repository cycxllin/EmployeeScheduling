package com.example.f22lovelace;

import androidx.annotation.NonNull;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.ui.Edit.ViewEditEmployeeFragment;
import com.example.f22lovelace.ui.addemployees.AddEmployeesFragment;
import com.example.f22lovelace.ui.defavail.DefAvailFragment;
import com.example.f22lovelace.ui.editShift.editShiftFragment;
import com.example.f22lovelace.ui.editavailability.EditAvailabilityFragment;
import com.example.f22lovelace.ui.employees.EmployeesFragment;
import com.example.f22lovelace.ui.schedule.ScheduleFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import com.example.f22lovelace.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DBHandler dbHandler;

    private Fragment fragment;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create database if it does not exist
        dbHandler = new DBHandler(MainActivity.this);
        //SQLiteDatabase is lazy meaning it won't actually create the database until you call it.
        //It's called here to check that it is working.
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.close();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_employees, R.id.nav_schedule)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerMain);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerMain);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (AddEmployeesFragment.backpressedlistener != null){
            AddEmployeesFragment.backpressedlistener.onBackPressed();
            if(ScheduleFragment.backpressedlistener != null){
                super.onBackPressed();
            }
        }
        else if(ViewEditEmployeeFragment.backpressedlistener != null){
            ViewEditEmployeeFragment.backpressedlistener.onBackPressed();
            if(ScheduleFragment.backpressedlistener != null){
                super.onBackPressed();
            }
        }
        else if(EditAvailabilityFragment.backpressedlistener != null){
            EditAvailabilityFragment.backpressedlistener.onBackPressed();
            if(ScheduleFragment.backpressedlistener != null){
                super.onBackPressed();
            }
        }
        else if(DefAvailFragment.backpressedlistener != null){
            DefAvailFragment.backpressedlistener.onBackPressed();
            if(ScheduleFragment.backpressedlistener != null){
                super.onBackPressed();
            }
        }
        else if(editShiftFragment.backpressedlistener != null && ScheduleFragment.backpressedlistener != null){
            System.out.println("THIS ONE HERE");
            editShiftFragment.backpressedlistener.onBackPressed();
            if (editShiftFragment.backpressedlistener != null){
                super.onBackPressed();}}

        else if(editShiftFragment.backpressedlistener != null && EmployeesFragment.backpressedlistener != null){
            editShiftFragment.backpressedlistener.onBackPressed();
            if(EmployeesFragment.backpressedlistener != null){
                super.onBackPressed();
            }
        }
        else if(editShiftFragment.backpressedlistener != null){
            editShiftFragment.backpressedlistener.onBackPressed();
            if(EmployeesFragment.backpressedlistener != null){
                super.onBackPressed();
            }
        }
        else if(ScheduleFragment.backpressedlistener != null){
            ScheduleFragment.backpressedlistener.onBackPressed();
            super.onBackPressed();
        }
        else if(EmployeesFragment.backpressedlistener != null){
            EmployeesFragment.backpressedlistener.onBackPressed();
            super.onBackPressed();
        }
        else{
            super.onBackPressed();
        }
    }

}