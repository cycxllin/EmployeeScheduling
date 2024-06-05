package com.example.f22lovelace.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    Context context;
    private final ArrayList<String> daysOfMonth;
    private final CalendarClickListener calendarClickListener;
    private final ArrayList<Integer> daysAvail;
    private final ArrayList<Integer> mornsAvail;
    private final ArrayList<Integer> evesAvail;
    private final ArrayList<Integer> notAvail;
    private int mode;

    public CalendarAdapter(Context context, ArrayList<String> daysOfMonth,
                           CalendarClickListener calendarClickListener, ArrayList<Integer> daysAvail,
                           ArrayList<Integer> mornsAvail, ArrayList<Integer> evesAvail, ArrayList<Integer> notAvail,
                           int mode)
    {
        this.daysAvail = daysAvail;
        this.mornsAvail = mornsAvail;
        this.evesAvail = evesAvail;
        this.notAvail = notAvail;
        this.context = context;
        this.daysOfMonth = daysOfMonth;
        this.calendarClickListener = calendarClickListener;
        this.mode = mode;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.12);
        return new CalendarViewHolder(view, calendarClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        int pos = holder.getBindingAdapterPosition();
        String dayStr = daysOfMonth.get(pos);

        holder.dayOfMonth.setText(dayStr);

        if (!dayStr.isEmpty()){
            int dayInt = Integer.parseInt(dayStr);

            //is it on any list?
            if (daysAvail != null && daysAvail.contains(dayInt)){mode = 1;}
            else if (mornsAvail != null && mornsAvail.contains(dayInt)){mode = 2;}
            else if (evesAvail != null && evesAvail.contains(dayInt)){mode = 3;}
            else if (notAvail != null && notAvail.contains(dayInt)){mode = 0;}

            //is it a weekend day?

            switch (mode){
                case 1:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.madang));
                    break;
                case 2:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.chardonnay));
                    break;
                case 3:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.aero));
                    break;
                case 0:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        }


        holder.dayOfMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarClickListener.onItemClick(pos, dayStr, view);
            }
        });

    }

    @Override
    public int getItemCount() {return daysOfMonth.size();}

    public interface CalendarClickListener
    {
        void previousMonthAction();

        void onItemClick(int position, String dayText, View view);
    }
}
