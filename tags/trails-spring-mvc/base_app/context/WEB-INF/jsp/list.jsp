<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>

    <a href="<c:url value="/prepareToSearchInstances.htm">
               <c:param name="type" value="${trailsModel.classDescriptor.type.name}"/>
             </c:url>">Search</a> 
    <c:out value="${trailsModel.classDescriptor.pluralDisplayName}" />

       <a href="<c:url value="/prepareToEditOrAddAnInstance.htm">
               <c:param name="type" value="${trailsModel.classDescriptor.type.name}"/>
             </c:url>">Add</a> 
    <c:out value="${trailsModel.classDescriptor.pluralDisplayName}" />

<p>
  List of <c:out value="${trailsModel.classDescriptor.pluralDisplayName}" />
<p>

<table border="1">

<!-- Create the column headers. -->
<tr>
	<% //Loop over all column names. Only show the ones NOT hidden and summary. %>
	<c:forEach var="columnName" items="${trailsModel.columnNames}">
	  <c:if test="${!columnName.hidden && columnName.summary}">
			<th>
				<c:out value="${columnName.displayName}"/>
			</th>
	  </c:if>
	</c:forEach>
	<th>
		&nbsp;
	</th>
</tr>

  <% //Loop over all rows. %>
  <c:forEach var="row" items="${trailsModel.rows}">
		<tr>

			<% //Loop over all column. Only show the ones NOT hidden and summary. %>
			<c:forEach var="column" items="${row.columns}">
				<c:if test="${!column.propertyDescriptor.hidden && column.propertyDescriptor.summary}">
					<td valign="top">
						<trails:property property="${column}" 
										 readOnly="true" />
					</td>
				</c:if>
			</c:forEach>
			<td valign="top">
				<a href="<c:url value="/deleteInstance.htm">
	      					<c:param name="type" value="${trailsModel.classDescriptor.type.name}"/>
        					<c:param name="id" value="${row.identifierValue}"/>
      					</c:url>">Delete</a>&nbsp;
				<a href="<c:url value="/prepareToEditOrAddAnInstance.htm">
	      					<c:param name="type" value="${trailsModel.classDescriptor.type.name}"/>
        					<c:param name="id" value="${row.identifierValue}"/>
       					</c:url>">Edit</a>
			</td>
		</tr>
  </c:forEach>
</table>

<trails:paging classDescriptor="${trailsModel.classDescriptor}" selectedPageNumber="${trailsModel.currentPageNumber}" totalNumberOfPages="${trailsModel.totalNumberOfPages}"/>

<%@ include file="/WEB-INF/jsp/includes/end.jsp" %>
