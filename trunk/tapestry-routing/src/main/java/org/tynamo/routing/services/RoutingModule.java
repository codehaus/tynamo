package org.tynamo.routing.services;

import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ClassNameLocator;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;
import org.tynamo.routing.Route;
import org.tynamo.routing.RoutingSymbols;
import org.tynamo.routing.annotations.At;

public class RoutingModule {

	public static void bind(ServiceBinder binder) {

		binder.bind(AnnotatedPagesManager.class);
		binder.bind(RouteSource.class);
		binder.bind(RouteFactory.class);

	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("RouterLinkTransformer", RouterLinkTransformer.class);
	}

	public static void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration,
	                                              @Autobuild RouterDispatcher dispatcher) {
		configuration.add(RouterDispatcher.class.getSimpleName(), dispatcher, "after:PageRender");
	}

	@Contribute(RouteSource.class)
	public static void loadRoutesFromAnnotatedPages(OrderedConfiguration<Route> configuration,
	                                                AnnotatedPagesManager manager,
	                                                RouteFactory routeFactory) {

		for (Class clazz : manager.getPages()) {
			if (clazz.isAnnotationPresent(At.class)) {
				At ann = (At) clazz.getAnnotation(At.class);
				if (ann != null) {

					String pathExpression = ann.value();
					Route route = routeFactory.create(pathExpression, clazz);
					configuration.add(route.getCanonicalizedPageName().toLowerCase(), route, ann.order());

				}
			}
		}
	}

	@Contribute(AnnotatedPagesManager.class)
	public static void searchForAnnotatedPages(Configuration<Class> configuration,
	                                           @Symbol(InternalSymbols.APP_PACKAGE_PATH) String appPackagePath,
	                                           @Symbol(RoutingSymbols.DISABLE_AUTODISCOVERY) Boolean preventScan,
	                                           ClassNameLocator classNameLocator) {

		if (!preventScan) {
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

			for (String className : classNameLocator.locateClassNames(appPackagePath)) {
				try {
					Class entityClass = contextClassLoader.loadClass(className);

					if (entityClass.isAnnotationPresent(At.class)) {
						configuration.add(entityClass);
					}
				} catch (ClassNotFoundException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	@Contribute(SymbolProvider.class)
	@FactoryDefaults
	public static void provideFactoryDefaults(final MappedConfiguration<String, String> configuration) {
		configuration.add(RoutingSymbols.DISABLE_AUTODISCOVERY, "false");
	}

}
