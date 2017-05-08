<%--
  * Copyright 2017 Rod MacKenzie.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="admin" tagdir="/WEB-INF/tags/admin" %>
<%@ include file="/include-internal.jsp" %>

<div class="section noMargin">
    <h2 class="noBorder">Gradle Build Cache</h2>

    <%--@elvariable id="statistics" type="com.hazelcast.monitor.LocalMapStats"--%>
    <jsp:useBean id="lastAccessTime" class="java.util.Date" />
    <jsp:setProperty name="lastAccessTime" property="time" value="${statistics.lastAccessTime}" />
    <jsp:useBean id="lastUpdateTime" class="java.util.Date" />
    <jsp:setProperty name="lastUpdateTime" property="time" value="${statistics.lastUpdateTime}" />
    <jsp:useBean id="creationTime" class="java.util.Date" />
    <jsp:setProperty name="creationTime" property="time" value="${statistics.creationTime}" />

    <p style="margin-top: 2em">Statistics</p>
    <table class="parametersTable" style="width: 100%">
        <tr>
            <th style="width: 45%">Name</th>
            <th>Value</th>
        </tr>
        <tr>
            <td>Hits</td>
            <td><c:out value="${statistics.hits}"/></td>
        </tr>
        <tr>
            <td>Heap Cost</td>
            <td><c:out value="${statistics.heapCost}"/></td>
        </tr>
        <tr>
            <td>Last Access Time</td>
            <td><fmt:formatDate value="${lastAccessTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <td>Last Update Time</td>
            <td><fmt:formatDate value="${lastUpdateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <td>Creation Time</td>
            <td><fmt:formatDate value="${creationTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
    </table>
</div>
