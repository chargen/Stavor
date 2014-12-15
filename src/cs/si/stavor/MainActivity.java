package cs.si.stavor;

import java.util.ArrayList;

//import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import cs.si.stavor.R;
import cs.si.stavor.app.Installer;
import cs.si.stavor.app.OrekitInit;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.database.ReaderDbHelper;
import cs.si.stavor.database.UserMission;
import cs.si.stavor.dialogs.ErrorDialogFragment;
import cs.si.stavor.dialogs.TutorialDialogFragment;
import cs.si.stavor.dialogs.WelcomeDialogFragment;
import cs.si.stavor.fragments.HudFragment;
import cs.si.stavor.fragments.MapFragment;
import cs.si.stavor.fragments.NavigationDrawerFragment;
import cs.si.stavor.fragments.OrbitFragment;
import cs.si.stavor.fragments.RetainedFragment;
import cs.si.stavor.fragments.SimulatorFragment;
import cs.si.stavor.fragments.StationsFragment;
import cs.si.stavor.fragments.TestFragment;
import cs.si.stavor.mission.MissionAndId;
import cs.si.stavor.settings.SettingsBasicFragment;
import cs.si.stavor.settings.SettingsCoverageFragment;
import cs.si.stavor.settings.SettingsExtraFragment;
import cs.si.stavor.settings.SettingsGeneralFragment;
import cs.si.stavor.settings.SettingsGeneralMapFragment;
import cs.si.stavor.settings.SettingsMeasuresFragment;
import cs.si.stavor.settings.SettingsModelsFragment;
import cs.si.stavor.settings.SettingsOrbitFragment;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.station.StationAndId;
import cs.si.stavor.web.MyResourceClient;
import cs.si.stavor.web.MyUIClient;
import cs.si.stavor.web.WebAppInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Main activity of the application, managing persistent objects and fragments
 * @author Xavier Gibert
 *
 */
public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	/**
	 * Used to store information during application restart due to configuration
	 * changes
	 */
	private RetainedFragment dataFragment;

	/**
	 * Simulator object
	 */
	private Simulator simulator;
	
	/**
	 * Returns the simulator object
	 * @return
	 */
	public Simulator getSimulator(){
		return simulator;
	}
	
	/**
	 * Browser object
	 */
	private XWalkView mXwalkView;
	private XWalkView mXwalkViewOrbit;
	private boolean loadBrowser;
	private boolean loadBrowserOrbit;
	/**
	 * Returns the simulator object
	 * @return
	 */
	public XWalkView getBrowser(){
		return mXwalkView;
	}
	public XWalkView getBrowserOrbit(){
		return mXwalkViewOrbit;
	}
	public boolean getLoadBrowserFlag(){
		return loadBrowser;
	}
	public void resetLoadBrowserFlag(){
		loadBrowser = false;
	}
	public void raiseLoadBrowserFlag(){
		loadBrowser = true;
	}
	public boolean getLoadBrowserFlagOrbit(){
		return loadBrowserOrbit;
	}
	public void resetLoadBrowserFlagOrbit(){
		loadBrowserOrbit = false;
	}
	public void raiseLoadBrowserFlagOrbit(){
		loadBrowserOrbit = true;
	}
	// to know when the oncreate and onresume are triggered for the first time
	private boolean flagActivityFirstExec = false;
	
    public boolean flag_show_welcome = false;
    
    private ProgressBar browserProgressBar = null;
    private FrameLayout browserProgressLayout = null;
    public void setBrowserProgressBar(ProgressBar bar, FrameLayout fr){
    	browserProgressLayout = fr;
    	browserProgressBar = bar;
    }
    public void resetBrowserProgressBar(){
    	browserProgressLayout = null;
    	browserProgressBar = null;
    }
    public void setBrowserProgressValue(int progr){
    	if(browserProgressLayout!=null && browserProgressBar!=null){
	    	browserProgressBar.setProgress(progr);
	    	if(progr<10000){
	    		browserProgressLayout.setVisibility(View.VISIBLE);
	    	}else{
	    		Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
                fadeOut.setDuration(1500);
                fadeOut.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {}
                    public void onAnimationRepeat(Animation animation) {}
                    public void onAnimationEnd(Animation animation) {
                    	browserProgressLayout.setVisibility(View.GONE);
                    }
                });

                browserProgressLayout.startAnimation(fadeOut);
	    		
	    	}
    	}
    }
    
    
    private ProgressBar browserProgressBarOrbit = null;
    private FrameLayout browserProgressLayoutOrbit = null;
    public void setBrowserProgressBarOrbit(ProgressBar bar, FrameLayout fr){
    	browserProgressLayoutOrbit = fr;
    	browserProgressBarOrbit = bar;
    }
    public void resetBrowserProgressBarOrbit(){
    	browserProgressLayoutOrbit = null;
    	browserProgressBarOrbit = null;
    }
    public void setBrowserProgressValueOrbit(int progr){
    	if(browserProgressLayoutOrbit!=null && browserProgressBarOrbit!=null){
	    	browserProgressBarOrbit.setProgress(progr);
	    	if(progr<10000){
	    		browserProgressLayoutOrbit.setVisibility(View.VISIBLE);
	    	}else{
	    		Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
                fadeOut.setDuration(1500);
                fadeOut.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {}
                    public void onAnimationRepeat(Animation animation) {}
                    public void onAnimationEnd(Animation animation) {
                    	browserProgressLayoutOrbit.setVisibility(View.GONE);
                    }
                });

                browserProgressLayoutOrbit.startAnimation(fadeOut);
	    		
	    	}
    	}
    }

    private ProgressBar browserProgressBarMap = null;
    private FrameLayout browserProgressLayoutMap = null;
    public void setBrowserProgressBarMap(ProgressBar bar, FrameLayout fr){
    	browserProgressLayoutMap = fr;
    	browserProgressBarMap = bar;
    }
    public void resetBrowserProgressBarMap(){
    	browserProgressLayoutMap = null;
    	browserProgressBarMap = null;
    }
    public void setBrowserProgressValueMap(int progr){
    	if(browserProgressLayoutMap!=null && browserProgressBarMap!=null){
	    	browserProgressBarMap.setProgress(progr);
	    	if(progr<10000){
	    		browserProgressLayoutMap.setVisibility(View.VISIBLE);
	    	}else{
	    		Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
                fadeOut.setDuration(1500);
                fadeOut.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {}
                    public void onAnimationRepeat(Animation animation) {}
                    public void onAnimationEnd(Animation animation) {
                    	browserProgressLayoutMap.setVisibility(View.GONE);
                    }
                });

                browserProgressLayoutMap.startAnimation(fadeOut);
	    		
	    	}
    	}
    }
    public ArrayList<UserMission> userMissions;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long start_time = System.nanoTime();
		
		//Install Orekit default files if not installed yet
		userMissions = new ArrayList<UserMission>();
		Installer.installApkData(this);
		
		//Initialize Orekit with the data files
		OrekitInit.init(Installer.getOrekitDataRoot(this));
		
		//Configure application window
		//requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		//setProgressBarVisibility(true);
		
		// find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
        	flagActivityFirstExec=true;
        	((StavorApplication)getApplication()).modelViewId = R.id.menu_views_ref_frame_xyz;
        	((StavorApplication)getApplication()).modelOrbitViewId = R.id.menu_orbviews_free;
			((StavorApplication)getApplication()).follow_sc = R.id.menu_mapviews_free;
        	((StavorApplication)getApplication()).zoom = 1;
        	((StavorApplication)getApplication()).lon = (float)0.0;
        	((StavorApplication)getApplication()).lat = (float)0.0;
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            
            Simulator simu = new Simulator(this);
            
            XWalkView xwalkView = new XWalkView(this.getApplicationContext(), this);
			//mXwalkView.setBackgroundResource(R.color.black);
			xwalkView.setBackgroundColor(0x00000000);
			xwalkView.setResourceClient(new MyResourceClient(xwalkView));
	        xwalkView.setUIClient(new MyUIClient(xwalkView));
	        xwalkView.clearCache(true);
	        
	        /*XWalkSettings browserSettings = xwalkView.getSettings();
	    	
	    	browserSettings.setJavaScriptEnabled(true);
	    	browserSettings.setUseWideViewPort(false);
	    	//browserSettings.setLoadWithOverviewMode(true);
	    	browserSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
	    	browserSettings.setAllowUniversalAccessFromFileURLs(true);
	    	//browserSettings.setBuiltInZoomControls(true);
	    	//browserSettings.setDisplayZoomControls(true);
	    	//browserSettings.setSupportZoom(true);, OnMenuItemClickListener*/
	    	
	    	//Orbit browser
	    	XWalkView xwalkViewOrbit = new XWalkView(this.getApplicationContext(), this);
			//mXwalkView.setBackgroundResource(R.color.black);
			xwalkViewOrbit.setBackgroundColor(0x00000000);
			xwalkViewOrbit.setResourceClient(new MyResourceClient(xwalkViewOrbit));
	        xwalkViewOrbit.setUIClient(new MyUIClient(xwalkViewOrbit));
	        xwalkViewOrbit.clearCache(true);
	        
	        /*XWalkSettings browserSettingsOrbit = xwalkViewOrbit.getSettings();
	    	
	    	browserSettingsOrbit.setJavaScriptEnabled(true);
	    	browserSettingsOrbit.setUseWideViewPort(false);
	    	//browserSettings.setLoadWithOverviewMode(true);
	    	browserSettingsOrbit.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
	    	browserSettingsOrbit.setAllowUniversalAccessFromFileURLs(true);
	    	//browserSettings.setBuiltInZoomControls(true);
	    	//browserSettings.setDisplayZoomControls(true);
	    	//browserSettings.setSupportZoom(true);, OnMenuItemClickListener*/
	    	
	    	((StavorApplication)getApplication()).jsInterface = new WebAppInterface(this, simu.getSimulationResults());
	    	xwalkView.addJavascriptInterface(((StavorApplication)getApplication()).jsInterface, "Android");
	    	xwalkViewOrbit.addJavascriptInterface(((StavorApplication)getApplication()).jsInterface, "Android");
	    	
            ReaderDbHelper db_help_tmp;
            SQLiteDatabase db_tmp;
            db_help_tmp = Installer.installApkDatabase(this);
            db_tmp = db_help_tmp.getWritableDatabase();
            
            dataFragment.setData(
            		xwalkView,
            		true,
            		xwalkViewOrbit,
            		true,
            		simu,
            		db_help_tmp,
            		db_tmp,
            		Parameters.Hud.start_panel_open
            		);
        }
        
        this.mXwalkView = dataFragment.getBrowser();
        this.mXwalkViewOrbit = dataFragment.getBrowserOrbit();
        this.loadBrowser = dataFragment.getLoadBrowser();
        this.loadBrowserOrbit = dataFragment.getLoadBrowserOrbit();
        
        // the data is available in dataFragment.getData()
        this.simulator = dataFragment.getSim();
        //this.simulator.reconstruct(this);
        
        //Update javascriptInterface
        ((StavorApplication)getApplication()).jsInterface.reconstruct(this, simulator.getSimulationResults());
        
		//Install the Missions database if not installed yet and store database objects
		((StavorApplication)getApplication()).db_help = dataFragment.getDbHelp();
		((StavorApplication)getApplication()).db = dataFragment.getDb();

		hud_panel_open = dataFragment.getHudPanelOpen();
		
		if(flagActivityFirstExec){
        	mXwalkView.load(Parameters.Web.STARTING_PAGE,null);
        	mXwalkViewOrbit.load(Parameters.Web.STARTING_PAGE_ORBIT,null);
        	flagActivityFirstExec=false;
        }
        
        // NAVIGATION
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		
		//Wait a minimum amount of time to display the splash screen
		long diff_time = System.nanoTime()-start_time;
		if(diff_time<Parameters.App.splash_min_time_ns){
			try {
				Thread.sleep((Parameters.App.splash_min_time_ns-diff_time)/1000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		//Reset flag to not start playing when the browser has to reload.
		if(simulator!=null)
			simulator.resetTemporaryPause();
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		if(position==0){// selection of tabs content
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					SimulatorFragment.newInstance(position + 1)).commit();
		}else if(position==1){
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					HudFragment.newInstance(position + 1)).commit();
		}else if(position==2){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsBasicFragment.newInstance(position +1)).commit();
		}else if(position==3){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsExtraFragment.newInstance(position +1)).commit();
		}else if(position==4){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsMeasuresFragment.newInstance(position +1)).commit();
		}else if(position==5){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsModelsFragment.newInstance(position +1)).commit();
		}else if(position==6){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		OrbitFragment.newInstance(position +1)).commit();
		}else if(position==7){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsOrbitFragment.newInstance(position +1)).commit();
		}else if(position==8){
			// Display the fragment as the main content.
	        fragmentManager    
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsGeneralFragment.newInstance(position +1)).commit();
		}else if(position==9){
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					MapFragment.newInstance(position + 1)).commit();
		}else if(position==10){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsCoverageFragment.newInstance(position +1)).commit();
		}else if(position==11){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		StationsFragment.newInstance(position +1)).commit();
		}else if(position==12){
			// Display the fragment as the main content.
	        fragmentManager    
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsGeneralMapFragment.newInstance(position +1)).commit();
		}else if(position==13){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, TestFragment.newInstance(position + 1)).commit();
		}else{
			
		}
	}

	/**
	 * Updates the title of the section
	 */
	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		case 5:
			mTitle = getString(R.string.title_section5);
			break;
		case 6:
			mTitle = getString(R.string.title_section6);
			break;
		case 7:
			mTitle = getString(R.string.title_section7);
			break;
		case 8:
			mTitle = getString(R.string.title_section8);
			break;
		case 9:
			mTitle = getString(R.string.title_section9);
			break;
		case 10:
			mTitle = getString(R.string.title_section10);
			break;
		case 11:
			mTitle = getString(R.string.title_section11);
			break;
		case 12:
			mTitle = getString(R.string.title_section12);
			break;
		case 13:
			mTitle = getString(R.string.title_section13);
			break;
		case 14:
			mTitle = getString(R.string.title_section14);
			break;
		}
	}
    
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			showAbout();
			return true;
		}
		/*if (id == R.id.action_reset_conf) {
			resetUserConfigShowDialog();
			return true;
		}
		if (id == R.id.action_reset_db) {
			resetMissionsDbShowDialog();
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Start the About activity
	 */
	public void showAbout() {
		Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
		MainActivity.this.startActivity(myIntent);
	}
	
	/**
	 * Starts the Mission editor activity in create mode
	 */
	public void showMissionCreator() {
		Intent myIntent = new Intent(MainActivity.this, MissionActivity.class);
		MainActivity.this.startActivity(myIntent);
	}
	
	/**
	 * Starts the Mission editor activity in edit mode
	 */
	public void showMissionEditor(MissionAndId mission){
		Intent myIntent = new Intent(MainActivity.this, MissionActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("MISSION",mission);
		myIntent.putExtras(b);
		MainActivity.this.startActivity(myIntent);
	}
	
	/**
	 * Starts the Station editor activity in create mode
	 */
	public void showStationCreator() {
		Intent myIntent = new Intent(MainActivity.this, StationActivity.class);
		MainActivity.this.startActivity(myIntent);
	}
	
	/**
	 * Starts the Station editor activity in edit mode
	 */
	public void showStationEditor(StationAndId station){
		Intent myIntent = new Intent(MainActivity.this, StationActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("STATION",station);
		myIntent.putExtras(b);
		MainActivity.this.startActivity(myIntent);
	}

	/**
	 * GoTo specified section of the navigation menu
	 * @param sel
	 */
	public void showSection(final int sel) {
		runOnUiThread( new Runnable() {
			public void run() {    
				mNavigationDrawerFragment.select(sel);
				onSectionAttached(sel+1);
				restoreActionBar();
	        }
		});
	}
	
	/**
	 * GoTo previously selected section of the navigation menu
	 */
	public void showSection() {
		showSection(mNavigationDrawerFragment.getSelectedPosition());
	}
	
    private Toast toast;
    private long lastBackPressTime = 0;


    @Override
    public void onBackPressed() {
    	if (!mNavigationDrawerFragment.isDrawerOpen()) {//If the navigation menu is not opened
	    	int sel = mNavigationDrawerFragment.getSelectedPosition();
	    	if(sel==7){//If it is currently in a configuration section, goto Hud
	    		showSection(6);
	    	}else if (sel>1 && sel<6){//If it is in Hud, goto simulator screen
	    		showSection(1);
	    	}else if (sel==1 || sel==6 || sel==8){//If it is in Hud, goto simulator screen
	    		showSection(0);
	    	}else if (sel==0){//If it is in Simulator, warn user before exit
		    	if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {//Wait some time for confirmation
		    		toast = Toast.makeText(this, getString(R.string.app_exit_prevent_message), 4000);
		    		toast.show();
		    		this.lastBackPressTime = System.currentTimeMillis();
		    	} else {//Exit application
		    		if (toast != null) {
		    			toast.cancel();
		    		}
		    		super.onBackPressed();
		    	}
	    	}
    	}else{//If the navigation menu is open, close it
    		showSection();	
	    }
    }
    
    /**
     * Displays the Welcome dialog
     */
    public void showWelcomeMessage() {
        DialogFragment newFragment = new WelcomeDialogFragment();
        newFragment.show(getFragmentManager(), "welcome");
    }
    
    /**
     * Displays an error dialog
     * @param message
     * @param canIgnore
     */
    public void showErrorDialog(String message, boolean canIgnore) {
    	DialogFragment newFragment = ErrorDialogFragment.newInstance(message, canIgnore);
    	newFragment.setCancelable(false);
    	newFragment.show(getFragmentManager(), "error");
    }
    
    
    public void showTutorialSimulator(){
    	String key = getString(R.string.pref_key_tutorial_simulator);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_simulator);
	    	String message = getString(R.string.tutorial_message_simulator);
	    	showTutorialDialog(key, title, message);
    	}
    	if(flag_show_welcome){
    		flag_show_welcome=false;
    		showWelcomeMessage();
    	}
    }
    public void showTutorialDisplay(){
    	String key = getString(R.string.pref_key_tutorial_display);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_display);
	    	String message = getString(R.string.tutorial_message_display);
	    	showTutorialDialog(key, title, message);
    	}
    }
    public void showTutorialOrbit(){
    	String key = getString(R.string.pref_key_tutorial_orbit);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_orbit);
	    	String message = getString(R.string.tutorial_message_orbit);
	    	showTutorialDialog(key, title, message);
    	}
    }
    public void showTutorialConfig(){
    	String key = getString(R.string.pref_key_tutorial_config);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_config);
	    	String message = getString(R.string.tutorial_message_config);
	    	showTutorialDialog(key, title, message);
    	}
    }
	public void showTutorialMap(){
    	String key = getString(R.string.pref_key_tutorial_map);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_map);
	    	String message = getString(R.string.tutorial_message_map);
	    	showTutorialDialog(key, title, message);
    	}
    }
    public void showTutorialStations(){
    	String key = getString(R.string.pref_key_tutorial_stations);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_stations);
	    	String message = getString(R.string.tutorial_message_stations);
	    	showTutorialDialog(key, title, message);
    	}
    }
    public void showTutorialCoverage(){
    	String key = getString(R.string.pref_key_tutorial_coverage);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_coverage);
	    	String message = getString(R.string.tutorial_message_coverage);
	    	showTutorialDialog(key, title, message);
    	}
    }
    public void showTutorialConfigMap(){
    	String key = getString(R.string.pref_key_tutorial_config_map);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_tutorial)){
	    	String title = getString(R.string.tutorial_title_config_map);
	    	String message = getString(R.string.tutorial_message_config_map);
	    	showTutorialDialog(key, title, message);
    	}
    }
    private void showTutorialDialog(String key, String title, String message) {
    	DialogFragment newFragment = TutorialDialogFragment.newInstance(key, title, message);
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "tutorial-"+title);
    }
    
    /**
     * Displays a confirmation dialog for reseting application configuration
     */
    /*private void resetUserConfigShowDialog() {
    	DialogFragment newFragment = ResetAppDialogFragment.newInstance();
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "reset");
	}*/
    
    /**
     * Displays a confirmation dialog for reseting Missions database
     */
    /*private void resetMissionsDbShowDialog() {
    	DialogFragment newFragment = ResetDbDialogFragment.newInstance();
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "reset_db");
	}*/
    
    @Override
    protected void onPause() {//Pause simulator and browser
        super.onPause();
        /*if(simulator!=null){
        	simulator.pause();
        }*/
    }

    @Override
    protected void onResume() {//Resume browser
        super.onResume();
        this.simulator.reconstruct(this);
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (mXwalkView != null) {
            mXwalkView.onActivityResult(requestCode, resultCode, data);
        }
        if (mXwalkViewOrbit != null) {
            mXwalkViewOrbit.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
        if (mXwalkViewOrbit != null) {
            mXwalkViewOrbit.onNewIntent(intent);
        }
    	super.onNewIntent(intent);
    }
	
	@Override
    public void onDestroy() {//Disconnect simulator, close database and browser
        super.onDestroy();
        // store the data in the fragment
        if(isFinishing()){
        	simulator.disconnect();
            //((StavorApplication)getApplication()).db_help.close();
            mXwalkView.onDestroy();
            mXwalkViewOrbit.onDestroy();
        }else{
        	dataFragment.setData(
        			this.mXwalkView,
        			this.loadBrowser,
        			this.mXwalkViewOrbit,
        			this.loadBrowserOrbit,
        			this.simulator,
        			((StavorApplication)getApplication()).db_help,
        			((StavorApplication)getApplication()).db,
        			hud_panel_open
        			);
        	//Recycle background
            BitmapDrawable bd = (BitmapDrawable)getWindow().getDecorView().getBackground();
            Bitmap mBitmap = bd.getBitmap();
            if (mBitmap != null && !mBitmap.isRecycled()) {
            	getWindow().getDecorView().setBackgroundResource(0);
                bd.setCallback(null);
                mBitmap.recycle();
                mBitmap = null; 
            }
        }
    }

	private boolean hud_panel_open;
	public boolean getHudPanelOpen(){
		return hud_panel_open;
	}
	public void setHudPanelOpen(boolean open){
		hud_panel_open = open;
	}
}
