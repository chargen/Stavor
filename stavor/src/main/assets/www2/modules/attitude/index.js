// MAIN
	//***********************************************************************************************************************
	//		GLOBAL VARIABLES
	//***********************************************************************************************************************
	setLoadingProgress(5);
	var container, scene, camera, renderer, controls, stats, light, delta;
	var sun, sunGlow, sunLight, lineSun, spriteSun, contextSun;
	var earth, contextEarth, lineEarth, spriteEarth;
	var plane_orb, incl_arc, spriteInclination, contextInclination;
	//var keyboard = new THREEx.KeyboardState();
	var clock = new THREE.Clock();
	var spacecraft, arrow_vel, arrow_accel, arrow_momentum, target_a, vector_a, direction_a;
	var long_arc, lat_arc, long_sprite, lat_sprite, lineAngle, lineSpheric, lineSpheric2;
	var vectors_arc, vectors_sprite;
	var origin = new THREE.Vector3(0,0,0);

	var fontsizeSun, borderColorSun, borderThicknessSun, backgroundColorSun, fontColorSun;
	var fontsizeEarth, borderColorEarth, borderThicknessEarth, backgroundColorEarth, fontColorEarth;
	var fontsizeInclination, borderColorInclination, borderThicknessInclination, backgroundColorInclination, fontColorInclination;
	var fontsizeLongitude, borderColorLongitude, borderThicknessLongitude, backgroundColorLongitude, fontColorLongitude;
	var fontsizeLatitude, borderColorLatitude, borderThicknessLatitude, backgroundColorLatitude, fontColorLatitude;
	var fontsizeAngles, borderColorAngles, borderThicknessAngles, backgroundColorAngles, fontColorAngles;

	var fps_update_counter = 0;
	var miniSphereX,miniSphereXX,miniSphereY,miniSphereYY,miniSphereZ,miniSphereZZ;
	var gui, parameters;
	var axis_x = new THREE.Vector3(1,0,0);
	var axis_z = new THREE.Vector3(0,0,1);

	var textureEarth, textureSun, textureSun2, textureSun3, textureSun4;
	//-----------------------------------------------------------------------------------------------------------------------
	//			SCENE PARAMS (Hard-coded parameters)
	//-----------------------------------------------------------------------------------------------------------------------
	var canvas_mode = false;
	var cam_init_pos  = new THREE.Vector3(300,300,300);
	var cam_view_angle = 25;
	var cam_rend_near = 0.1;
	var cam_rend_far = 20000;
	
	var sphere_radius = 100;
	var miniSphere_radius = 5;
	var miniSphere_margin = 4;
	var torus_radius = sphere_radius;
	var torus_tube = 0.5;
	
	var sc_scale = 1.5;

	var init_sc_dir_xyz = new THREE.Vector3(0.577350269,0.577350269,0.577350269);
	var init_sc_up_xyz = new THREE.Vector3(-0.577350269,-0.577350269,0.577350269);
	var init_sc_dir_rear = new THREE.Vector3(0,0,-1);
	var init_sc_up_rear = new THREE.Vector3(0,1,0);
	var init_sc_dir_front = new THREE.Vector3(0,0,1);
	var init_sc_up_front = new THREE.Vector3(0,1,0);
	var init_sc_dir_top = new THREE.Vector3(0,1,0);
	var init_sc_up_top = new THREE.Vector3(0,0,1);
	var init_sc_dir_bottom = new THREE.Vector3(0,-1,0);
	var init_sc_up_bottom = new THREE.Vector3(0,0,1);
	var init_sc_dir_left = new THREE.Vector3(1,0,0);
	var init_sc_up_left = new THREE.Vector3(0,1,0);
	var init_sc_dir_right = new THREE.Vector3(-1,0,0);
	var init_sc_up_right = new THREE.Vector3(0,1,0);

	var sc_axis_lenght = sphere_radius*0.4;
	var sc_body_color = 0xDDDDDD;
	var sc_window_color = 0x00d4ff;
	var sc_engine_color = 0x545454;
	var sc_eng_solid_color = 0x5d00ff;//For when not using texture

	var sun_radius = 5;
	var sun_solid_color = 0xffb600 ;//For when not using textures
	var sun_obj_dist = sphere_radius + 10;
	
	var earth_radius = 8;
	var earth_solid_color = 0x00bfff ;//For when not using textures
	var earth_obj_dist = sphere_radius + earth_radius;

	var planes_width = sphere_radius+earth_radius*2;
	//var plane_theta_seg = 30;
	//var plane_phi_seg = 10;

	var arc_inclination_radius = sphere_radius + torus_tube +1;
	var arc_radius = sphere_radius+earth_radius*2;
	var arc_sphcoord_radius = 3*sphere_radius/4;
	var arc_vectors_radius = 3*sphere_radius/4;
	var arc_sprite_radius = arc_radius+3;
	var arc_tube = 0.5;
	var arc_color = 0xFFFF00;
	
	var arrow_head_length = 9;
	var arrow_head_width = 5;
	var arrow_max_length = sphere_radius;
	var target_head_length = 2;
	var target_head_width = 1;
	var target_length = sphere_radius + target_head_width;
	var momentum_length = sphere_radius/3;
	var momentum_head_length = 8;
	var momentum_head_width = 7;	

//-----------------------------------------------------------------------------------------------------------------------
//			DEBUG OPTIONS
//-----------------------------------------------------------------------------------------------------------------------

	var auto_rotate_sc = false;// If true, it ignores the simulator attitude and rotates the spacecraft.
		
//-----------------------------------------------------------------------------------------------------------------------
//			PERFORMANCE VALUES (Set at initialization)
//-----------------------------------------------------------------------------------------------------------------------
	
	var show_fps = true;//Show FPS stats in Android
	var fps_update_skips = 60;

	var show_sky = true;
	var show_sphere = true;
	var show_mini_spheres = true;
	var show_circles = true;
	var show_axis = true;
	var show_axis_labels = true;



	var show_planes = false;//XGGDEBUG UNUSED

	//Angles and planes
	var show_orbital_plane = false;
	var plane_xy_color = 0xff0094;
	var plane_orb_color = 0x65ff00;
	var show_inclination = true;// depends on show_planes, called: show orbit-xy planes 

	var show_spheric_coords = false;
	var spheric_coords_selection = "Earth";//Any of the basic indicators except for the attitude

	var show_vectors_angle = false;
	var vectors_angle_sel1 = "Earth";
	var vectors_angle_sel2 = "Velocity";

	//Removed due to inconsistency if S/C doesn't exist
	//var show_spacecraft = true;//If set to false, instead of a S/C it will be a miniSphere in the reference position.
	var show_sc_axis = true;
	var sc_show_eng_texture = true;
	
	var show_sun = true;
	var sun_rotates = true;
	var sun_rotation_speed = 5;//Rotation speed multiplier [0->9]
	var show_sun_texture = true;
	var sun_simple_glow = true;//Recomended to not use the shader glow, problems in android
	var sun_show_line = true;
	var sun_show_dist = true;
	
	var show_earth = true;
	var earth_rotates = true;
	var earth_rotation_speed = 2;//Rotation speed multiplier [0->9]
	var show_earth_texture = true;
	var earth_show_line = true;
	var earth_show_dist = true;
	
	var show_velocity = true;
	var color_velocity = 0x001dff;
	var limit_velocity = 10; //Km/s value corresponding to the full length arrow (touching the sphere)
	var show_acceleration = true;
	var color_acceleration = 0xfc00b0;
	var limit_acceleration = 5; //Km/s2 value corresponding to the full length arrow (touching the sphere)
	var show_momentum = true;
	var color_momentum = 0x00fc19;
	var show_target_a = false;
	var color_target_a = 0xff0000;
	var value_target_a  = new THREE.Vector3(-5,-5,-5); // direction --> will be normalized
	var show_vector_a = false;
	var color_vector_a = 0x00fffa;
	var limit_vector_a = 25; //In the same units as the provided values
	var value_vector_a  = new THREE.Vector3(-7,-5,-5); // will be normalized with its limit
	var show_direction_a = false;
	var color_direction_a = 0xffff00;
	var value_direction_a  = new THREE.Vector3(-5,-5,-7); // direction --> will be normalized
	
	var performance_level = 3;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;
	
	getInitialization();//If used in Android, update the init params with the Android configuration
	
	// Segments
	if(performance_level<1)
		performance_level=1;
	var segments_scale = performance_level;//Multiply segments of all geometries: 
	var sc_body_segments = 8 * segments_scale;
	var sc_window_segments = 10 * segments_scale;
	var sc_engine_segments = 10 * segments_scale;
	var sc_eng_disk_segments = sc_engine_segments;
	var sun_seg = 10 * segments_scale;
	var earth_seg = 12 * segments_scale;
	var sphere_segments = 20 * segments_scale;
	var miniSphere_seg = 7 * segments_scale;
	var torus_seg_r = 4 * segments_scale;
	var torus_seg_t = 32 * segments_scale;
	var arc_seg_r = 4 * segments_scale;
	var arc_seg_t = 32 * segments_scale;
	var arrow_segments = 4 * segments_scale;
	var momentum_segments = 4 * segments_scale;
	var target_segments = 8 * segments_scale;
	// smooth my curve over this many points
	var arc_resolution = 30*performance_level;
	var plane_resolution = 20*performance_level;
//-----------------------------------------------------------------------------------------------------------------------
//			DYNAMIC VALUES (Updated at each cycle)
//-----------------------------------------------------------------------------------------------------------------------
	
	var value_attitude  = new THREE.Quaternion(0,0,0,1);
	var value_sun  = new THREE.Vector3(-1.1414775124432093E7,-1.464188429948789E8,7716114.240559303); //Km
	var value_earth  = new THREE.Vector3(7.337791634217176E-12,42164.0,-2.010800849831316E-12); //Km
	var value_velocity  = new THREE.Vector3(2.83195518282009,-5.49945264687097E-16,-1.1973314470667253); //Km/s
	var value_acceleration  = new THREE.Vector3(-4.517719883627554E-4,0.22420886513859567,1.9100613309698625E-4); //Km/s2
	var value_momentum  = new THREE.Vector3(5.048428313412141E10,-3.091270787372526E-6,1.1940655832842628E11); // direction --> will be normalized
	
//-----------------------------------------------------------------------------------------------------------------------

	init();
	setLoadingProgress(100);
	animate();

// FUNCTIONS 		


function animate() 
{
    	requestAnimationFrame( animate );
	render();		
	update();
}



function render() 
{
	renderer.render( scene, camera );
}