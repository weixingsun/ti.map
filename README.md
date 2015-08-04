Changed Google Maps implementation for Android:
1.add click event into map event like this:
	map.addEventListener('click', function(e) {
	    var lng = e.longitude;
	    var lat = e.latitude;
		Ti.API.info('map.clicked:'+lat+','+lng);
	});
2.add annotation by resource id
	var params = {
        latitude:lat,
        longitude:lng,
        animate:true,
        image: img, //resourceId,
        draggable: true,
  };
  module.createAnnotation(params);
  
3. more to add
