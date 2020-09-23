/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.slemarchand.content.dashboard.extras.web.internal.borrowed.item;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.type.ContentDashboardItemType;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public interface ContentDashboardItem<T> {

	public List<AssetCategory> getAssetCategories();

	public List<AssetCategory> getAssetCategories(long vocabularyId);

	public List<AssetTag> getAssetTags();

	public List<Locale> getAvailableLocales();

	public String getClassName();

	public Long getClassPK();

	public ContentDashboardItemType getContentDashboardItemType();

	public Date getCreateDate();

	public Map<String, Object> getData(Locale locale);

	public Locale getDefaultLocale();

	public Date getExpirationDate();

	public Date getModifiedDate();

	public Date getPublishDate();

	public String getScopeName(Locale locale);

	public String getTitle(Locale locale);

	public long getUserId();

	public String getUserName();

	public String getUserPortraitURL(HttpServletRequest httpServletRequest);

	public List<Version> getVersions(Locale locale);

	public static class Version {

		public Version(String label, String style, double version) {
			_label = label;
			_style = style;
			_version = version;
		}

		public String getLabel() {
			return _label;
		}

		public String getStyle() {
			return _style;
		}

		public double getVersion() {
			return _version;
		}

		public JSONObject toJSONObject() {
			return JSONUtil.put(
				"statusLabel", getLabel()
			).put(
				"statusStyle", getStyle()
			).put(
				"version", getVersion()
			);
		}

		private final String _label;
		private final String _style;
		private final double _version;

	}

}