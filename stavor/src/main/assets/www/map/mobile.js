// initialize map when page ready
var map;

// Get rid of address bar on iphone/ipod
var fixSize = function() {
    window.scrollTo(0,0);
    document.body.style.height = '100%';
    if (!(/(iphone|ipod)/.test(navigator.userAgent.toLowerCase()))) {
        if (document.body.parentNode) {
            document.body.parentNode.style.height = '100%';
        }
    }
};
setTimeout(fixSize, 700);
setTimeout(fixSize, 1500);

var init = function () {
    // create map
    map = new OpenLayers.Map({
        div: "map",
        theme: null,
        numZoomLevels: 18,
        controls: [
            new OpenLayers.Control.TouchNavigation({
                dragPanOptions: {
                    enableKinetic: true
                }
            })
        ],
        layers: [
            new OpenLayers.Layer.OSM("OpenStreetMap", ["http://a.basemaps.cartocdn.com/dark_all/${z}/${x}/${y}.png"]/*null*/, {
                transitionEffect: 'null',
        wrapDateLine: false
            })
        ],
        center: new OpenLayers.LonLat(0, 0),
        zoom: 0
    });

	/*map.events.register("moveend", map, function(){
	     var lyr = map.getLayersByName('vectorLayer')[0];
	     map.removeLayer(lyr);
	});*/

	map.events.register("zoomend", map, function(){
	     storeNewZoom(map.getZoom());
	});

};
