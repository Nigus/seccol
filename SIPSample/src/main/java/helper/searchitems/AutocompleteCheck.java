package helper.searchitems;
/**
 * Created by Nigussie on 12.10.2015.
 */
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import com.portsip.R;

public class AutocompleteCheck extends Activity {

    private AutoCompleteTextView autoComplete;
    private MultiAutoCompleteTextView multiAutoComplete;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the defined string-array
        String[] colors = getResources().getStringArray(R.array.colorList);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,colors);

        autoComplete = (AutoCompleteTextView) findViewById(R.id.autoComplete);
        multiAutoComplete = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoComplete);
        // set adapter for the auto complete fields
        autoComplete.setAdapter(adapter);//
        multiAutoComplete.setAdapter(adapter);
        // specify the minimum type of characters before drop-down list is shown
        autoComplete.setThreshold(1);
        multiAutoComplete.setThreshold(2);
        // comma to separate the different colors
        multiAutoComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        // when the user clicks an item of the drop-down list
        multiAutoComplete.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Toast.makeText(getBaseContext(), "MultiAutoComplete: " +
                                "you add color "+arg0.getItemAtPosition(arg2),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}