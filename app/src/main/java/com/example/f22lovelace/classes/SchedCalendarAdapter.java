package com.example.f22lovelace.classes;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class SchedCalendarAdapter extends RecyclerView.Adapter<SchedCalendarViewHolder>
{
    Context context;
    private final ArrayList<String> daysOfMonth;
    private final CalendarClickListener calendarClickListener;
    private final IconClickListener iconClickListener;
    private final ArrayList<Integer> done;
    private final ArrayList<Integer> inProgress;
    private final ArrayList<String> saturdaysInMonth;

    public SchedCalendarAdapter(Context context, ArrayList<String> daysOfMonth,
                                CalendarClickListener calendarClickListener, IconClickListener iconClickListener,
                                ArrayList<Integer> done, ArrayList<Integer> inProgress,
                                ArrayList<String> saturdaysInMonth)
    {
        this.done = done;
        this.inProgress = inProgress;
        this.context = context;
        this.daysOfMonth = daysOfMonth;
        this.saturdaysInMonth = saturdaysInMonth;
        this.calendarClickListener = calendarClickListener;
        this.iconClickListener = iconClickListener;
    }


    @NonNull
    @Override
    public SchedCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.16);
        return new SchedCalendarViewHolder(view, calendarClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedCalendarViewHolder holder, int position)
    {
        int mode;
        int pos = holder.getBindingAdapterPosition();
        String dayStr = daysOfMonth.get(pos);
        String sat = saturdaysInMonth.get(pos);

        holder.dayOfMonth.setText(dayStr);
        ImageView weekCheckIcon = holder.weekCheckIcon;

        if (!dayStr.isEmpty()){
            int dayInt = Integer.parseInt(dayStr);

            //is it on any list?
            if (done != null && done.contains(dayInt)){mode = 1;}
            else if (inProgress != null && inProgress.contains(dayInt)){mode = 2;}
            else {mode = 0;}

            switch (mode){
                case 1:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.madang));
                    break;
                case 2:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.chardonnay));
                    break;
                case 0:
                    holder.dayOfMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            //Show icon if saturday is in list, currently shows on all saturdays
            if (dayStr == sat){
                weekCheckIcon.setVisibility(VISIBLE);
            }
        }


        holder.dayOfMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarClickListener.onItemClick(pos, dayStr, view);
            }
        });

        holder.weekCheckIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { iconClickListener.onItemClick(dayStr, view);}
        });

    }

    @Override
    public int getItemCount() {return daysOfMonth.size();}

    public interface CalendarClickListener
    {
        void onItemClick(String dayTxt, View view);

        void onItemClick(int position, String dayText, View view);
    }

    public interface IconClickListener{
        void onItemClick(String dayStr, View view);
    }
}
