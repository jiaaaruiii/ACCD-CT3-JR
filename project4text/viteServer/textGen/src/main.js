import './style.css'

let fetchButton = document.getElementById('fetchTEXT');
let generateButton = document.getElementById('generateTEXT');
let inputText = document.getElementById('inputText');
let outputText = document.getElementById('outputText');
let sourceURL = 'https://poetrydb.org/author/Emily%20Dickinson';
let order = 3;
let ngrams = {}

function fetchSourceText(){
  fetch(sourceURL)
  .then(response => response.json())
  .then(poems => {
      poems.forEach(poem =>{
        poem.lines.forEach(line => {
          line = line.replace(/["'`,!?;.:-_()]/g, '');
          if (!line) return;
          //console.log(line);

          for(let i = 0; i<= line.length - order; i++) {
            let gram = line.substring(i,i + order);
            if(!ngrams[gram]) {
              ngrams[gram] = [];
            }
            ngrams[gram].push(line.charAt(i + order));
          }
        })
      })
    console.log(ngrams);
  })
  .catch(error => {
      console.error('Error fetching the text file:', error);
  })
}

function generateText(){
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

fetchButton.addEventListener('click', fetchSourceText);
generateButton.addEventListener('click', generateText)