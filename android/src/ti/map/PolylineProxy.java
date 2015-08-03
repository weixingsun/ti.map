package ti.map;

import java.util.ArrayList;
import java.util.HashMap;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.util.TiConvert;

import ti.map.Shape.IShape;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


@Kroll.proxy(name="Polyline",creatableInModule=MapModule.class, propertyAccessors = {

	MapModule.PROPERTY_STROKE_COLOR, MapModule.PROPERTY_STROKE_WIDTH,

	PolylineProxy.PROPERTY_STROKE_COLOR2,
	PolylineProxy.PROPERTY_STROKE_WIDTH2,

	PolylineProxy.PROPERTY_ZINDEX,

	MapModule.PROPERTY_POINTS,

})
public class PolylineProxy extends KrollProxy implements IShape
{

	private PolylineOptions options;
	private Polyline polyline;

	private static final int MSG_FIRST_ID = KrollProxy.MSG_LAST_ID + 1;

	private static final int MSG_SET_POINTS 	  = MSG_FIRST_ID + 499;
	private static final int MSG_SET_STROKE_COLOR = MSG_FIRST_ID + 501;
	private static final int MSG_SET_STROKE_WIDTH = MSG_FIRST_ID + 502;
	private static final int MSG_SET_ZINDEX 	  = MSG_FIRST_ID + 503;

	public static final String PROPERTY_STROKE_COLOR2 = "color";
	public static final String PROPERTY_STROKE_WIDTH2 = "width";

	public static final String PROPERTY_ZINDEX = "zIndex";

	public PolylineProxy() {
		super();
	}

	public PolylineProxy(TiContext tiContext) {
		this();
	}

	@Override
	public boolean handleMessage(Message msg)
	{

		AsyncResult result = null;
		switch (msg.what) {
			case MSG_SET_POINTS: {
				result = (AsyncResult) msg.obj;
				polyline.setPoints(processPoints(result.getArg(), true));
				result.setResult(null);
				return true;
			}
			case MSG_SET_STROKE_WIDTH: {
				result = (AsyncResult) msg.obj;
				polyline.setWidth((Float)result.getArg());
				result.setResult(null);
				return true;
			}
			case MSG_SET_STROKE_COLOR: {
				result = (AsyncResult) msg.obj;
				polyline.setColor((Integer)result.getArg());
				result.setResult(null);
				return true;
			}
			case MSG_SET_ZINDEX: {
				result = (AsyncResult) msg.obj;
				polyline.setZIndex((Float)result.getArg());
				result.setResult(null);
				return true;
			}
			default : {
				return super.handleMessage(msg);
			}
		}
	}
	public void processOptions() {

		options = new PolylineOptions();
		// (int) 	 strokeColor
		// (float)	 strokeWidth
		// (int) 	 fillColor
		// (float)	 zIndex

		if (hasProperty(MapModule.PROPERTY_POINTS)) {
			 processPoints(getProperty(MapModule.PROPERTY_POINTS), false);
		}

		if (hasProperty(MapModule.PROPERTY_STROKE_COLOR)) {
			options.color(TiConvert.toColor((String)getProperty(MapModule.PROPERTY_STROKE_COLOR)));
		}
		// alternate API
		if (hasProperty(PolylineProxy.PROPERTY_STROKE_COLOR2)) {
			options.color(TiConvert.toColor((String)getProperty(PolylineProxy.PROPERTY_STROKE_COLOR2)));
		}
			
		if (hasProperty(MapModule.PROPERTY_STROKE_WIDTH)) {
			options.width(TiConvert.toFloat(getProperty(MapModule.PROPERTY_STROKE_WIDTH)));
		}
		// alternate API
		if (hasProperty(PolylineProxy.PROPERTY_STROKE_WIDTH2)) {
			options.width(TiConvert.toFloat(getProperty(PolylineProxy.PROPERTY_STROKE_WIDTH2)));
		}

		if (hasProperty(PolylineProxy.PROPERTY_ZINDEX)) {
			options.zIndex(TiConvert.toFloat(getProperty(PolylineProxy.PROPERTY_ZINDEX)));
		}

	}

	public void addLocation(Object loc, ArrayList<LatLng> locationArray, boolean list) {
		LatLng location = parseLocation(loc);
		if (list) {
			locationArray.add(location);
		} else {
			options.add(location);
		}

	}

	public ArrayList<LatLng> processPoints(Object points, boolean list) {

		ArrayList<LatLng> locationArray = new ArrayList<LatLng>();
		//multiple points
		if (points instanceof Object[]) {
			Object[] pointsArray = (Object[]) points;
			for (int i = 0; i < pointsArray.length; i++) {
				Object obj = pointsArray[i];
				addLocation(obj, locationArray, list);
			}
			return locationArray;
		}

		//single point
		addLocation(points, locationArray, list);
		return locationArray;
	}

	public PolylineOptions getOptions() {
		return options;
	}

	public void setPolyline(Polyline r) {
		polyline = r;
	}

	public Polyline getPolyline() {
		return polyline;
	}

	@Override
	public void onPropertyChanged(String name, Object value) {

		super.onPropertyChanged(name, value);

		if (polyline == null) {
			return;
		}

		else if (name.equals(MapModule.PROPERTY_POINTS)) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_POINTS), value);
		}

		else if (name.equals(MapModule.PROPERTY_STROKE_WIDTH)) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_STROKE_WIDTH), TiConvert.toFloat(value));
		}
		// alternate API
		else if (name.equals(PolylineProxy.PROPERTY_STROKE_WIDTH2)) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_STROKE_WIDTH), TiConvert.toFloat(value));
		}

		else if (name.equals(MapModule.PROPERTY_STROKE_COLOR)) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_STROKE_COLOR), TiConvert.toColor((String)value));
		}
		// alternate API
		else if (name.equals(PolylineProxy.PROPERTY_STROKE_COLOR2)) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_STROKE_COLOR), TiConvert.toColor((String)value));
		}

		else if (name.equals(PolylineProxy.PROPERTY_ZINDEX)) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_ZINDEX), TiConvert.toFloat(value));
		}

	}

	// A location can either be a an array of longitude, latitude pairings or
	// an array of longitude, latitude objects.
	// e.g. [123.33, 34.44], OR {longitude: 123.33, latitude, 34.44}
	private LatLng parseLocation(Object loc) {
		LatLng location = null;
		if (loc instanceof HashMap) {
			HashMap<String, String> point = (HashMap<String, String>) loc;
			location = new LatLng(TiConvert.toDouble(point
					.get(TiC.PROPERTY_LATITUDE)), TiConvert.toDouble(point
					.get(TiC.PROPERTY_LONGITUDE)));
		} else if (loc instanceof Object[]) {
			Object[] temp = (Object[]) loc;
			location = new LatLng(TiConvert.toDouble(temp[1]), TiConvert.toDouble(temp[0]));
		}
		return location;
	}



}
