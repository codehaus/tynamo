<% 
// Tag file for rendering a lonk
// This property-tag is used if TrailsProperty.isCollection returns true.
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>

<%@ attribute name="className" type="java.lang.Object"%>
<%@ attribute name="identifierValue" type="java.lang.Object" %>
<%@ attribute name="linkName" type="java.lang.Object" %>
<%@ attribute name="url" required="true" %>

<a href="<c:url value="${url}">
		<c:param name="type" value="${className}"/>
		<c:param name="id" value="${identifierValue}"/></c:url>">
		<c:out value="${linkName}"/>
</a> 