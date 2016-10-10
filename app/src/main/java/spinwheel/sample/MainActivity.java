package spinwheel.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spinwheel.SpinWheelListener;
import com.spinwheel.WheelView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WheelView wheelView = (WheelView) findViewById(R.id.wheel_view);
        wheelView.setSpinWheelListener(new SpinWheelListener() {
            @Override
            public void onRotationStarted() {

            }

            @Override
            public void onRotationEnd(int portionInArray, String result) {

            }
        });

        String[] possibleOutcomes = new String[]{
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H"
        };
        wheelView.setPossibleOutcomeName(possibleOutcomes);
    }
}
