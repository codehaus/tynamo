package org.tynamo.editablecontent.internal;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.jpa.annotations.CommitAfter;
import org.tynamo.editablecontent.EditableContentSymbols;
import org.tynamo.editablecontent.entities.RevisionedContent;
import org.tynamo.editablecontent.entities.TextualContent;
import org.tynamo.editablecontent.services.EditableContentStorage;

public class EditableContentStorageImpl implements EditableContentStorage {
	private EntityManager entityManager;
	private ThreadLocale threadLocale;

	public EditableContentStorageImpl(EntityManager entityManager, ThreadLocale persistentLocale,
		@Inject @Symbol(EditableContentSymbols.LOCALIZED_CONTENT) boolean localizedContent) {
		this.entityManager = entityManager;
		this.threadLocale = localizedContent ? persistentLocale : null;
	}

	private String localizeContentId(final String contentId) {
		if (threadLocale == null || threadLocale.getLocale() == null) return contentId;
		return contentId + "_" + threadLocale.getLocale().toString();
	}

	@Override
	public String getHtmlContent(String contentId) {
		// TODO handle cache
		TextualContent content = getTextualContent(contentId);
		return content == null ? null : content.getValue();
	}

	@Override
	public boolean contains(String contentId) {
		contentId = localizeContentId(contentId);
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		Root<?> from = cq.from(TextualContent.class);
		cq.select(qb.count(from));
		cq.where(qb.equal(from.get("id"), contentId));
		return entityManager.createQuery(cq).getSingleResult() > 0 ? true : false;
	}

	void createRevision(TextualContent content, int maxHistory) {
		RevisionedContent revision = new RevisionedContent(content);
		entityManager.persist(revision);
		// delete revisions earlier than max allowed. If maxHistory is 0, keep all revisions
		if (maxHistory > 0) {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<RevisionedContent> query = builder.createQuery(RevisionedContent.class);
			// typically, we should be deleting only max one at a time, but this still shouldn't
			// be performing that badly as the entities are lazily fetched
			Root<?> from = query.from(RevisionedContent.class);
			query.where(builder.equal(from.get("id"), content.getId()));
			query.orderBy(builder.desc(from.get("revision")));
			List<RevisionedContent> revisions = entityManager.createQuery(query).getResultList();
			int size = revisions.size();
			for (int i = 0; size - i > maxHistory; i++)
				entityManager.remove(revisions.get(i));
		}
	}

	@Override
	@CommitAfter
	public String updateContent(String contentId, String contentValue, int maxHistory) {
		contentId = localizeContentId(contentId);
		TextualContent content = getTextualContent(contentId);
		if (content == null) {
			content = new TextualContent();
			content.setId(contentId);
		} else {
			// TODO fetch current version and store the older version as a previous version to revisionedContent
			if (maxHistory >= 0) createRevision(content, maxHistory);
		}
		content.setValue(contentValue);
		content.setLastModified(new Date());

		// TODO persist previous version
		entityManager.persist(content);
		return null;
	}

	@Override
	public TextualContent getTextualContent(String contentId) {
		return entityManager.find(TextualContent.class, localizeContentId(contentId));
	}

}
