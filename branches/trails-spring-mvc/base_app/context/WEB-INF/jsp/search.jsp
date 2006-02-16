<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>

Search instance of <c:out value="${trailsModel.classDescriptor.pluralDisplayName}" />
<p>

<form name="form" method="post" action="<c:url value="/searchInstances.htm"/>">
<table border="1">

<!-- Loop over all rows. -->
<c:forEach var="row" items="${trailsModel.rows}">

	<input type="hidden" name="type" value="<c:out value="${trailsModel.classDescriptor.type.name}"/>"/>

  	<!-- Loop over all columns. -->
	<c:forEach var="column" items="${row.columns}">
		<c:if test="${column.propertyDescriptor.searchable}">
      <tr>
        <td>
         	<c:out value="${column.propertyDescriptor.displayName}"/>
        </td>
        <td>
        	<trails:property property="${column}" action="search"/>
      	</td>
    	</tr>
		</c:if>
  </c:forEach>
</c:forEach>
</table>
<input type="submit" value="search">
</form>

<%@ include file="/WEB-INF/jsp/includes/end.jsp" %>

