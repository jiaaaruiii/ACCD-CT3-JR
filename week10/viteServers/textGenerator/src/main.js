import './style.css'

let fetchButton = document.getElementById('fetchTEXT');
let generateButton = document.getElementById('generateTEXT');
let ngramsOverWords = document.getElementById('ngramSelection').checked

let inputText = document.getElementById('inputText');
let outputText = document.getElementById('outputText');

let sourceURL = 'https://poetrydb.org/author/Emily%20Dickinson';
let order = 3;
let ngrams = {}
let beginnings = []

function fetchSourceText(){
  fetch(sourceURL)
  .then(response => response.json())
  .then(poems => {
    console.log(poems);
    console.log(ngramsOverWords)
    poems.forEach(poem =>{
      poem.lines.forEach(line => {
        line = line.replace(/[-"'`#()<>,!?;.:]/g, '');
        if (!line) return;

        if (ngramsOverWords) {
          for(let i = 0; i <= line.length - order; i++) {
            let gram = line.substring(i,i + order);
            if(!ngrams[gram]) {
              ngrams[gram] = [];
            }
            ngrams[gram].push(line.charAt(i + order));
          }

        } else{  
          let words = line.split(' ');
          console.log(words)
          for (let i = 0; i < words.length - 1; i++){
            if (i == 0){
              beginnings.push(words[i]);
            }

            let next = words[i + 1];

            if(!ngrams[words[i]]){
              ngrams[words[i]] = []
            }

            ngrams[words[i]].push(next);
          }
        }
      })
    })
      inputText.disabled = false;
      generateButton.disabled = false;
      console.log(ngrams);
  })
  .catch(error => {
      console.error('Error fetching the text file:', error);
  })
}

function generateText(){
  if(ngramsOverWords){
    generateNGramText();
  }else{
    generateWordText();
  }
}

function generateNGramText(){
  let currentGram = inputText.value.substring(0,order)
  let result = currentGram
  for(let i = 0; i < 100; i++){
    let possibilities = ngrams[currentGram]
    if(!possibilities) break;
    let next = possibilities[Math.floor(Math.random()*
    possibilities.length)];
    result += next
    currentGram = result.substring(result.length - order, result.length)
  }
  outputText.textContent = result
}

function generateWordText(){
  let currentWord = beginnings [Math.floor(Math.random() * beginnings.length)];
  let result = currentWord;
  for (let i = 0; i < 10; i++){
    let possibilities = ngrams[currentWord];
    if(!possibilities) break;
    let next = possibilities[Math.floor(Math.random() * possibilities.length)];
    result += ' ' + next;
    currentWord =next;

  }

  outputText.innerHTML = '<br/>' + result;
    

}


function toggleNGramMode(e) {
  ngramsOverWords = document.getElementById("ngramSelection").checked;
  ngrams = {}
  beginnings = []
  inputText.value = ''
  outputText.textContent = ''
  inputText.disabled = true;
  generateButton.disabled = true;
}

//event listener
fetchButton.addEventListener('click', fetchSourceText);
generateButton.addEventListener('click', generateText)
document.getElementById("ngramSelection").addEventListener('change', toggleNGramMode);
document.getElementById("wordSelection").addEventListener('change', toggleNGramMode);