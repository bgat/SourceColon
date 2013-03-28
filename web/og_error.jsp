<%--
CDDL HEADER START

The contents of this file are subject to the terms of the
Common Development and Distribution License (the "License").
You may not use this file except in compliance with the License.

See LICENSE.txt included in this distribution for the specific
language governing permissions and limitations under the License.

When distributing Covered Code, include this CDDL HEADER in each
file and include the License file at LICENSE.txt.
If applicable, add the following below this CDDL HEADER, with the
fields enclosed by brackets "[]" replaced with your own identifying
information: Portions Copyright [yyyy] [name of copyright owner]

CDDL HEADER END

Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
Portions Copyright 2011 Jens Elkner.

--%>
<%@ page session="false" isErrorPage="true" import="
java.io.PrintWriter,
                                                    java.io.StringWriter,
                                                    org.watermint.sourcecolon.org.opensolaris.opengrok.web.Util"
    %>
<%
  /* ---------------------- error.jsp start --------------------- */
  {
    cfg = PageConfig.get(request);
    cfg.setTitle("Error!");

    String configError = "";
    if (cfg.getSourceRootPath().isEmpty()) {
      configError = "CONFIGURATION parameter has not been configured in " + "web.xml! Please configure your webapp.";
    } else if (!cfg.getEnv().getSourceRootFile().isDirectory()) {
      configError = "The source root specified in your configuration does " + "not point to a valid directory! Please configure your webapp.";
    }
%>
<%@ include file="og_header.jspf" %>
<div class="container-fluid">
  <div class="row-fluid">
    <div class="span3">
      <%@ include file="og_menu.jspf" %>
    </div>
    <div class="span9">
      <h1 class="error">There was an error!</h1>

      <p class="error"><%= configError %>
      </p>
      <%
        if (exception != null) {
      %>
      <p class="error"><%= exception.getMessage() %>
      </p>
        <pre><%
          StringWriter wrt = new StringWriter();
          PrintWriter prt = new PrintWriter(wrt);
          exception.printStackTrace(prt);
          prt.close();
          out.write(Util.htmlize(wrt.toString()));
        %></pre>
      <%
      } else {
      %><p class="error">Unknown Error</p>
      <%
        }
      %>
    </div>
  </div>
</div>
<% } %>
<%@ include file="og_foot.jspf" %>