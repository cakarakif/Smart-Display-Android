package com.example.smartdisplay.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartdisplay.R;
import com.example.smartdisplay.ReminderAlarm.AddReminder;

public class StatisticsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        AddReminder ad=new AddReminder(root);
        ad.onTimeSet(16,55,1);
        ad.onTimeSet(16,56,2);

        return root;
    }
}