package it.androidavanzato.rxautocomplete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;


public class MainActivity extends AppCompatActivity {

    private static final String[] STATES = {
            "Alabama",
            "Alaska",
            "American Samoa",
            "Arizona",
            "Arkansas",
            "California",
            "Colorado",
            "Connecticut",
            "Delaware",
            "District Of Columbia",
            "Federated States Of Micronesia",
            "Florida",
            "Georgia",
            "Guam",
            "Hawaii",
            "Idaho",
            "Illinois",
            "Indiana",
            "Iowa",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Maine",
            "Marshall Islands",
            "Maryland",
            "Massachusetts",
            "Michigan",
            "Minnesota",
            "Mississippi",
            "Missouri",
            "Montana",
            "Nebraska",
            "Nevada",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "New York",
            "North Carolina",
            "North Dakota",
            "Northern Mariana Islands",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Palau",
            "Pennsylvania",
            "Puerto Rico",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Vermont",
            "Virgin Islands",
            "Virginia",
            "Washington",
            "West Virginia",
            "Wisconsin",
            "Wyoming"
    };
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        ListView list = (ListView) findViewById(R.id.list);

        EditText search = (EditText) findViewById(R.id.search);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);

        list.setAdapter(adapter);

        Observable.switchOnNext(
                WidgetObservable.text(search)
                        .map(e -> e.text().toString().toLowerCase())
                        .map(this::filterStates)
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(this::updateAdapter, this::manageError);
    }

    private void manageError(Throwable throwable) {
        Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void updateAdapter(List<String> items) {
        adapter.setNotifyOnChange(false);
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }

    private Observable<List<String>> filterStates(String filter) {
        return Observable
                .from(STATES)
                .filter(state -> state.toLowerCase().startsWith(filter))
                .toList()
                .delay(strings -> Observable.timer(strings.size() * 300, TimeUnit.MILLISECONDS));
    }

}
