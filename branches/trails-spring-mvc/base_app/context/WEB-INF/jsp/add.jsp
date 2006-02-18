<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>

Adding instance of <c:out value="${trailsModel.classDescriptor.pluralDisplayName}" />
<p>
<form name="add" method="post" action="<c:url value="/saveInstance.htm"/>">
  <% // Loop over all rows. %>
  <c:forEach var="row" items="${trailsModel.rows}">
	  <input type="hidden" name="type" value="<c:out value="${trailsModel.classDescriptor.type.name}"/>"/>
     <table border="1">
     <% // Loop over all rows. %>
     <c:forEach var="column" items="${row.columns}" begin="0">
  	   <c:if test="${!column.propertyDescriptor.hidden && !column.propertyDescriptor.identifier}">
         <tr>
           <td>
             <c:out value="${column.propertyDescriptor.displayName}"/>
           </td>
           <td>
        		  <trails:property property="${column}" action="add"/>
           </td>
          </tr>
        </c:if>
      </c:forEach>
    </table>
    <trails:hidden-properties objectDataDescriptor="${row}"/>
	</c:forEach>
  <p>
    <a href="javascript:document.forms['add'].action = '<c:url value="/saveInstance.htm"/>';document.forms['add'].submit();"><img title="Save" src="images/save.gif"></a>&nbsp;&nbsp;&nbsp;
  </p>  
</form>
<br>
<br>
<div id="errors">
  <spring:bind path="trailsModel.*">
    <c:forEach items="${status.errorMessages}" var="error">
    	<c:out value="${error}"/><br>
  	</c:forEach>
	</spring:bind>
</div>
<%@ include file="/WEB-INF/jsp/includes/end.jsp" %>
