
let words = [
    "Навальный",
    "Юрий Дудь",
    "Юлий Онежка",
    "Юлик",
    "Кузьма Гридин",
    "Кузьма",
    "The Kate Clapp",
    "Стас Давыдов",
    "Wylsacom",
];

const searchLine = document.getElementById("search");
const defaultPlaceholder = "Поиск в Умной Ленте.";
const defaultPlaceholderExample = "Например: ";

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function getRandomInt(max) {
    return Math.floor(Math.random() * Math.floor(max));
}

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        let j = getRandomInt(array.length);
        let tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}

let continueWordLine = true;

async function killLineWords() {
    continueWordLine = false;
}

async function searchLineWords() {
    shuffleArray(words)
    continueWordLine = true;
    let lastWord = "";
    while(continueWordLine) {
        searchLine.placeholder = defaultPlaceholderExample;
        let random = getRandomInt(words.length);
        if (lastWord == words[random])
            random = (random + 1) % words.length;
        lastWord = words[random];
        for (let i = 0; i < words[random].length && continueWordLine; i++) {
            searchLine.placeholder += words[random][i];
            if (words[random][i] != ' ')
                await sleep(100 + Math.random() * 300);
        }
        if(continueWordLine)
            await sleep(800);
    }
    searchLine.placeholder = defaultPlaceholder;
}

function searchLineRedirectAction(el) {
    if (event.key === 'Enter') {
        window.location.replace("/nl-feed?url="+el.value);
    }
    console.log(event.key);
}

searchLine.onkeydown = (x => searchLineRedirectAction(searchLine));

searchLine.onblur = (x => killLineWords());

searchLine.onfocus = (x => searchLineWords());

document.body.onload = (x => searchLine.focus());

function onImageSliderClick(event) {
    console.log(event);
    console.log(event.parentNode.parentNode);
    console.log(event.parentNode.parentNode.firstChild.firstChild);
    console.log(event.parentNode.parentNode.firstChild.firstChild.src=event.src);
}

function showMore(event) {
    event.classList.add("hidden");
    let discr = event.previousSibling;
    let closeBtn = event.nextSibling;
    discr.classList.remove("see-more");
    closeBtn.classList.remove("hidden");
}

function hide(event) {
    event.classList.add("hidden");
    let discr = event.previousSibling.previousSibling;
    discr.classList.add("see-more");
    let openBtn = event.previousSibling;
    openBtn.classList.remove("hidden");
}

let block = false;

window.onscroll = function(event) {
    let scrollHeight = Math.max(
        document.body.scrollHeight, document.documentElement.scrollHeight,
        document.body.offsetHeight, document.documentElement.offsetHeight,
        document.body.clientHeight, document.documentElement.clientHeight
    );
    if (!block && scrollHeight - window.pageYOffset < 2000) {
        if (!block) {
            getNewPosts();
        }
    }
};