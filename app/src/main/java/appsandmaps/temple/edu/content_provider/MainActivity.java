package appsandmaps.temple.edu.content_provider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


//public class MainActivity extends ActionBarActivity {

    public class MainActivity extends Activity implements View.OnClickListener {


        private final static String TAG = "CustomContentProvider";
        EditText title, content, displaytext;
        Button add, update, showNotes;
        TextView textViews;
        //UpdateActivityClass MyAsTk = new UpdateActivityClass();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

       //     title = (EditText) findViewById(R.id.title);
       ////     content = (EditText) findViewById(R.id.content);
           // displaytext = (EditText) findViewById(R.id.users);

            // add Click Listners
          //  add = (Button) findViewById(R.id.button_add);
          //  add.setOnClickListener(this);
          //  update = (Button) findViewById(R.id.button_update);
          //  update.setOnClickListener(this);
         //   showNotes = (Button) findViewById(R.id.show_notes);
         //   showNotes.setOnClickListener(this);

            //  MyAsTk.delegate = this;

            getStepInformation();

        }


       /* void addNote() {
            if (title.getText().toString().length() > 0
                    && content.getText().toString().length() > 0) {
                ContentValues values = new ContentValues();
                values.put(ContractClass.NotesTable.TITLE, title.getText().toString());
                values.put(ContractClass.NotesTable.CONTENT, content.getText().toString());
                getContentResolver().insert(ContractClass.CONTENT_URI, values);
                Log.i(TAG, "Inserted");
                makeToast("Note Added");
            } else {
                makeToast("Empty Field");
            }
        }

        void updateNote(String str_id) {
            try {
                int id = Integer.parseInt(str_id);
                Log.i(TAG, "Updating with id = " + id);
                ContentValues values = new ContentValues();
                values.put(ContractClass.NotesTable.TITLE, title.getText().toString());
                values.put(ContractClass.NotesTable.CONTENT, content.getText().toString());
                getContentResolver().update(ContractClass.CONTENT_URI, values,
                        ContractClass.NotesTable.ID + " = " + id, null);
                makeToast("Note Updated");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        void getStepInformation() {
            Cursor cur = getContentResolver().query(ContractClass.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                Log.i(TAG, "Showing values.....");
                while (cur.moveToNext()) {
                    String Id = cur.getString(cur.getColumnIndex(ContractClass.NotesTable.ID));
                    String title = cur.getString(cur.getColumnIndex(ContractClass.NotesTable.TITLE));
                    String Steps = cur.getString(cur.getColumnIndex(ContractClass.NotesTable.CONTENT));
                    System.out.println("Id = " + Id + ", Note Title : " + title + ", Steps :" + Steps);
                    textViews =(TextView) findViewById(R.id.textView);
                    textViews.setText(title);
                    circlebar(Steps);

                }
             //   makeToast("Check the LogCat for Notes");


            } else {
                Log.i(TAG, "No Notes added");
                makeToast("No Notes added");
            }


        }


        @Override
        public void onClick(View arg0) {
            if (arg0 == add) {
                //addNote();
            }
            if (arg0 == showNotes) {
                // show all
                getStepInformation();
            }
        }

        private void makeToast(String text) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }





void circlebar(final String steps){

        final TextView tv;
        final ProgressBar pBar;
    final int[] pStatus = {0};
        final Handler handler = new Handler();

            tv = (TextView) findViewById(R.id.textView1);
            pBar = (ProgressBar) findViewById(R.id.progressBar1);

        final int percent = ((Integer.parseInt(steps)*100)/50000);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (pStatus[0] <= percent) {

                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                pBar.setProgress(pStatus[0]);
                                pBar.setSecondaryProgress(pStatus[0] + 3);
                                tv.setText(steps + "/" + 50000);
                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            // Just to display the progress slowly
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus[0]++;
                    }
                }
            }).start();
        }

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    /**
     * A placeholder fragment containing a simple view.
     */
/*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            return rootView;
        }
    }
*/

}






