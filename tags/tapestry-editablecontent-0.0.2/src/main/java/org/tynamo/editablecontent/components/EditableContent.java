package org.tynamo.editablecontent.components;

import javax.servlet.http.HttpServletRequest;

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
import org.apache.tapestry5.corelib.components.Form;
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

/**
 * A component that displays html-formatted textual content and allows editing it inline in a ckeditor instance. The initial value is the
 * nested static html content if any.
 * 
 * @tapestrydoc
 */

@Import(stylesheet = "EditableContent.css")
public class EditableContent {

	// @Parameter(defaultPrefix = "literal")
	// private Integer cacheMaxAge;

	// @Inject
	// private WikiContentCache wikiContentCache;

	@Inject
	@Symbol(EditableContentSymbols.READONLY_BYDEFAULT)
	private boolean defaultReadOnly;

	public boolean isDefaultReadOnly() {
		return defaultReadOnly;
	}

	/**
	 * Set this flag to true to make the component read only. Uses the symbol {@link EditableContentSymbols.READONLY_BYDEFAULT} as the default
	 * value
	 * 
	 * @since 0.0.1
	 */
	@Parameter("defaultReadOnly")
	private boolean readOnly;

	@Inject
	@Symbol(EditableContentSymbols.DEFAULT_AUTHORROLE)
	private String defaultAuthorRole;

	public String getDefaultAuthorRole() {
		return defaultAuthorRole;
	}

	/**
	 * The role the current user needs to have to edit the contents, as determined by {@link HttpServletRequest#isUserInRole(String roleName)}
	 * . Uses the symbol {@link EditableContentSymbols.DEFAULT_AUTHORROLE} as the default value
	 * 
	 * @since 0.0.1
	 */
	@Parameter("defaultAuthorRole")
	private String authorRole;

	/**
	 * The contentId used associated with the persistent content. The default is the same as the component id. Use care when determinig the id
	 * since the ids need to be unique within the same persistent storage and renaming the id later won't migrate the contents under the old
	 * id.
	 * 
	 * @since 0.0.1
	 */
	@Parameter("defaultContentId")
	private String contentId;

	/**
	 * The max number of most recent revisions of the contents to be preserved. 0 for preserving all, negative for preserving no previous
	 * revisions.
	 * 
	 * @since 0.0.1
	 */
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

	@Inject
	private HttpServletRequest httpServletRequest;

	public boolean isEditable() {
		if (readOnly) return false;
		return httpServletRequest.isUserInRole(authorRole);
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

	@InjectComponent
	private Form contentEditorForm;

	public void onValidateFromContentEditorForm() {
		inEditMode = false;
		if (!httpServletRequest.isUserInRole(authorRole)) {
			contentEditorForm.recordError("Changes ignored, you are not authorized to modify this content");
			return;
		}

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
