package org.tynamo.editablecontent.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValidationDecorator;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.AfterRenderBody;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageReset;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.DefaultValidationDecorator;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.internal.services.RenderQueueImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.tynamo.editablecontent.EditableContentSymbols;
import org.tynamo.editablecontent.entities.TextualContent;
import org.tynamo.editablecontent.services.EditableContentStorage;

@Import(stylesheet = "EditableContent.css")
public class EditableContent {

	/**
	 * Message will be displayed if page is not found.
	 */
	@Parameter("defaultMessage")
	private String message;

	public String getDefaultMessage() {
		return "No content available. Check again later";
	}

	// @Parameter(defaultPrefix = "literal")
	// private Integer cacheMaxAge;

	// @Inject
	// private WikiContentCache wikiContentCache;

	@Inject
	@Symbol(EditableContentSymbols.READONLY_BYDEFAULT)
	public boolean defaultReadOnly;

	public boolean isDefaultReadyOnly() {
		return defaultReadOnly;
	}

	@Parameter("defaultReadOnly")
	private boolean readOnly;

	@Parameter("defaultContentId")
	private String contentId;

	@Parameter(defaultPrefix = "literal", value = "100")
	private int maxHistory;

	@Property
	private String contentValue;

	public String getDefaultContentId() {
		return componentResources.getId();
	}

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Request request;

	@Inject
	private EditableContentStorage contentStorage;

	// @BeforeRenderTemplate
	// public Object render(MarkupWriter writer) {
	// // if (request.getParameter("nc") != null) cacheMaxAge = 0;
	// // String content = wikiContentCache.getRenderedPage(page, cacheMaxAge);
	// // if (content == null) writer.write(message);
	// // else writer.writeRaw(content);
	// // return false;
	//
	// }

	@BeforeRenderBody
	public Object dontRenderContent(MarkupWriter writer) {
		// don't conditionally render the content here, otherwise the possible static body will be rendered twice
		return false;
	}

	@PageReset
	public void resetToReadMode() {
		componentResources.discardPersistentFieldChanges();
	}

	@AfterRenderBody
	public Object renderContent(MarkupWriter writer) {
		TextualContent content = contentStorage.getTextualContent(contentId);
		if (content == null) return componentResources.getBody();
		writer.writeRaw(content.getValue());
		return true;
	}

	@Persist(PersistenceConstants.CLIENT)
	private Long versionForEdit;

	@Persist(PersistenceConstants.SESSION)
	private boolean inEditMode;

	public boolean isEdited() {
		if (request.getSession(false) == null) return false;
		return inEditMode;
	}

	public boolean isEditable() {
		if (readOnly) return false;
		if (request.getSession(false) == null) return false;
		// TODO check role-based authorization
		return true;
	}

	@InjectComponent
	Zone contentZone;

	public Object onActionFromCancelLink() {
		inEditMode = false;
		return request.isXHR() ? contentZone.getBody() : null;
	}

	public Object onActionFromEditLink() {
		inEditMode = true;

		TextualContent content = contentStorage.getTextualContent(contentId);
		if (content == null) {
			versionForEdit = null;
			contentValue = renderMarkup((RenderCommand) componentResources.getBody());
			if (contentValue == null) contentValue = "";
		} else {
			versionForEdit = content.getVersion();
			contentValue = content.getValue();
		}

		return request.isXHR() ? contentZone.getBody() : null;
	}

	@Inject
	private AlertManager alertManager;

	public void onValidateFromContentEditorForm() {
		inEditMode = false;
		if (versionForEdit != null) {
			TextualContent content = contentStorage.getTextualContent(contentId);
			if (versionForEdit != content.getVersion())
				alertManager.warn(content.getAuthor()
					+ " had modified contents after you started editing, those changes were erased!");
		}
		contentStorage.updateContent(contentId, contentValue, maxHistory);
	}

	Object onSuccess() {
		TextualContent content = contentStorage.getTextualContent(contentId);
		versionForEdit = content.getVersion();
		return request.isXHR() ? contentZone.getBody() : null;
	}

	Object onFailure() {
		return request.isXHR() ? contentZone.getBody() : null;
	}

	@Inject
	private Environment environment;
	@Inject
	private AssetSource assetSource;

	@Inject
	private Logger logger;

	private String renderMarkup(RenderCommand renderCommand) {
		MarkupWriter markupWriter = new MarkupWriterImpl();

		// validation track
		ValidationDecorator decorator = new DefaultValidationDecorator(environment,
			assetSource.getExpandedAsset("${tapestry.spacer-image}"), markupWriter);
		environment.push(ValidationDecorator.class, decorator);
		RenderQueueImpl renderQueue = new RenderQueueImpl(logger);
		renderQueue.push(renderCommand);
		renderQueue.run(markupWriter);

		environment.pop(ValidationDecorator.class);
		return markupWriter.toString().replaceAll("^\\n+", "").replaceAll("\\n+$", "");
	}
}
