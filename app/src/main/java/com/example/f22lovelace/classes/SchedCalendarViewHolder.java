package com.example.f22lovelace.classes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

public class SchedCalendarViewHolder extends RecyclerView.ViewHolder
{
    public final TextView dayOfMonth;
    public final ImageView weekCheckIcon;

    public SchedCalendarViewHolder(@NonNull View itemView, SchedCalendarAdapter.CalendarClickListener calendarClickListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        weekCheckIcon = itemView.findViewById(R.id.weekCheckIcon);

    }

}
