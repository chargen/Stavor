package cs.si.stavor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

import android.view.View;
import android.webkit.WebView;

import com.google.gson.Gson;

import cs.si.satcor.MainActivity;
import cs.si.satcor.StavorApplication;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.station.LatLon;
import cs.si.stavor.station.StationArea;
import cs.si.stavor.station.VisibilityCircle;

/**
 * Contains and handles both the model information and configuration
 * @author Xavier Gibert
 *
 */
public class ModelSimulation {
	private Gson gson = new Gson();
    private ModelConfigurationMap config;
    private ModelStateMap state;
    private MainActivity activity;
    private WebView browser;
    private boolean isBrowserLoaded;
    private Browsers selectedBrowser = Browsers.None;
    
    public ModelSimulation(MainActivity acv){
    	isBrowserLoaded = false;
    	activity=acv;
    	config = new ModelConfigurationMap(activity.getApplicationContext(),
    			((StavorApplication)activity.getApplication()).db,
    			getMapPathBuffer(),
    			((StavorApplication)activity.getApplication()).follow_sc);
    }
    
    /**
     * Initialize the required elements for the simulation, 
     * before the simulator is played to save time
     */
    public void preInitialize(){
    	try{
	    	if(earthFixedFrame==null){
				earthFixedFrame = CelestialBodyFactory.getEarth().getBodyOrientedFrame();
	    	}
			if(earth==null && earthFixedFrame!=null){
				earth = new OneAxisEllipsoid(
	    				Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
	    		 		Constants.WGS84_EARTH_FLATTENING,
	    		 		earthFixedFrame);
				
			}
    	} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Establish the Hud View
     * @param hud
     * @param mBrowser
     */
    public void setHud(Browsers type, View mBrowser){
    	selectedBrowser = type;
    	if(selectedBrowser.equals(Browsers.Map)){
        	browser = (WebView)mBrowser;
    	}
    }
    
    public void clearHud(){
      	selectedBrowser = Browsers.None;
    	browser = null;
    }
    
    /**
     * Set the loaded status of the browser
     * @param is
     */
    public void setBrowserloaded(boolean is) {
    	isBrowserLoaded = is;
	}

    /**
     * Returns the Initialization for the WebGL model in a JavaScript readable format
     * @return
     */
	public synchronized String getInitializationMapJSON() {
    	config = new ModelConfigurationMap(activity.getApplicationContext(),
    			((StavorApplication)activity.getApplication()).db,
    			getMapPathBuffer(),
    			((StavorApplication)activity.getApplication()).follow_sc);
        return gson.toJson(config);
    }
    
    /**
     * Pushes the new simulation step to the WebGL model
     */
    public void pushSimulationModel(){
    	if(browser!=null && isBrowserLoaded){
    		if(selectedBrowser.equals(Browsers.Map)){
    			if(mapPathBuffer.size()!=0){
    				browser.loadUrl("javascript:updateModelState('"+gson.toJson(state)+"')");
    			}
    		}
    	}
	}
    
    private void clearSimulationModel(){
    	if(browser!=null && isBrowserLoaded){
    		if(selectedBrowser.equals(Browsers.Map)){
				browser.loadUrl("javascript:clearPath()");
    		}
    	}
	}
    
    private OneAxisEllipsoid earth;
    private Frame earthFixedFrame;
    private AbsoluteDate date_tmp = null;
    public void updateSimulation(SpacecraftState scs, int sim_progress){
    	if(selectedBrowser.equals(Browsers.Map)){
    		 try {
    			 
    			 //Sat_Pos
    		 	GeodeticPoint gp = earth.transform(scs.getPVCoordinates(earthFixedFrame).getPosition(), earthFixedFrame, scs.getDate());
    		 	double lat = gp.getLatitude()*180/Math.PI;
    		 	double lon = gp.getLongitude()*180/Math.PI;
    		 	double alt = gp.getAltitude();
    		 	if(!Double.isNaN(lat)&&!Double.isNaN(lon))
    		 		addToMapPathBuffer(lat, lon, alt);
    		 	
    		 	//Sun_Pos
    		 	GeodeticPoint gp2 = earth.transform(CelestialBodyFactory.getSun().getPVCoordinates(scs.getDate(), earthFixedFrame).getPosition(), earthFixedFrame, scs.getDate());
    		 	double lat2 = gp2.getLatitude()*180/Math.PI;
    		 	double lon2 = gp2.getLongitude()*180/Math.PI;
    		 	if(!Double.isNaN(lat)&&!Double.isNaN(lon)){
    		 		sun_lat = lat2;
    		 		sun_lon = lon2;
    		 	}
    		 	
    		 	//Solar terminator
    		 	ArrayList<LatLon> solarTerminator = new ArrayList<LatLon>();
    		 	if(date_tmp == null || Math.abs(scs.getDate().durationFrom(date_tmp))>Parameters.Map.solar_terminator_threshold){
    		 		date_tmp = scs.getDate();
    		 		
	    		 	Vector3D s = CelestialBodyFactory.getSun().getPVCoordinates(
	    		 				scs.getDate(), 
	    		 				earthFixedFrame
	    		 			).getPosition().normalize();
	    		 	Vector3D t = s.orthogonal();
	    		 	Vector3D u = Vector3D.crossProduct(t, s);
	    		 	
	    		 	double alpha_o = Math.atan((-t.getY())/(u.getY()));
	    		 	Vector3D test_point = (t.scalarMultiply(Math.cos(alpha_o))).add(u.scalarMultiply(Math.sin(alpha_o)));
	    		 	if(test_point.getX()>0)
	    		 		alpha_o = alpha_o + FastMath.PI;
	    		 	
	    		 	double alpha_margin = 0.02;
	    		 	double alpha = alpha_margin;
	    		 	double d_alpha = 2*FastMath.PI/Parameters.Map.solar_terminator_points;
	    		 	for(int i = 0; i<Parameters.Map.solar_terminator_points; i++){
	    		 		Vector3D point = (t.scalarMultiply(Math.cos(alpha+alpha_o))).add(u.scalarMultiply(Math.sin(alpha+alpha_o))).scalarMultiply(Constants.WGS84_EARTH_EQUATORIAL_RADIUS);
	    		 		GeodeticPoint gpoint = earth.transform(point, earthFixedFrame, scs.getDate());
	    		 		solarTerminator.add(new LatLon(gpoint.getLatitude()*180/Math.PI,gpoint.getLongitude()*180/Math.PI));
	    		 		alpha = alpha + d_alpha;
	    		 		if(alpha>(2*FastMath.PI)-alpha_margin)
	    		 			alpha=2*FastMath.PI-alpha_margin;
	    		 	}
    		 	}
    		 	
    		 	
    		 	//Station Areas
    		 	ArrayList<StationArea> stations = new ArrayList<StationArea>();
    		 	for(int i = 0; i < config.stations.length; i++){
    		 		if(config.stations[i].enabled){
    		 			List<LatLon> circle = VisibilityCircle.computeCircle(
    		 					earth,
	    		 				config.stations[i].latitude, 
	    		 				config.stations[i].longitude, 
	    		 				config.stations[i].altitude, 
	    		 				config.stations[i].name, 
	    		 				config.stations[i].elevation, 
	    		 				scs.getPVCoordinates().getPosition().getNorm(), 
	    		 				Parameters.Map.station_visibility_points);
    		 			
    		 			//Find polygon type
    		 			int type = VisibilityCircle.computeType(
    		 					earth,
	    		 				config.stations[i].latitude, 
	    		 				config.stations[i].longitude, 
	    		 				config.stations[i].altitude, 
	    		 				config.stations[i].elevation, 
	    		 				scs.getPVCoordinates().getPosition().getNorm()
	    		 				);
    		 			
    		 			//------------------------
    		 			
	    		 		stations.add(new StationArea(
	    		 				config.stations[i].name,
	    		 				config.stations[i].longitude,
	    		 				circle.toArray(new LatLon[circle.size()]),
	    		 				type
	    		 				));
    		 		}
    		 	}
    		 	
    		 	//Satellite FOV
    		 	//data
    		 	Rotation attitude = scs.getAttitude().withReferenceFrame(earthFixedFrame).getRotation();
    		 	Vector3D close = scs.getPVCoordinates(earthFixedFrame).getPosition();
    		 	double sensor_aperture = 3;
    		 	Vector3D sensor_sc_direction = new Vector3D(0,0,1);
    		 	//step
    		 	sensor_sc_direction = sensor_sc_direction.normalize();
    		 	Vector3D axis = attitude.applyInverseTo(sensor_sc_direction);
    		 	Vector3D ortho = axis.orthogonal();
    		 	Rotation rot_aperture = new Rotation(ortho, sensor_aperture*Math.PI/180);
    		 	Vector3D start = rot_aperture.applyTo(axis);
    		 	//points
    		 	
    		 	double angle_step = 2.0*Math.PI/Parameters.Map.satellite_fov_points;
    		 	double angle = 0;
    		 	ArrayList<LatLon> fov = new ArrayList<LatLon>();
    		 	for(int j = 0; j < Parameters.Map.satellite_fov_points; j++){
    		 		Rotation r_circle = new Rotation(axis, angle);
    		 		Vector3D dir = r_circle.applyTo(start);
    		 		dir = dir.add(close);
    		 		GeodeticPoint intersec = earth.getIntersectionPoint(new Line(dir, close, 0.0), close, earthFixedFrame, scs.getDate());
    		 		if(intersec!=null){
	    		 		fov.add(new LatLon(intersec.getLatitude()*180/Math.PI,intersec.getLongitude()*180/Math.PI));
    		 		}
    		 		angle += angle_step;
    		 	}
    		 	
    		 	
    		 	state = new ModelStateMap(getMapPathBufferLast(), solarTerminator.toArray(new LatLon[solarTerminator.size()]), fov.toArray(new LatLon[fov.size()]), stations.toArray(new StationArea[stations.size()]), sun_lat, sun_lon);
    		 	
    		} catch (OrekitException e) {
    			e.printStackTrace();
    		}
    	}
    }

	double sun_lat, sun_lon;

	private ArrayList<MapPoint> mapPathBuffer = new ArrayList<MapPoint>();
	public synchronized void resetMapPathBuffer() {
		mapPathBuffer.clear();
		clearSimulationModel();
	}
	
	private double tmp_lat=0, tmp_lon=0;
	public synchronized void addToMapPathBuffer(double lat, double lon, double alt) {
		if(Math.abs(tmp_lat-lat)>Parameters.Map.marker_pos_threshold || Math.abs(tmp_lon-lon)>Parameters.Map.marker_pos_threshold){
			tmp_lat = lat;
			tmp_lon = lon;
			mapPathBuffer.add(new MapPoint(lat,lon,alt));
		}
	}
	public synchronized MapPoint[] getMapPathBuffer(){
		MapPoint[] r =
				  (MapPoint[])mapPathBuffer.toArray(new MapPoint[mapPathBuffer.size()]);
		//resetMapPathBuffer();
		return r;
	}
	public synchronized MapPoint getMapPathBufferLast(){
		if(mapPathBuffer.size()>0){
			return mapPathBuffer.get(mapPathBuffer.size()-1);
		}else
			return null;
	}
	
	class MapPoint{
		public MapPoint(double lat, double lon, double alt){
			latitude = lat;
			longitude = lon;
			altitude = alt;
		}
		double latitude = 0;
		double longitude = 0;
		double altitude = 0;
	}

	
}
