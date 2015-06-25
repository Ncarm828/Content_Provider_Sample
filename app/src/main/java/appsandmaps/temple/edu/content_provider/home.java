package appsandmaps.temple.edu.content_provider;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;


public class home extends Activity {

    EditText etExp, etLevel;
    Button btnExp, btnLevel,btnNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //fetching Random Value form these variables

        Random r = new Random();
        final int level = r.nextInt(10 - 3) + 3;
        final int exp = r.nextInt(95-35) + 35;

        //setting the fragments to show these values
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ExperienceFragment expFragment = new ExperienceFragment(exp);
        fragmentTransaction.replace(R.id.fragmentExp, expFragment);
        fragmentTransaction.commit();


        //setting the fragments to show these values
        FragmentManager fragmentManagerLevel = getFragmentManager();
        FragmentTransaction fragmentTransactionLevel = fragmentManager.beginTransaction();
        LevelFragment levelFragment = new LevelFragment(level);
        fragmentTransactionLevel.replace(R.id.fragmenLevel, levelFragment);
        fragmentTransactionLevel.commit();


        //Calling Nick's mainActivity
        btnNick = (Button)findViewById(R.id.button2);
        btnNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(home.this, MainActivity.class);
                startActivity(i);
           }
       });
    }
}

