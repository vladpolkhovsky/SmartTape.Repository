let offset = 0;

let count = 10;

const feed_center = document.getElementsByClassName("feed-center")[0];

let id = 1;

const queryString = window.location.search;
console.log(queryString);

const urlParams = new URLSearchParams(queryString);

let pId = urlParams.get('pID')

let isGeneral = urlParams.get("package")

if (pId !== null)
    id = pId;

if (isGeneral !== null)
    id = -1

function getNewPosts() {
    block = true;
    let xhr = new XMLHttpRequest();
    let url = './api/posts?package-id='+id+'&offset='+offset+'&count='+count;
    console.log(url);
    xhr.open('GET', url, true);
    xhr.send();
    xhr.onload = function () {
        let result = JSON.parse(xhr.responseText);
        console.log(result)
        result.response.forEach(element => {
            createPost(element)
        });
        offset++;
        block = false;
    }
}

async function createPost(element) {
    console.log(element.header_short_name)
    let postDiv = feed_center.appendChild(document.createElement("div"));
    postDiv.classList.add("post");
    let postDivHeader = postDiv.appendChild(document.createElement("div"));
    postDivHeader.classList.add("post-header");
    createHeader(postDivHeader, element.header_tittle, element.header_short_name, element.header_url, element.time, element.package_name);

    if (element.attachments.length > 0) {
        let imageDiv = postDiv.appendChild(document.createElement("div"));
        imageDiv.classList.add("attachments");
        createImageArea(imageDiv, element.attachments)
    }

    if (element.description.length > 0) {
        let discrDiv = postDiv.appendChild(document.createElement("div"));
        discrDiv.classList.add("description-vk");
        if (element.description.length > 120) {
            discrDiv.classList.add("see-more");
            let aShowMore = postDiv.appendChild(document.createElement("a"));
            aShowMore.classList.add("bar");
            aShowMore.setAttribute("onclick", "return showMore(this);");
            aShowMore.innerText = "Показать больше";
            let aHide = postDiv.appendChild(document.createElement("a"));
            aHide.classList.add("bar");
            aHide.classList.add("hidden")
            aHide.setAttribute("onclick", "return hide(this);");
            aHide.innerText = "Скрыть";
        }
        createDiscription(discrDiv, element.header_short_name, element.description);
    }
}

/*
<p><span class="header-time">12 минут назад</span>|<span class="header-package-info">Больше в пакете "Популярное".</span></p>
*/
async function createHeader(postDivHeader, tittle, screenName, headerSrc, time, package_) {
    let headerImage = postDivHeader.appendChild(document.createElement("img"));
    headerImage.classList.add("header-img");
    headerImage.src = headerSrc;
    let headerTittle = postDivHeader.appendChild(document.createElement("div"));
    headerTittle.classList.add("header-tittle");
    let headerH1 = headerTittle.appendChild(document.createElement("h1"));
    let headerP = headerTittle.appendChild(document.createElement("p"));
    let span_screen_name = headerH1.appendChild(document.createElement("span"));
    let span = headerH1.appendChild(document.createElement("span"));
    let span_tittle = headerH1.appendChild(document.createElement("span"));
    span_screen_name.innerText = '@' + screenName;
    span_screen_name.classList.add("header-user-name");
    span.innerText = "|";
    span_tittle.innerHTML = tittle;
    let span_time = headerP.appendChild(document.createElement("span"))
    span = headerP.appendChild(document.createElement("span"))
    let span_pinfo = headerP.appendChild(document.createElement("span"))
    span_time.innerText = time;
    span_time.classList.add("header-time");
    span.innerText = "|";
    span_pinfo.innerHTML = package_;
    span_pinfo.classList.add("header-package-info");
}

async function createImageArea(imageDiv, images) {
    console.log(images);
    let divVkImage = imageDiv.appendChild(document.createElement("div"));
    divVkImage.classList.add("attachment-image-vk");
    let mainImage = divVkImage.appendChild(document.createElement("div"));
    mainImage.classList.add("main-image");
    let mainImg = mainImage.appendChild(document.createElement("img"));
    mainImg.src = images[0].url;
    if (images.length > 1) {
        let subImage = divVkImage.appendChild(document.createElement("div"));
        subImage.classList.add("sub-images");
        images.forEach(el => {
            let img = subImage.appendChild(document.createElement("img"));
            img.setAttribute("onclick", "return onImageSliderClick(this);");
            img.src = el.url;
        });
    }
}

async function createDiscription(discrDiv, screenName, text) {
    let p = discrDiv.appendChild(document.createElement("p"));
    let sn = p.appendChild(document.createElement("span"));
    sn.classList.add("header-user-name")
    sn.innerText = '@' + screenName + ':';
    let textSpan = p.appendChild(document.createElement("span"));
    textSpan.classList.add("description-vk-text");
    textSpan.innerText = text;
}

getNewPosts();
