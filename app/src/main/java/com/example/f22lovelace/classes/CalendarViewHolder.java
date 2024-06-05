package com.example.f22lovelace.classes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.f22lovelace.R;

import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder
{
    public final TextView dayOfMonth;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.CalendarClickListener calendarClickListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);

    }

}
