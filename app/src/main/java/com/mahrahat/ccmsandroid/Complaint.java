package com.mahrahat.ccmsandroid;


/**
 * Complaint class, can be used to insert and retrieve complaints into and from local database.
 */

public class Complaint {

    private String complaintId, description, category, status, timeSubmitted, timeUpdated;

    public Complaint() {
        complaintId = null;
        description = null;
        category = null;
        status = null;
        timeSubmitted = null;
        timeUpdated = null;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(String timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public String getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(String timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId='" + complaintId + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", timeSubmitted='" + timeSubmitted + '\'' +
                ", timeUpdated='" + timeUpdated + '\'' +
                '}';
    }
}
