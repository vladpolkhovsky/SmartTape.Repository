<!DOCTYPE html>
<html>
    <head>
        <%@ page contentType="text/html; charset=UTF-8" %>
        <title>Index.html</title>
        <script>
            redirectURL = "/nl-feed";
            redirect = function () {
                window.location.replace(redirectURL);
                console.log("click");
            }
            redirect();
        </script>
    </head>
    <body>
        <h2>Index.html проекта Умная Лента.</h2>
        <p>
            Это техническая стараница.<br>
            Если перенаправления не произошло автоматически <a href="#" onclick="redirect()">Нажмите сюда</a>.
        </p>
    </body>
</html>
