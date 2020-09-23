package com.slemarchand.content.dashboard.extras.web.internal.portlet.filter;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;

import java.util.regex.Pattern;

import javax.portlet.RenderRequest;
import javax.portlet.filter.PortletFilter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
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
		"javax.portlet.name=" +
				"com_liferay_content_dashboard_web_portlet_" +
					"ContentDashboardAdminPortlet"
	},
	service = PortletFilter.class
)
public class ContentDashboardAdminPortletFilter extends ContentTransformationPortletFilter {

	@Override
	protected String transform(RenderRequest renderRequest, String content) throws Exception {
		
		String transformedContent;
		
		try {
		
			long timestamp = _bundleContext.getBundle().getLastModified(); 
			
			Document doc = Jsoup.parse(content);
			
			doc.prepend("<style id=\"content_dashboard_extras_css\">@import '/o/content-dashboard-extras-web/css/main.css?t=" + timestamp + "';</style>");
			
			doc.append("<script id=\"content_dashboard_extras_js\" src=\"/o/content-dashboard-extras-web/js/main.js?t=" + timestamp + "\"></script>");
			
			doc.getElementsByClass("sheet-title").last().append(_getExportNavItemHTML(renderRequest));
				
			transformedContent = doc.html();
			
		} catch(Exception e) {
			transformedContent = content;
		}
		
		return transformedContent;
	}

	private String _getExportNavItemHTML(RenderRequest renderRequest) {
		
		String html = StringPool.BLANK;
		
		try {
			html = StreamUtil.toString(this.getClass().getResourceAsStream("/META-INF/resources/html/export.html"));
		
			String exportURL = _portal.getCurrentURL(renderRequest);
			
			exportURL = _http.addParameter(exportURL, "p_p_resource_id", "/content_dashboard_extras/export");
			exportURL = _http.setParameter(exportURL, "p_p_lifecycle", "2");

			html = html.replaceAll(Pattern.quote("{exportURL}"), exportURL);
		
		} catch (Exception e) {
			_log.error(e);
		}
		
		return html;
	}

	@Override
	protected Portal getPortal() {
		return _portal;
	}
	
	@Activate
	private void _activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Reference
	private Portal _portal;
	
	@Reference
	private Http _http;
	
	private BundleContext _bundleContext;
	
	private static final Log _log = LogFactoryUtil.getLog(
			ContentDashboardAdminPortletFilter.class);

}