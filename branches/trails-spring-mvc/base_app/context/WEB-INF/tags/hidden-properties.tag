<%
// Tag file for rendering all hidden properties of a certain class descriptor.
// This property-tag is used if TrailsProperty.isHidden returns true.
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>

<%@ attribute name="objectDataDescriptor" required="true" type="org.trails.spring.mvc.ObjectDataDescriptor" %>


<%// loop over all columns, a column is a propertyName, propertyValue pair, which result in a table row. %>
<c:forEach var="column" items="${objectDataDescriptor.columns}">
 	<c:if test="${column.propertyDescriptor.hidden && !column.propertyDescriptor.identifier}">
	  <trails:property property="${column}"/>
  </c:if>
</c:forEach>