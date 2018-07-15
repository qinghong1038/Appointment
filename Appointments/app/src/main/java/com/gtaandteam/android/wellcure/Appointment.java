package com.gtaandteam.android.wellcure;

public class Appointment {

//This class will be used to store information regarding a patient's Appointments in a well structured format
    //TODO: Remove the temporary constructors when no longer needed

    public static final int NO_IMAGE_PROVIDED = -1;

    private String mPatientName, mDoctorName, mDate;
    private Float mFees;
    private int mDoctorImage = NO_IMAGE_PROVIDED;
    final String Dr = "Dr. ";



    //Temporary Constructor:
    public Appointment(String DoctorName, String Date, int DoctorImage){
        mDoctorName =  Dr + DoctorName;
        mDate = Date;
        mDoctorImage = DoctorImage;
    }


    //Temporary Constructor:
    public Appointment(String DoctorName, String Date){
        mDoctorName =  Dr +DoctorName;
        mDate = Date;

    }

    public Appointment(String PatientName,String DoctorName, String Date, Float Fees){
        mPatientName = PatientName;
        mDoctorName =  Dr +DoctorName;
        mDate = Date;
        mFees = Fees;

    }

    public Appointment(String PatientName,String DoctorName, String Date, Float Fees, int DoctorImage){
        mPatientName = PatientName;
        mDoctorName =  Dr +DoctorName;
        mDate = Date;
        mFees = Fees;
        mDoctorImage = DoctorImage;

    }

    public Float getmFees() {
        return mFees;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmDoctorName() {
        return mDoctorName;
    }

    public String getmPatientName() {
        return mPatientName;
    }

    public int getmDoctorImage() {
        return mDoctorImage;
    }

    public boolean hasImage() {
        return mDoctorImage != NO_IMAGE_PROVIDED;
    }

}


