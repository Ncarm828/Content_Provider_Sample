package appsandmaps.temple.edu.content_provider;

//package com.example.harshu195.minicalendareventlist;

        import android.content.ContentResolver;
        import android.content.Context;
        import android.database.Cursor;
        import android.provider.CalendarContract;
        import java.util.Calendar;

public class CalendarEvent {
    Context context;
    String res="";

    /**Creating a projectin string that will fetch the following Event Details
     * Calendar ID
     * Event Tittle
     * Event Description
     * Event Start Time
     * Event End Time
     * Event Duration
     * And if the Event is an ALL DAY one.
     */

    String[] projection = new String[] {
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY};

    public CalendarEvent(Context context) {
        this.context = context;
    }

    public Boolean isActivitySlot( Long startTime, Long endTime) {

        /*
              * Select all the event that have the Event End Time Greater than the provided tartTime
              * AND
              * Select all the event that have the Start Time les than the provided endtime
         */

        String selection = "(( " + CalendarContract.Events.DTEND + " >= " + startTime + " ) AND" +
                " ( " + CalendarContract.Events.DTSTART + " <= " + endTime + " ))";


        Cursor cursor = this.context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);

        //Creating a Calendar object just to print the info in Human Readable format
        Calendar eventStartTime = Calendar.getInstance();
        Calendar eventEndTime = Calendar.getInstance();

        //Traverse through the cursor
        if (cursor.moveToFirst()) {
            do {
                //Setting the Calendar object
                eventStartTime.setTimeInMillis(cursor.getLong(3));
                eventEndTime.setTimeInMillis(cursor.getLong(4));

                //Assigning the result into a string to access in future for display
                res = ("\n" + "Title: " + " " + cursor.getString(1) + "\n"
                        + "\n" + " Start-Time: " + " " + eventStartTime.get(Calendar.HOUR_OF_DAY) + ":" + eventStartTime.get(Calendar.MINUTE) + "\n"
                        + "\n" + " End-Time: " + " " + eventEndTime.get(Calendar.HOUR_OF_DAY) + ":" + eventEndTime.get(Calendar.MINUTE) + "\n"
                        + "\n");
            } while (cursor.moveToNext());
            cursor.close();
            return true;
        } else {
            //Setting the Calendar object
            eventEndTime.setTimeInMillis(endTime);
            eventStartTime.setTimeInMillis(startTime);
            //Assigning the result into a string to access in future for display
            res = "Start-Time:" + " " + eventStartTime.get(Calendar.HOUR_OF_DAY) + ":" + eventStartTime.get(Calendar.MINUTE) + "\n" +
                    "End-Time:" + " " + eventEndTime.get(Calendar.HOUR_OF_DAY) + ":" + eventEndTime.get(Calendar.MINUTE) + "\n";
            return false;
        }
    }

    public Boolean isSlotAvailable(){

        //Setting the start time to check for all the events to current system time.
        Long currSlotStartTime =(System.currentTimeMillis()) ;

        //Setting the end time to 1 hours from current time
        Long currSlotEndTime =(System.currentTimeMillis() +1*60*60*1000 ) ;

        //Call to check the if this is an Activity or a Free slot
        if(isActivitySlot(currSlotStartTime,currSlotEndTime)){
            return false;
        }
        else {
            return true;
        }
    }

    // checking the next Slot
    public Boolean isNextSlotAvailable(){

        //starts 1 hr form the current time and End in 2 hrs from current time.
        Long nxtSlotStartTime = System.currentTimeMillis() + 1*60*60*1000 ;
        Long nxtSlotEndTime = System.currentTimeMillis() + 2*60*60*1000;

        //Call to check the if this is an Activity or a Free slot
        if(isActivitySlot(nxtSlotStartTime,nxtSlotEndTime)){
            return false;
        }
        else {
            return true;
        }
    }
}
