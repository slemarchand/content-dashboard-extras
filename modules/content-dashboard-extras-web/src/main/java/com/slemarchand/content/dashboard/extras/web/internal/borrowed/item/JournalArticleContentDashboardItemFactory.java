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

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.info.display.url.provider.InfoEditURLProviderTracker;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.type.ContentDashboardItemTypeFactory;
import com.slemarchand.content.dashboard.extras.web.internal.borrowed.item.type.ContentDashboardItemTypeFactoryTracker;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemFactory.class)
public class JournalArticleContentDashboardItemFactory
	implements ContentDashboardItemFactory<JournalArticle> {

	@Override
	public ContentDashboardItem<JournalArticle> create(long classPK)
		throws PortalException {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
				classPK, WorkflowConstants.STATUS_ANY, false);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		Optional<ContentDashboardItemTypeFactory>
			contentDashboardItemTypeFactoryOptional =
				_contentDashboardItemTypeFactoryTracker.
					getContentDashboardItemTypeFactoryOptional(
						DDMStructure.class.getName());

		ContentDashboardItemTypeFactory contentDashboardItemTypeFactory =
			contentDashboardItemTypeFactoryOptional.orElseThrow(
				NoSuchModelException::new);

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		JournalArticle latestApprovedJournalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				classPK, WorkflowConstants.STATUS_APPROVED);

		return new JournalArticleContentDashboardItem(
			assetEntry.getCategories(), assetEntry.getTags(),
			contentDashboardItemTypeFactory.create(
				ddmStructure.getStructureId()),
			_groupLocalService.fetchGroup(journalArticle.getGroupId()),
			_infoEditURLProviderTracker.getInfoEditURLProvider(
				JournalArticle.class.getName()),
			journalArticle, _language, latestApprovedJournalArticle,
			_modelResourcePermission,
			_userLocalService.fetchUser(journalArticle.getUserId()));
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ContentDashboardItemTypeFactoryTracker
		_contentDashboardItemTypeFactoryTracker;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoEditURLProviderTracker _infoEditURLProviderTracker;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle> _modelResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

}