
let pListUl = document.getElementById("pList");

let pNameElement = document.getElementById("pName");

let packageResponce;

function getPackagesInfo() {
    block = true;
    let xhr = new XMLHttpRequest();
    let url = '/api/packages-data';
    console.log(url);
    xhr.open('GET', url, true);
    xhr.send();
    xhr.onload = function () {
        let result = JSON.parse(xhr.responseText);
        console.log(result)
        console.log(result.error)
        if (result.error != undefined)
            showError(result.error);
        else {
            packageResponce = result.response;
            displayPackages(result.response);
        }
    }
}

function createClickablePackage(name, pid, isFirst) {
    return "<span><a id=\"pid-" + pid + "\" " + (isFirst ? "class=\"active\"" : "") + " onclick=\"select("+pid+")\">" + name + "</a></span>";
}

let pLinksArea = document.getElementById("pLinksList");

function createLink(link) {
    if (link.deleted === undefined)
        link.deleted = 0;
    let lid = link.lid;
    if (link.deleted === 1)
        lid = -lid;
    let linkLi = document.createElement("li");
    pLinksArea.appendChild(linkLi);
    let urlArea = document.createElement("input");
    let delButton = document.createElement("a");
    delButton.setAttribute("onclick", "remove(" + link.lid + ")");
    delButton.classList.add("center-text");
    if (link.deleted === 1) {
        delButton.innerHTML = "&#9989;";
    } else {
        delButton.innerHTML = "&#10060;";
    }
    urlArea.type = "text";
    urlArea.name = "url+" + link.pid + "+" + lid;
    urlArea.value = link.url;
    let isCheckedArea = document.createElement("input");
    isCheckedArea.type = "checkbox"
    isCheckedArea.name = "checkbox+" + link.pid + "+" + lid;
    isCheckedArea.checked = !link.hidden;
    console.log(urlArea)
    console.log(isCheckedArea)
    linkLi.appendChild(urlArea);
    if (lid !== 0) {
        linkLi.appendChild(delButton);
        linkLi.appendChild(isCheckedArea);
    }
}

function remove(linkid) {
    let pid = null;
    for (let i = 0; i < packageResponce.length; i++) {
        for (let j = 0; j < packageResponce[i].links.length; j++) {
            if (Math.abs(packageResponce[i].links[j].lid) === linkid) {
                packageResponce[i].links[j].deleted ^= 1;
                pid = packageResponce[i].links[j].pid;
            }
        }
    }
    if (pid != null)
        select(pid)
}

function fillPackageArea(packageItem) {
    console.log(packageItem.name);
    let nameArae = document.getElementById("name-area");
    nameArae.name = "name+" + packageItem.id;
    console.log(nameArae);
    nameArae.value = packageItem.name;
    for (let i = 0; i < packageItem.links.length; i++) {
        console.log(packageItem.links[i]);
        createLink(packageItem.links[i]);
    }
}

let oldSelected = 1;

function select(pid) {
    let last = document.getElementById("pid-"+oldSelected);
    last.classList.remove("active");
    let curr = document.getElementById("pid-"+pid);
    curr.classList.add("active");
    pLinksArea.innerHTML = "";
    oldSelected = pid;
    for (let i = 0; i < packageResponce.length; i++) {
        if (packageResponce[i].id === pid) {
            pNameElement.innerText = packageResponce[i].name;
            fillPackageArea(packageResponce[i]);
        }
    }
}

function displayPackages(pArray) {
    console.log(pArray);
    for (let indx = 0; indx < pArray.length; indx++) {
        let package = pArray[indx];
        let cLiItem = document.createElement("li");
        pListUl.appendChild(cLiItem);
        cLiItem.innerHTML = createClickablePackage(package.name, package.id, indx == 0);
        if (indx == 0) {
            oldSelected = package.id;
            select(package.id);
        }
        console.log()
    }
}

function showError(msg) {
    let msgP = document.getElementById("errorMessage");
    console.log(msgP);
    let msgDiv = document.getElementById("errorArea");
    console.log(msgDiv);
    msgDiv.style.display = "block";
    msgP.innerText = msg;
}

function addNewLink() {
    for (let i = 0; i < packageResponce.length; i++) {
        if (packageResponce[i].id === oldSelected) {
            packageResponce[i].links.push({
                "pid": packageResponce[i].id,
                "lid": 0,
                "url": "vk.com/",
                "hidden": false
            });
            select(oldSelected);
            break;
        }
    }
}

getPackagesInfo();