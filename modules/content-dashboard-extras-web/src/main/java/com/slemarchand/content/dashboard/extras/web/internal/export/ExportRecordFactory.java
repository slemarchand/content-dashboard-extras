package com.slemarchand.content.dashboard.extras.web.internal.export;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.ContentDashboardItem;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author Sébastien Le Marchand
 *
 */
@Component(immediate = true, service = ExportRecordFactory.class)
public class ExportRecordFactory {

	public List<ExportRecord> create(List<ContentDashboardItem<?>> items) {
		
		DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		
		return items.stream().map(item -> this.create(item, df)).collect(Collectors.toList());
	}
	
	public ExportRecord create(ContentDashboardItem<?> item, DateTimeFormatter df) {
		
		Locale defaultLocale = item.getDefaultLocale();
		
		ExportRecord record = new ExportRecord();
		
		record.setClassName(item.getClassName());
		record.setClassPK(item.getClassPK());
		record.setCreateDate(_toString(item.getCreateDate(), df));
		record.setDefaultLocale(defaultLocale.toString());
		record.setExpirationDate(_toString(item.getExpirationDate(), df));
		record.setModifiedDate(_toString(item.getModifiedDate(), df));
		record.setPublishDate(_toString(item.getPublishDate(), df));
		record.setScopeName(item.getScopeName(defaultLocale));
		record.setTitle(item.getTitle(defaultLocale));
		record.setUserName(item.getUserName());
		
		long userId = item.getUserId();
		
		record.setUserId(userId);
		
		User user = _userLocalService.fetchUser(userId);
		
		if(user != null) {
			record.setUserScreenName(user.getScreenName());
			record.setUserEmailAddress(user.getEmailAddress());
		}
		
		return record;
	}
	
	private String _toString(Date d, DateTimeFormatter df) {
		
		if(d == null) {
			return StringPool.BLANK;
		}
		
		ZoneId zone = ZoneId.systemDefault();
		
		LocalDateTime dt = d.toInstant()
	      .atZone(zone)
	      .toLocalDateTime();
		
		return df.format(dt);
	}
	
	@Reference
	private UserLocalService _userLocalService;
}
