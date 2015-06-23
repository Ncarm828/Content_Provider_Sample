package appsandmaps.temple.edu.content_provider;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LevelFragment extends Fragment {
    TextView tv;
    ProgressBar pBar;
    int pStatus = 0;
    int endLevel =0 ;
    private Handler handler = new Handler();
    public LevelFragment() {
        // Required empty public constructor
    }
    public LevelFragment(Integer etLevel) {
        //Getting value from the function call and setting it as a End point
        endLevel = etLevel*10;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_level, container, false);
        tv = (TextView) v.findViewById(R.id.textView1);
        pBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        while (pStatus <= endLevel) {
            pBar.setProgress(pStatus);
            pBar.setSecondaryProgress(100);
            tv.setText(endLevel/10 + "/" +"10");
            pStatus++;
        }
        return v;
    }
}