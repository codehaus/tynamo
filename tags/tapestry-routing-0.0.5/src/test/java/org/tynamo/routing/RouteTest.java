package org.tynamo.routing;

import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.routing.annotations.At;
import org.tynamo.routing.modules.TestsModule;
import org.tynamo.routing.pages.Home;
import org.tynamo.routing.pages.SimplePage;
import org.tynamo.routing.pages.SubFolderHome;
import org.tynamo.routing.pages.subpackage.SubPackageMain;
import org.tynamo.routing.pages.subpackage.SubPage;
import org.tynamo.routing.pages.subpackage.UnannotatedPage;
import org.tynamo.routing.services.RouterDispatcher;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RouteTest extends RoutingTestCase {

	@Override
	protected void addAdditionalModules(RegistryBuilder builder) {
		builder.add(TestsModule.class);
	}

	@Test
	public void regular_expressions() {

		String path = "/foo/52";

		String routeExpression = Route.buildExpression("/foo/{0}");

		Assert.assertEquals(routeExpression, "\\Q/foo/\\E([^/]+)");

		Pattern p = Pattern.compile(routeExpression);
		Matcher m = p.matcher(path);

		Assert.assertTrue(m.matches());
		Assert.assertEquals(m.group(1), "52");

		p = Pattern.compile(Route.buildExpression("/blah/{0}/{1}/bar"));
		m = p.matcher("/blah/54/foo/bar");

		Assert.assertTrue(m.matches());
		Assert.assertEquals(m.group(1), "54");
		Assert.assertEquals(m.group(2), "foo");
	}

	@Test
	public void decode_page_render_request() {
		Route route = routeFactory.create(SimplePage.class.getAnnotation(At.class).value(), SimplePage.class.getSimpleName());
		Request request = mockRequest();

		expect(request.getPath()).andReturn("/foo/45/bar/24").atLeastOnce();

		replay();

		PageRenderRequestParameters parameters = routeDecoder.decodePageRenderRequest(route, request);

		Assert.assertEquals(parameters.getLogicalPageName(), "SimplePage");
		Assert.assertEquals(parameters.getActivationContext().getCount(), 2);
		Assert.assertEquals(parameters.getActivationContext().get(Integer.class, 0).intValue(), 45);
		Assert.assertEquals(parameters.getActivationContext().get(Integer.class, 1).intValue(), 24);

	}

	@Test
	public void home() {
		testPageRenderLinkGeneration("/", Home.class, "/", "", 0);
	}

	@Test
	public void home_with_locale() {
		testPageRenderLinkGeneration("/fi/", Home.class, "/fi/", "", 0);
	}

	@Test
	public void home_with_context() {
		testPageRenderLinkGeneration("/myapp/", Home.class, "/", "/myapp", 0);
	}

	@Test
	public void home_with_context_and_locale() {
		testPageRenderLinkGeneration("/myapp/fi/", Home.class, "/fi/", "/myapp", 0);
	}

	@Test
	public void subfolder_listing() {
		testPageRenderLinkGeneration("/subfolder", SubFolderHome.class, "/subfolder/", "", 0);
	}

	@Test
	public void subfolder_listing_with_locale() {
		testPageRenderLinkGeneration("/fi/subfolder", SubFolderHome.class, "/fi/subfolder/", "", 0);
	}

/*
	@Test
	public void subfolder_listing_with_locale_path_encoding_off() {
		testPageRenderLinkGeneration("/fi/subfolder", SubFolderHome.class, "/fi/subfolder/", "", 0, false);
	}
*/

	@Test
	public void subfolder_listing_without_last_slash() {
		testPageRenderLinkGeneration("/subfolder", SubFolderHome.class, "/subfolder", "", 0);
	}

	@Test
	public void subfolder_listing_with_context() {
		testPageRenderLinkGeneration("/myapp/subfolder", SubFolderHome.class, "/subfolder/", "/myapp", 0);
	}

	@Test
	public void simplepage() {
		testPageRenderLinkGeneration("/foo/45/bar/24", SimplePage.class, "/foo/45/bar/24", "", 2);
	}

	@Test
	public void simplepage_with_context() {
		testPageRenderLinkGeneration("/myapp/foo/45/bar/24", SimplePage.class, "/foo/45/bar/24", "/myapp", 2);
	}

	@Test
	public void subpackage() {
		testPageRenderLinkGeneration("/subpackage/inventedpath", SubPage.class, "/subpackage/inventedpath", "", 0);
	}

	@Test
	public void subpackage_with_package_prefix() {
		testPageRenderLinkGeneration("/subpackage", SubPackageMain.class, "/subpackage", "", 0);
	}

	@Test
	public void link_to_unannotatedpage() {
		testPageRenderLinkGeneration("/not/annotated/parameter", UnannotatedPage.class, "/not/annotated/parameter", "", 1);
	}

	@Test
	public void order() throws IOException {

		Request request = mockRequest();
		expect(request.getPath()).andReturn("/subpackage/inventedpath").atLeastOnce();

		PageRenderRequestParameters expectedParameters = new PageRenderRequestParameters("subpackage/SubPageFirst", new EmptyEventContext(), false);
		ComponentRequestHandler requestHandler = mockComponentRequestHandler();
		requestHandler.handlePageRender(expectedParameters);

		RouterDispatcher routerDispatcher = new RouterDispatcher(requestHandler, routeSource);

		replay();

		routerDispatcher.dispatch(request, null);

		verify();
	}
}