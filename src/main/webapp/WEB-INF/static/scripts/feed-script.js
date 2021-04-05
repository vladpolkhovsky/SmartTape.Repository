
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

function body_onload() {
    searchLine.focus();
}