package spinwheel.sample;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spinwheel.SpinWheelListener;
import com.spinwheel.WheelView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WheelView wheelView = (WheelView) findViewById(R.id.wheel_view);
        wheelView.setSpinWheelListener(new SpinWheelListener() {
            @Override
            public void onRotationStarted() {

            }

            @Override
            public void onRotationEnd(String result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Your outcome is " + result + ".")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wheelView.reset();
                            }
                        })
                        .show();
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
