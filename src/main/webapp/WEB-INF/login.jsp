<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (errorMessage != null) {
        session.removeAttribute("errorMessage");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family: Arial; margin: 40px; }
        .error { color: red; margin: 10px 0; }
        .form-group { margin: 15px 0; }
        label { display: block; margin-bottom: 5px; }
        input { padding: 8px; width: 200px; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; }
    </style>
</head>
<body>
<h2>Авторизация</h2>

<% if (errorMessage != null) { %>
<div class="error"><%= errorMessage %></div>
<% } %>

<form method="post" action="login">
    <div class="form-group">
        <label>Login:</label>
        <label>
            <input name="username" type="text" required>
        </label>
    </div>
    <div class="form-group">
        <label>Password:</label>
        <label>
            <input name="password" type="password" required>
        </label>
    </div>
    <button type="submit">Войти</button>
</form>
</body>
</html>
