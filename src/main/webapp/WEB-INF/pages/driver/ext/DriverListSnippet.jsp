<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="panel panel-default">
	<div class="panel-heading">
		<h1>List of drivers</h1>
	</div>
	<div class="panel-body">

		<%-- Edit priveleges --%>
		<sec:authorize access="hasRole('ROLE_MANAGER')">
			<div class="panel-footer">
				<a href="${pageContext.request.contextPath}/driver/new" role="button"
				   class="btn btn-default btn-large btn-block"><span
						class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add new driver</a>
			</div>
		</sec:authorize>

		<table class="table table-striped">
			<thead>
				<tr>
					<th class="text-center">Personal number</th>
					<th>First name</th>
					<th>Last name</th>
					<th>Status</th>
					<th>Current city</th>
					<th>Current truck</th>
					<th>Current order</th>
					<th>Worked in this month <small>hours</small></th>
					<th class="text-center">View</th>

					<%-- Edit priveleges --%>

					<sec:authorize access="hasRole('ROLE_MANAGER')">
						<th class="text-center">Edit</th>
						<th class="text-center">Delete</th>
					</sec:authorize>

				</tr>
			</thead>
			<tbody>

				<c:forEach items="${drivers}" var="driver">
					<tr>

						<td class="text-center"><c:out value="${driver.personalNumber}" /></td>
						<td><c:out value="${driver.firstName}" /></td>
						<td><c:out value="${driver.lastName}" /></td>
						<td><c:out value="${driver.driverStatus}" /></td>
						<td><c:out value="${cities[driver.currentCityId].name}" /></td>
						<td>
						    <c:choose>
								<c:when test="${!empty driver.currentTruckNumber}">${driver.currentTruckNumber}</c:when>
								<c:otherwise>Not assigned</c:otherwise>
							</c:choose>
						</td>

						<td><c:if test="${empty driver.currentOrderId}">Not assigned</c:if>

							<sec:authorize access="hasRole('ROLE_MANAGER')">
								<a href="${pageContext.request.contextPath}/order/${driver.currentOrderId}">
								    ${driver.currentOrderId}
								</a>
							</sec:authorize>
							<sec:authorize access="hasRole('ROLE_DRIVER')">
			                       ${driver.currentOrderId}
			                </sec:authorize>
						</td>

						<td>
						  	<fmt:formatNumber type="number" value="${driver.workingHoursThisMonth}"/>
						</td>

						<td class="text-center"><a
							href="${pageContext.request.contextPath}/driver/${driver.driverId}"><span
								class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a>
						</td>

						<%-- Edit priveleges --%>
						<sec:authorize access="hasRole('ROLE_MANAGER')">
							<td class="text-center">
								<c:choose>
								<c:when test="${empty driver.currentOrderId}">
								 <a href="${pageContext.request.contextPath}/driver/${driver.driverId}/edit"><span
									class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
								 </a>
								</c:when>
									<c:otherwise>
						          <span
										  class="glyphicon glyphicon-pencil disabled-color" aria-hidden="true"></span>
									</c:otherwise>
								</c:choose>
							</td>

							<td class="text-center">
								<c:choose>
								<c:when test="${empty driver.currentOrderId}">
									<span onclick="deleteDriver(this, ${driver.driverId})"
									class="glyphicon glyphicon-remove red-on-hover" aria-hidden="true"></span>
								</c:when>
								<c:otherwise>
									<span class="glyphicon glyphicon-remove red-on-hover disabled-color" aria-hidden="true"></span>
								</c:otherwise>
								</c:choose>
							</td>
						</sec:authorize>

					</tr>
				</c:forEach>

			</tbody>
		</table>

	</div>

</div>