package org.trails.component;

import org.apache.tapestry.IRequestCycle;
import org.trails.page.SearchPage;
import org.trails.page.TrailsPage;
import org.trails.page.PageResolver;

public abstract class SearchLink extends AbstractTypeNavigationLink {
    public static final String POSTFIX = "Search";
    public SearchLink()
    {
        super();
    }

    /**
     * Finds the search page for the type specified by the typeName
     * component parameter and forwards to it, setting the example model 
     * to a new instance.  This instance is use to build a query by
     * example.
     * @param cycle
     */
    public void click(IRequestCycle cycle)
    {
        PageResolver pageResolver = getPageResolver();
        SearchPage searchPage = (SearchPage) pageResolver.resolvePage(cycle, getTypeName(), TrailsPage.PageType.SEARCH);
        searchPage.setTypeName(getTypeName());
        cycle.activate(searchPage);
    }

    public String getLinkText() {
        String name = getClassDescriptor().getDisplayName();
        return generateLinkText(name, "org.trails.component.searchlink", "[TRAILS][ORG.TRAILS.COMPONENT.SEARCHLINK]");
    }
}