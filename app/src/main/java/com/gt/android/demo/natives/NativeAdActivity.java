package com.gt.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.IdRes;

import com.gt.android.demo.Constants;
import com.gt.android.demo.R;

public class NativeAdActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private int curPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.native_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        bindButton(R.id.unified_native_ad_button, NativeAdUnifiedActivity.class);
        bindButton(R.id.unified_native_ad_list_button, NativeAdUnifiedListActivity.class);
        bindButton(R.id.unified_native_ad_recycle_button, NativeAdUnifiedRecycleActivity.class);
        bindButton(R.id.unified_native_ad_draw_button, NativeAdDrawActivity.class);
    }

    private void bindButton(@IdRes int id, final Class clz) {
        this.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placementId;
                if (clz == NativeAdDrawActivity.class) {
                    String[] stringArray = getResources().getStringArray(R.array.native_draw_id_value);
                    placementId = stringArray[curPosition];
                } else {
                    String[] stringArray = getResources().getStringArray(R.array.native_id_value);
                    placementId = stringArray[curPosition];
                }
                Intent intent = new Intent(NativeAdActivity.this, clz);
                intent.putExtra("placementId", placementId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        curPosition = position;
        Log.d(Constants.TAG, "------onItemSelected------" + curPosition);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(Constants.TAG, "------onNothingSelected------");
    }
}