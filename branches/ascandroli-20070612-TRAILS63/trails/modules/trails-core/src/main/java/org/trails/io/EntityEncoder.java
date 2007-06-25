package org.trails.io;

import org.apache.tapestry.Tapestry;
import org.apache.tapestry.engine.ServiceEncoder;
import org.apache.tapestry.engine.ServiceEncoding;
import org.apache.tapestry.services.ServiceConstants;


public class EntityEncoder implements ServiceEncoder {

    String _pageName;
    String _url;
    String _prefix = "HIBRN8:Dorg.trails.demo.";

    public void encode(ServiceEncoding encoding) {
        if (!isExternalService(encoding))
            return;

        String pageName = encoding.getParameterValue(ServiceConstants.PAGE);

        if (!pageName.equals(_pageName))
            return;

        StringBuilder builder = new StringBuilder(_url);

        String[] params = encoding.getParameterValues(ServiceConstants.PARAMETER);

        // params will not be null; in fact, pretty sure it will consist
        // of just one element (an integer).

        for (String param : params) {
            builder.append("/");
            builder.append(compress(param));
        }

        encoding.setServletPath(builder.toString());

        encoding.setParameterValue(ServiceConstants.SERVICE, null);
        encoding.setParameterValue(ServiceConstants.PAGE, null);
        encoding.setParameterValue(ServiceConstants.PARAMETER, null);
    }

    private String compress(String param) {
        return param.substring(_prefix.length());
    }

    private String[] uncompress(String param) {
/*
        String[] result = new String[param.length];

        for (int i = 0; i < param.length; i++) {
            result[i] = _prefix + param[i];
        }

        return result;
*/
        return new String[]{_prefix + param};
    }

    private boolean isExternalService(ServiceEncoding encoding) {
        String service = encoding.getParameterValue(ServiceConstants.SERVICE);

        return service.equals(Tapestry.EXTERNAL_SERVICE);
    }

    public void decode(ServiceEncoding encoding) {
        String servletPath = encoding.getServletPath();

        if (!servletPath.equals(_url))
            return;

        String pathInfo = encoding.getPathInfo();

//        String[] params = TapestryUtils.split(pathInfo.substring(1), '/');
        String params = pathInfo.substring(1);

        encoding.setParameterValue(ServiceConstants.SERVICE, Tapestry.EXTERNAL_SERVICE);
        encoding.setParameterValue(ServiceConstants.PAGE, _pageName);
        encoding.setParameterValues(ServiceConstants.PARAMETER, uncompress(params));
    }

    public void setPageName(String pageName) {
        this._pageName = pageName;
    }

    public void setUrl(String url) {
        this._url = url;
    }

    public void setPrefix(String prefix) {
        this._prefix = prefix;
    }
}
