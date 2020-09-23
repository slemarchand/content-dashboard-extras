package com.slemarchand.content.dashboard.extras.web.internal.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;

import java.util.Collection;

/**
 * 
 * @author Sébastien Le Marchand
 *
 */
public class JSONArrayUtil {
	
	public static JSONArray deserializeJSONArray(String jsonArray) {
		
		Collection<?> deserialized = (Collection<?>)JSONFactoryUtil.looseDeserialize(jsonArray);
		
		return JSONFactoryUtil.createJSONArray(deserialized);
	}

}
