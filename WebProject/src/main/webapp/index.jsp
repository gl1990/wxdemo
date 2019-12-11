<%-- Created by IntelliJ IDEA. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.codecoord.com" prefix="c" %>
<%@ taglib uri="http://www.codecoord.com/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.codecoord.com/fn" prefix="fn" %>
<c:set var="basePath" value="${pageContext.request.contextPath }"></c:set>
<%@ page import="accessToken.AccessTokenInfo"%>
<html>
  <head>
    <title></title>
  </head>
  <body>
  		<%-- 
  <form action="" method="get">
  <button οnclick="submit">测试微信公众号</button>
	</form>
  --%>
      <form action="${pageContext.request.contextPath}/accessTokenServlet" method="get">
      	<button οnclick="submit">获取access_token ${pageContext.request.contextPath}</button>
      </form>
      <hr/>
      <c:if test="AccessTokenInfo.accessToken != null">
          access_token为：<%=AccessTokenInfo.accessToken.getTokenName()%>
      </c:if>
  </body>
</html>

