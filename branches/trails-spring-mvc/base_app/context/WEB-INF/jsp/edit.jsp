<%@ include file="/WEB-INF/jsp/includes/header.jsp" %>

Edit instance of <c:out value="${trailsModel.classDescriptor.pluralDisplayName}" />
<p>
<form name="edit" method="post" action="<c:url value="/saveInstance.htm"/>">
  <% // Loop over all rows. %>
  <c:forEach var="row" items="${trailsModel.rows}">
	  <input type="hidden" name="id" value="<c:out value="${row.identifierValue}"/>"/>  
	  <input type="hidden" name="type" value="<c:out value="${trailsModel.classDescriptor.type.name}"/>"/>
     <table border="1">
     <% // Loop over all rows. %>
     <c:forEach var="column" items="${row.columns}" begin="0">
  	   <c:if test="${!column.propertyDescriptor.hidden}">
         <tr>
           <td>
             <c:out value="${column.propertyDescriptor.displayName}"/>
           </td>
           <td>
        	   <c:choose>
        		   <c:when test="${column.propertyDescriptor.identifier}">
        		     <c:out value="${column.value}"/>
          	   </c:when>
          	   <c:otherwise>
          		  <trails:property property="${column}"/>
          	   </c:otherwise>
          	 </c:choose>
            </td>
          </tr>
        </c:if>
      </c:forEach>
    </table>
    <trails:hidden-properties objectDataDescriptor="${row}"/>
	</c:forEach>
  <p>
    <a href="javascript:document.forms['edit'].action = '<c:url value="/saveInstance.htm"/>';document.forms['edit'].submit();"><img title="Save" src="images/save.gif"></a>&nbsp;&nbsp;&nbsp;
    <a href="javascript:document.forms['edit'].action = '<c:url value="/deleteInstance.htm"/>';document.forms['edit'].submit();"><img title="Delete" src="images/delete.gif"></a>
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
