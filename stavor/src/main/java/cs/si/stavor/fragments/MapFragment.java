package cs.si.stavor.fragments;


import org.xwalk.core.XWalkView;

import cs.si.stavor.MainActivity;
import cs.si.stavor.R;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.model.Browsers;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.web.MyResourceClient;
import cs.si.stavor.web.MyUIClient;
import cs.si.stavor.web.WebAppInterface;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

/**
 * Fragment with the visualization browser for the map
 * @author Xavier Gibert
 *
 */

@SuppressWarnings("deprecation")
public final class MapFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private static String screenName = "Map";
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 */
	public static MapFragment newInstance(int sectionNumber) {	
		MapFragment fragment = new MapFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public MapFragment() {
	}
	
	private Simulator simulator;
	LinearLayout browserLayout, slider_content;
	Button views_menu;
	SlidingDrawer drawer;
	
	/**
	 * WebView from XWalk project to increase compatibility of WebGL
	 */
    private XWalkView browser;
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		View rootView = inflater.inflate(R.layout.map_display, container,
				false);
		
		((MainActivity)getActivity()).refreshActionBarIcons();

		//((MainActivity)getActivity()).showTutorialMap();
		
		//Browser
		/*if(mXwalkView==null){
			mXwalkView = ((MainActivity)getActivity()).getBrowserMap();
		}*/

		//Hud Panel
		drawer = (SlidingDrawer) rootView.findViewById(R.id.slidingDrawer1);
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            public void onDrawerOpened() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(browser.getLayoutParams());
            	if(getResources().getConfiguration().orientation==android.content.res.Configuration.ORIENTATION_PORTRAIT){
                	layoutParams.height = browser.getHeight()-slider_content.getHeight();
                	layoutParams.width = LayoutParams.MATCH_PARENT;
            	}else{
            		layoutParams.width = browser.getWidth()-slider_content.getWidth();
                	layoutParams.height = LayoutParams.MATCH_PARENT;
            	}
            	browser.setLayoutParams(layoutParams);
            	((MainActivity)getActivity()).setHudPanelOpen(true);
            }
        });
       
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            public void onDrawerClosed() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(browser.getLayoutParams());
            	layoutParams.height = LayoutParams.MATCH_PARENT;
            	layoutParams.width = LayoutParams.MATCH_PARENT;
            	browser.setLayoutParams(layoutParams);
            	((MainActivity)getActivity()).setHudPanelOpen(false);
            }
        });
        
		slider_content = (LinearLayout) rootView.findViewById(R.id.content);
		
		browser = new XWalkView(getActivity().getApplicationContext(), getActivity());
		//mXwalkView.setBackgroundResource(R.color.black);
		browser.setBackgroundColor(0x00000000);
		browser.setResourceClient(new MyResourceClient(browser));
		browser.setUIClient(new MyUIClient(browser));
		browser.clearCache(true);
		
        /*WebSettings browserSettingsMap = browser.getSettings();
    	
    	browserSettingsMap.setJavaScriptEnabled(true);
    	browserSettingsMap.setUseWideViewPort(false);
    	browserSettingsMap.setAllowFileAccessFromFileURLs(true);
    	browserSettingsMap.setAllowUniversalAccessFromFileURLs(true);*/
    	

    	simulator = ((MainActivity)getActivity()).getSimulator();
    	
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), simulator.getSimulationResults()), "Android");
    	
		
    	simulator.setHudView(Browsers.Map, rootView, browser);
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	browser.setLayoutParams(browser_params);
    	
    	browserLayout.addView(browser);
    	
    	
    	//Play/Pause/Stop buttons
    	ImageButton but_play = (ImageButton)rootView.findViewById(R.id.imageButtonPlay);
    	ImageButton but_stop = (ImageButton)rootView.findViewById(R.id.imageButtonStop);
    	simulator.setControlButtons(but_play,but_stop);
    	simulator.setCorrectSimulatorControls();
    	
    	views_menu = (Button) rootView.findViewById(R.id.buttonMissionNew);
    	views_menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				showPopup(arg0);
			}
    		
    	});
    	
    	views_menu.setText(titleOfViewId(((StavorApplication)getActivity().getApplication()).follow_sc));
    	
    	ImageButton but_clear = (ImageButton)rootView.findViewById(R.id.imageButtonClear);
    	but_clear.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				simulator.getSimulationResults().resetMapPathBuffer();
            	browser.load("javascript:clearPath()",null);
			}
    		
    	});
    	
    	ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBrowser);
    	FrameLayout progressBarLayout = (FrameLayout) rootView.findViewById(R.id.frameLayoutProgress);
    	progressBar.setProgress(10);
    	((MainActivity)getActivity()).setBrowserProgressBarMap(progressBar,progressBarLayout);
    	
		//needs to have browser defined but not loaded yet
    	rootView.post(new Runnable()
    	{
    	    @Override
    	    public void run()
    	    {
    	    	if(((MainActivity)getActivity()).getHudPanelOpen())
    	    		drawer.open();
    	    	/*
    	    	if(((MainActivity)getActivity()).getLoadBrowserFlagMap()){
    	    		//mXwalkView.load(Parameters.Web.STARTING_PAGE,null);
    	    		//mXwalkView.load("javascript:showLoadingScreen()",null);
    	    		
    	    		mXwalkView.loadUrl("javascript:reloadModel()");
    	    		((MainActivity)getActivity()).resetLoadBrowserFlagMap();
    	    	}else{
    	    		mXwalkView.loadUrl("javascript:setLoaded()");
    	    	}*/

            	browser.load(Parameters.Web.STARTING_PAGE_MAP,null);
    	    }
    	});
    	
		return rootView;
	}
	

	private String titleOfViewId(int id){
		switch (id) {
	        case R.id.menu_mapviews_free:
	        	return getString(R.string.menu_mapviews_free);
	        case R.id.menu_mapviews_locked:
	        	return getString(R.string.menu_mapviews_locked);
	        default:
	        	return getString(R.string.menu_mapviews_locked);
	    }
	}
	
	/**
	 * Shows the visualization Views menu
	 * @param v
	 */
    private void showPopup(View v) {
    	PopupMenu popup = new PopupMenu(getActivity(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        	@Override
            public boolean onMenuItemClick(MenuItem item) {
        		String com_view = (String)item.getTitle();
        		String command;
            	((StavorApplication)getActivity().getApplication()).follow_sc = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.menu_mapviews_free:
                    	command = getString(R.string.key_mapviews_free);
                    	break;
                    case R.id.menu_mapviews_locked:
                    	command = getString(R.string.key_mapviews_locked);
                    	break;
                    default:
                        return false;
                }
                views_menu.setText(com_view);
                browser.load("javascript:changeView('"+command+"')",null);
                return true;
            }
        });
        popup.inflate(R.menu.views_map);
        popup.show();

    }
    
	
	@Override
	public void onDestroyView(){
		simulator.setBrowserLoaded(false);
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        browser.onDestroy();
    }
	
    @Override
	public void onPause() {//Pause simulator and browser
        super.onPause();
        if(simulator!=null){
        	simulator.temporaryPause();
        }
        if (browser != null) {
            browser.load("javascript:updateMapCenter()",null);
        	browser.pauseTimers();
        	browser.onHide();
            //mXwalkView.pauseTimers();
            //mXwalkView.onHide();
        }
    }

    @Override
	public void onResume() {//Resume browser
        super.onResume();
        if (browser != null) {
        	browser.resumeTimers();
        	browser.onShow();
            //mXwalkView.resumeTimers();
            //mXwalkView.onShow();
        }
        if(simulator!=null){
        	simulator.resumeTemporaryPause();
        }
    }
	
	@Override
	public void onDetach() {
		((MainActivity)getActivity()).resetBrowserProgressBarMap();
		simulator.clearHud();
		//XWalk
        if (browser != null) {
            //mXwalkView.onDestroy();
			//System.gc();
        	browserLayout.removeView(browser);
        	browser.destroyDrawingCache();
        	//browser.destroy();
        }
        //unbindDrawables(getView());
	    super.onDetach();
	}
	
	/*private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        ((ViewGroup) view).removeAllViews();
        }
    }*/
	
}
