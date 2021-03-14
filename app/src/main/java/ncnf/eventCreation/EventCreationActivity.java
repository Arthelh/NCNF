package ncnf.eventCreation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import java.util.Calendar;
import java.util.Date;

public class EventCreationActivity extends AppCompatActivity {

    private EditText eventName;
    private EditText eventDescription;
    private EditText eventEmailContact;
    private EditText eventAddress;
    private Date eventDate;
    private int selYear, selMonth, selDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        Button validateCreation = (Button) findViewById(R.id.event_create_button);

        Button selectDate = findViewById(R.id.event_date_button);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                selYear = calendar.get(Calendar.YEAR);
                selMonth = calendar.get(Calendar.MONTH);
                selDay = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selYear = year;
                        selMonth = month;
                        selDay = dayOfMonth;
                    }
                }, selYear, selMonth, selDay);
                datePickerDialog.show();


            }
        });
    }

}
