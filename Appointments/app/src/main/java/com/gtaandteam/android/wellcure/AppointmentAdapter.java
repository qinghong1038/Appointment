package com.gtaandteam.android.wellcure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    public AppointmentAdapter(Context context, List<Appointment> appointments) {
        super(context, 0, appointments);
    }

    /**
     * Returns a list item view that displays information about the appointment at the given position
     * in the list of Appointments.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.past_appointment, parent, false);
        }
        Appointment currentAppointment = getItem(position);
        TextView Name = listItemView.findViewById(R.id.NameValue);
        TextView Date = listItemView.findViewById(R.id.DateValue);
        ImageView DoctorImage = listItemView.findViewById(R.id.DoctorImage);

        Name.setText(currentAppointment.getmDoctorName());
        Date.setText(currentAppointment.getmDate());
        if (currentAppointment.hasImage()) {
            // If an image is available, display the provided image based on the resource ID
            DoctorImage.setImageResource(currentAppointment.getmDoctorImage());
            // Make sure the view is visible
            DoctorImage.setVisibility(View.VISIBLE);
        } else {
            // Otherwise show generic image
            DoctorImage.setImageResource(R.drawable.headshot);
        }        return listItemView;
    }

}

