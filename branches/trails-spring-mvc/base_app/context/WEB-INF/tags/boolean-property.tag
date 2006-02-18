<%
// Tag file for rendering a boolean property.
// This property-tag is used if TrailsProperty.isBoolean returns true.
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>

<%@ attribute name="property" required="true" type="java.lang.Object" %>
<%@ attribute name="readOnly" %>
<c:choose>
	<c:when test='${property.propertyDescriptor.readOnly || readOnly == "true"}'>
		<c:choose>
			<c:when test="${property.value}">
				On
			</c:when>
			<c:otherwise>
				Off
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${property.propertyDescriptor.hidden}">
		<input type="hidden" name="<c:out value="${property.propertyDescriptor.name}"/>" value="<c:out value="${property.value}"/>">				
	</c:when>	
	<c:otherwise>
		<input type="radio" name="<c:out value="${property.propertyDescriptor.name}"/>" value="true" <c:if test="${property.value}">checked</c:if> />On
		<input type="radio" name="<c:out value="${property.propertyDescriptor.name}"/>" value="false" <c:if test="${!property.value}">checked</c:if>/>Off	                 
	</c:otherwise>
</c:choose>