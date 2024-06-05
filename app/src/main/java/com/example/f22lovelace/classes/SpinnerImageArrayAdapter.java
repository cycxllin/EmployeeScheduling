package com.example.f22lovelace.classes;

import static android.view.View.VISIBLE;

import static com.example.f22lovelace.R.drawable.ic_menu_sun;
import static com.example.f22lovelace.R.drawable.ic_menu_moon;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class SpinnerImageArrayAdapter extends ArrayAdapter<Employee> {
    private ArrayList<Employee> availEmp;
    private Context context;

    public SpinnerImageArrayAdapter(Context context, int resource,
                                    ArrayList<Employee> availEmp) {
        super(context, R.layout.spinner_layout, R.id.employee, availEmp);
        this.availEmp = availEmp;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getImageForPosition(position, convertView, parent);
    }

    private View getImageForPosition(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_layout, parent, false);
        //View row = LayoutInflater.from(context).inflate(R.layout.spinner_layout, parent, false);

        Employee emp = availEmp.get(position);
        TextView name = (TextView) row.findViewById(R.id.employee);
        if (emp.getID() != -1){
            name.setText("ID:" + emp.getID() + " " + emp.getFullName());
        }else{//Choose employee shown
            name.setText(emp.getFullName());
        }

        if (emp.getOpen() == 1) {
            ImageView sun = (ImageView) row.findViewById(R.id.drop_open);
            sun.setImageResource(ic_menu_sun);
        }
        if (emp.getClose() == 1){
            ImageView moon = (ImageView) row.findViewById(R.id.drop_close);
            moon.setImageResource(ic_menu_moon);
        }
        return row;
    }
}
