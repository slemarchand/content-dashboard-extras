package com.slemarchand.content.dashboard.extras.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.slemarchand.content.dashboard.extras.web.internal.util.JSONArrayUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author Sébastien Le Marchand
 *
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet",
		"mvc.command.name=/content_dashboard_extras/get_favorites"
	},
	service = MVCResourceCommand.class
)
public class GetFavoritesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

	
		try {
	
			PortletPreferences prefs = PortletPreferencesFactoryUtil.getPortletSetup(resourceRequest);
			
			JSONArray jsonArray = JSONArrayUtil.deserializeJSONArray(prefs.getValue("com.slemarchand.content.dashboard.extras.favorites", "[]"));
			
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonArray);
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception, exception);
			}

			resourceResponse.setStatus(500);
			
			Locale locale = _portal.getLocale(resourceRequest);

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				locale, getClass());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						resourceBundle, "an-unexpected-error-occurred")));
		}
	}
	
	private static final Log _log = LogFactoryUtil.getLog(
		GetFavoritesMVCResourceCommand.class);

	@Reference
	private Portal _portal;

}