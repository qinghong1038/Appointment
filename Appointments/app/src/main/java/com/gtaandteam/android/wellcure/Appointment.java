package com.gtaandteam.android.wellcure;
import android.text.format.Time;
public class Appointment {

//This class will be used to store information regarding a patient's Appointments in a well structured format
    //TODO: Remove the temporary constructors when no longer needed

    public static final int NO_IMAGE_PROVIDED = -1;

    private String mPatientName, mDoctorName, mDate;
    private Float mFees;
    private int mDoctorImage = NO_IMAGE_PROVIDED;
    private String mBookedOn;
    private String mPatientMobile,mPatientEmail;


    public Appointment(){

    }
    //Temporary Constructor:
    public Appointment(String DoctorName, String Date, String BookedOn, int DoctorImage){
        mBookedOn = BookedOn;
        mDoctorName =  "Dr. " + DoctorName;
        mDate = Date;
        mDoctorImage = DoctorImage;
    }


    //Temporary Constructor:
    public Appointment(String DoctorName, String Date,String BookedOn){
        mBookedOn = BookedOn;
        mDoctorName =  "Dr. " +DoctorName;
        mDate = Date;

    }

    public Appointment(String PatientName,String DoctorName, String Date,String BookedOn, Float Fees,String PatientMob,String PatientEmail){

        mBookedOn = BookedOn;
        mPatientName = PatientName;
        mDoctorName =  "Dr. " +DoctorName;
        mDate = Date;
        mFees = Fees;
        if(DoctorName.equals("Ritu Jain"))
            mDoctorImage = R.drawable.doctor;
        mPatientEmail=PatientEmail;
        mPatientMobile=PatientMob;

    }

    public Appointment(String PatientName,String DoctorName, String Date,String BookedOn, Float Fees, int DoctorImage){
        mPatientName = PatientName;
        mDoctorName =  "Dr. " +DoctorName;
        mBookedOn = BookedOn;
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

    public String getmBookedOn() {
        return mBookedOn;
    }

    public String getmPatientMobile() {
        return mPatientMobile;
    }

    public String getmPatientEmail() {
        return mPatientEmail;
    }
}
