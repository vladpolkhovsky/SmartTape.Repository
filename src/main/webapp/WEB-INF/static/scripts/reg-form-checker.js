const doRegButton = document.getElementById("button-submit");

const password_1 = document.getElementById("password-1");
const password_2 = document.getElementById("password-2");
const user_name = document.getElementById("user-name");

const span = document.getElementById("password-checker-span");
const l_span = document.getElementById("user-name-checker-span");

function checkPasswords() {
    if (password_1.value !== password_2.value || password_1.value === "" || password_2.value === "") {
        password_1.style.borderColor = password_2.style.borderColor = "#6E00B3";
        password_1.style.borderWidth = password_2.style.borderWidth = "4px";
        doRegButton.disabled = true;
        if (password_1.value != "" || password_2.value != "") {
            span.innerText = "Пароли не совпадают";
        } else {
            span.innerText = "";
        }
    }
    else {
        span.innerText = "";
        password_1.style.borderColor = password_2.style.borderColor = "#B3B3B3";
        password_1.style.borderWidth = password_2.style.borderWidth = "2px";
        doRegButton.disabled = false
    }
}

function checkUserName(userName) {
    let xhr = new XMLHttpRequest();
    let url = './api/user-name-checker?user-name='+userName;
    console.log(url);
    xhr.open('GET', url, true);
    xhr.send();
    xhr.onload = function () {
        let result = JSON.parse(xhr.responseText);
        console.log(result);
        if (result.already_exists)
            l_span.innerText = "Имя уже занято";
        else
            l_span.innerText = "";
    }
}

password_1.addEventListener("keyup", checkPasswords);
password_2.addEventListener("keyup", checkPasswords);
user_name.addEventListener("focusout", (x) => {
    return checkUserName(user_name.value);
});

doRegButton.addEventListener("click", (event) => {
    if (password_1.value != password_2.value)
        event.preventDefault();
})

checkPasswords();