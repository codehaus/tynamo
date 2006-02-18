<%
// Tag file for rendering a date property.
// This property-tag is used if TrailsProperty.isDate returns true.
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>

<%@ attribute name="property" required="true" type="java.lang.Object" %>
<%@ attribute name="readOnly" %>

<c:choose>
	<c:when test='${property.propertyDescriptor.readOnly || readOnly == "true"}'>
		<fmt:formatDate value="${property.value}" pattern="${property.propertyDescriptor.format}"/>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${!property.valueInvalid}">
				<input name="<c:out value="${property.propertyDescriptor.name}"/>" value="<fmt:formatDate value="${property.value}" pattern="${property.propertyDescriptor.format}"/>">
			</c:when>
			<c:when test="${property.propertyDescriptor.hidden}">
				<input type="hidden" name="<c:out value="${property.propertyDescriptor.name}"/>" value="<c:out value="${property.value}"/>">				
			</c:when>			
			<c:otherwise>
				<% // show the invalid value. %>
				<input name="<c:out value="${property.propertyDescriptor.name}"/>" value="<c:out value="${property.value}"/>">	
			</c:otherwise>
		</c:choose>
		<c:if test="${!property.propertyDescriptor.hidden}">
			<% // show the date picker. %>		
			<input type=button value="select" onclick="displayDatePicker('<c:out value="${property.propertyDescriptor.name}"/>', false, 'dmy', '-');">
	  </c:if>
	</c:otherwise>
</c:choose>
