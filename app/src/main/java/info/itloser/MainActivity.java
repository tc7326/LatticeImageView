package info.itloser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    LatticeImageView latticeImageView;

    EditText etColumns, etLines;

    Button btnSave, btnArray;

    CheckBox cbSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    public void initView() {
        latticeImageView = findViewById(R.id.liv_test);

        etColumns = findViewById(R.id.et_columns);
        etLines = findViewById(R.id.et_lines);

        btnSave = findViewById(R.id.btn_save);
        btnArray = findViewById(R.id.btn_array);

        cbSelectAll = findViewById(R.id.cb_select_all);

    }

    public void initEvent() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latticeImageView.setNumberOfLinesAndColumns(Integer.valueOf(etLines.getText().toString()), Integer.valueOf(etColumns.getText().toString()));
            }
        });

        btnArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latticeImageView.selectByArray(new int[]{0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1});
            }
        });

        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                latticeImageView.selectAll(isChecked);
            }
        });

    }

}
