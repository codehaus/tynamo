package org.tynamo.routing.services;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.tynamo.routing.Route;

import java.util.List;
import java.util.Map;

public class RouteProviderImpl implements RouteProvider {

	private final List<Route> routes;
	private final Map<String, Route> routeMap = CollectionFactory.newConcurrentMap();

	public RouteProviderImpl(List<Route> routes) {
		this.routes = routes;
		for (Route route : routes) {
			routeMap.put(route.getCanonicalizedPageName(), route);
		}
	}

	@Override
	public Route getRoute(String canonicalizedPageName) {
		return routeMap.get(canonicalizedPageName);
	}

	@Override
	public List<Route> getRoutes() {
		return routes;
	}
}
