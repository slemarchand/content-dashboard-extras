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

package com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.type;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Cristina González
 */
public class DDMStructureContentDashboardItemType
	implements ContentDashboardItemType<DDMStructure> {

	public DDMStructureContentDashboardItemType(
		DDMStructure ddmStructure, Group group) {

		_ddmStructure = ddmStructure;
		_group = group;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentDashboardItemType)) {
			return false;
		}

		ContentDashboardItemType contentDashboardItemType =
			(ContentDashboardItemType)object;

		if (Objects.equals(
				getClassName(), contentDashboardItemType.getClassName()) &&
			Objects.equals(
				getClassPK(), contentDashboardItemType.getClassPK())) {

			return true;
		}

		return false;
	}

	@Override
	public String getClassName() {
		return DDMStructure.class.getName();
	}

	@Override
	public long getClassPK() {
		return _ddmStructure.getStructureId();
	}

	@Override
	public String getFullLabel(Locale locale) {
		String groupName = StringPool.BLANK;

		try {
			groupName =
				StringPool.OPEN_PARENTHESIS +
					_group.getDescriptiveName(locale) +
						StringPool.CLOSE_PARENTHESIS;
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return getLabel(locale) + StringPool.SPACE + groupName;
	}

	@Override
	public String getLabel(Locale locale) {
		return _ddmStructure.getName(locale);
	}

	@Override
	public Date getModifiedDate() {
		return _ddmStructure.getModifiedDate();
	}

	@Override
	public long getUserId() {
		return _ddmStructure.getUserId();
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, getClassPK());

		return HashUtil.hash(hash, getClassName());
	}

	@Override
	public String toJSONString(Locale locale) {
		return JSONUtil.put(
			"className", getClassName()
		).put(
			"classPK", getClassPK()
		).put(
			"title", getFullLabel(locale)
		).toJSONString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureContentDashboardItemType.class);

	private final DDMStructure _ddmStructure;
	private final Group _group;

}