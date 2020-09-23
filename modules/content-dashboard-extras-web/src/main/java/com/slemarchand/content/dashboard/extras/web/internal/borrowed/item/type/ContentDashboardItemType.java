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

import java.util.Date;
import java.util.Locale;

/**
 * @author Cristina González
 */
public interface ContentDashboardItemType<T> {

	public String getClassName();

	public long getClassPK();

	public String getFullLabel(Locale locale);

	public String getLabel(Locale locale);

	public Date getModifiedDate();

	public long getUserId();

	public String toJSONString(Locale locale);

}