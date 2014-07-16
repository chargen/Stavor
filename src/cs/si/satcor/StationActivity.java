package cs.si.satcor;

import java.io.IOException;

import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import cs.si.satcor.R;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.database.SerializationUtil;
import cs.si.stavor.database.MissionReaderContract.MissionEntry;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import cs.si.stavor.mission.Mission;
import cs.si.stavor.mission.MissionAndId;
import cs.si.stavor.station.GroundStation;
import cs.si.stavor.station.StationAndId;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Activity to edit or create the stations of the database
 * @author Xavier Gibert
 *
 */
public class StationActivity extends Activity{

	boolean isEdit = false;
	EditText tx_name, tx_lat, tx_lon, tx_elev, tx_bw;
	Button button;
	StationAndId station;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.station_editor);
		
		//Load mission, in case of edit mode, and the mode flag
		Bundle b = this.getIntent().getExtras();
		if(b!=null){
		    station = (StationAndId) b.getSerializable("STATION");
		    isEdit = true;
		}else{
			station = new StationAndId(new GroundStation(), -1);
			isEdit=false;
		}
		
		button = (Button) findViewById(R.id.buttonStationSave);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(tx_name.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(), getString(R.string.station_name_is_mandatory), Toast.LENGTH_LONG).show();
				}else{
					//Save Station
					try{
						station.station.name = tx_name.getText().toString();
						
						station.station.latitude = Double.parseDouble(tx_lat.getText().toString());
						station.station.longitude = Double.parseDouble(tx_lon.getText().toString());
						station.station.ellipsoid_elevation = Double.parseDouble(tx_elev.getText().toString());
						station.station.beam_width = Double.parseDouble(tx_bw.getText().toString());
						
						if(isEdit){
							//Update register with new name and serialized
							if(editStation()){
								//((StavorApplication)getApplication()).loader.reset();
								finish();
							}else{
								Toast.makeText(getApplicationContext(), getString(R.string.station_error_edit), Toast.LENGTH_LONG).show();
							}
						}else{
							//Create new register in db
							if(addStation()){
								//((StavorApplication)getApplication()).loader.reset();
								finish();
							}else{
								Toast.makeText(getApplicationContext(), getString(R.string.station_error_create), Toast.LENGTH_LONG).show();
							}
						}
						
						
					}catch(Exception e){
						Toast.makeText(getApplicationContext(), getString(R.string.station_format_error), Toast.LENGTH_LONG).show();
					}
				}
			}
    		
    	});
		
		//Load Views
		tx_name = (EditText) findViewById(R.id.editTextStationName);
		tx_name.requestFocus();
		
		tx_lat = (EditText) findViewById(R.id.editTextStationLat);
		tx_lon = (EditText) findViewById(R.id.editTextStationLon);
		tx_elev = (EditText) findViewById(R.id.editTextStationElev);
		tx_bw = (EditText) findViewById(R.id.editTextStationBw);
		

		//Fill Views
		tx_lat.setText(Double.toString(station.station.latitude));
		tx_lon.setText(Double.toString(station.station.longitude));
		tx_elev.setText(Double.toString(station.station.ellipsoid_elevation));
		tx_bw.setText(Double.toString(station.station.beam_width));

		if(isEdit){
			button.setText(getString(R.string.station_edit));
			
			tx_name.setText(station.station.name);
			
		}else{
			button.setText(getString(R.string.station_create));
		}
		
	}
	
	private boolean editStation(){
		ContentValues values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, station.station.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, station.station.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, station.station.longitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, station.station.ellipsoid_elevation);
		values.put(StationEntry.COLUMN_NAME_BEAMWIDTH, station.station.beam_width);
		values.put(StationEntry.COLUMN_NAME_ENABLED, station.station.enabled);
		
		// Edit the row
		((StavorApplication)getApplication()).loader.update(
				StationEntry.TABLE_NAME,
				values,
				"_id "+"="+station.id, 
				null);
		return true;
	}
	
	private boolean addStation(){
		ContentValues values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, station.station.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, station.station.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, station.station.longitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, station.station.ellipsoid_elevation);
		values.put(StationEntry.COLUMN_NAME_BEAMWIDTH, station.station.beam_width);
		values.put(StationEntry.COLUMN_NAME_ENABLED, station.station.enabled);
		
		// Insert the new row
		((StavorApplication)getApplication()).loader.insert(
				StationEntry.TABLE_NAME,
				null,
		         values);
		return true;
		
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }


}