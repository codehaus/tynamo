<%
// Tag file for rendering a numeric property.
// This property-tag is used if TrailsProperty.isNumeric returns true.
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>

<%@ attribute name="property" required="true" type="java.lang.Object" %>
<%@ attribute name="readOnly" %>
<c:choose>
	<c:when test='${property.propertyDescriptor.readOnly || readOnly == "true"}'>
		<fmt:formatNumber value="${property.value}" pattern="${property.propertyDescriptor.format}"/>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${!property.valueInvalid}">
				<input name="<c:out value="${property.propertyDescriptor.name}"/>" value="<fmt:formatNumber value="${property.value}" pattern="${property.propertyDescriptor.format}"/>">
			</c:when>
			<c:when test="${property.propertyDescriptor.hidden}">
				<input type="hidden" name="<c:out value="${property.propertyDescriptor.name}"/>" value="<c:out value="${property.value}"/>">				
			</c:when>
			<c:otherwise>
				<input name="<c:out value="${property.propertyDescriptor.name}"/>" value="<c:out value="${property.value}"/>">
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>