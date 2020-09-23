package com.slemarchand.content.dashboard.extras.web.internal.portlet.action;


import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.searcher.Searcher;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.ContentDashboardItem;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.ContentDashboardItemFactoryTracker;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.searcher.ContentDashboardSearchRequestBuilderFactory;
import com.slemarchand.content.dashboard.extras.web.internal.export.ExportContentDashboardItemFactory;
import com.slemarchand.content.dashboard.extras.web.internal.export.ExportRecord;
import com.slemarchand.content.dashboard.extras.web.internal.export.ExportRecordFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
		"mvc.command.name=/content_dashboard_extras/export"
	},
	service = MVCResourceCommand.class
)
public class ExportMVCResourceCommand
	extends BaseMVCResourceCommand {



	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

	
		try {
	
			ExportContentDashboardItemFactory
			contentDashboardItemSearchContainerFactory =
				ExportContentDashboardItemFactory.getInstance(
					_contentDashboardItemFactoryTracker,
					_contentDashboardSearchRequestBuilderFactory, _portal,
					resourceRequest, _searcher);
			
			List<ContentDashboardItem<?>> items =
					contentDashboardItemSearchContainerFactory.getContentDashboardItems();
			
			List<ExportRecord> records = _exportRecordFactory.create(items);
			
			String format = ParamUtil.getString(resourceRequest, "format","json");
			
			String filename = "ContentDashboard_export_" + new Date().getTime() + "." + format;
			
			resourceResponse.setProperty("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			
			switch(format) {
			
				case "csv":
					_writeCSV(resourceResponse, records, CSVWriter.DEFAULT_SEPARATOR);
					break;
				case "csv_semi_comma":
					_writeCSV(resourceResponse, records, ';');
					break;	
				default: 
					JSONPortletResponseUtil.writeJSON(
						resourceRequest, resourceResponse,
						_toJSONArray(records));
			}
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

	private void _writeCSV(ResourceResponse resourceResponse, List<ExportRecord> records, char separator) throws IOException, CsvException {
		
		Writer writer = resourceResponse.getWriter();
		
	    StatefulBeanToCsv<ExportRecord> sbc = new StatefulBeanToCsvBuilder<ExportRecord>(writer)
	       .withSeparator(separator)
	       .build();
	 
	    sbc.write(records);
	    
	    writer.close();
	}

	private Object _toJSONArray(List<ExportRecord> records) {
		
		return JSONFactoryUtil.looseSerialize(records);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportMVCResourceCommand.class);

	@Reference
	private Portal _portal;
	
	@Reference
	private Searcher _searcher;
	
	@Reference
	private ContentDashboardSearchRequestBuilderFactory _contentDashboardSearchRequestBuilderFactory;
	
	@Reference
	private ContentDashboardItemFactoryTracker _contentDashboardItemFactoryTracker;
	
	@Reference
	private ExportRecordFactory _exportRecordFactory;
}