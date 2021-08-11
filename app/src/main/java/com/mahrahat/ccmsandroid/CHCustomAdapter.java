package com.mahrahat.ccmsandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


/**
 * Custom Adapter used to display complaints history.
 */

public class CHCustomAdapter extends ArrayAdapter<Complaint> {

    private final List<Complaint> complaints;
    private final Context context;

    public CHCustomAdapter(@NonNull Context context, List<Complaint> complaints) {
        super(context, R.layout.activity_complaints_history, complaints);
        this.context = context;
        this.complaints = complaints;
    }

    public View getView(int position, View convertView, ViewGroup vgParent) {
        Complaint complaint = getItem(position);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.box_complaint_details, vgParent, false);
            viewHolder.tvComplaintsId = convertView.findViewById(R.id.tv_complaint_details_id);
            viewHolder.tvComplaintsDescription = convertView.findViewById(R.id.tv_complaint_details_description);
            viewHolder.tvComplaintStatus = convertView.findViewById(R.id.tv_complaint_details_status);
            viewHolder.tvComplaintSubmitted = convertView.findViewById(R.id.tv_complaint_details_submitted);
            viewHolder.tvComplaintUpdated = convertView.findViewById(R.id.tv_complaint_details_updated);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvComplaintsId.setText("Complaint ID: " + complaint.getComplaintId());
        viewHolder.tvComplaintsDescription.setText("Description:\n" + complaint.getDescription());
        viewHolder.tvComplaintStatus.setText("Current status: " + complaint.getStatus());
        viewHolder.tvComplaintSubmitted.setText("Submitted at: " + complaint.getTimeSubmitted());
        viewHolder.tvComplaintUpdated.setText("Last updated: " + complaint.getTimeUpdated());
        return convertView;
    }

    private static class ViewHolder {
        TextView tvComplaintsId, tvComplaintsDescription, tvComplaintSubmitted, tvComplaintUpdated, tvComplaintStatus;
    }
}
