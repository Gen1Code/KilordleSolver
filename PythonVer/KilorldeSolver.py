import random

#checks to see if an appropriate amount of information has been acheived
def containall(AlphabetLeft):
    return all(not AlphabetLeft[i] for i in range(5))

#Generates an array containing the strings of letters not yet used in each position
def updateAlphaLibrary(AlphabetLeft,newWord):
    for i in range(5):
        AlphabetLeft[i].discard(newWord[i])

#returns the amount of information found if this word was used as the next word
def newAlphaLocations(AlphabetLeft,word,beg):
    count = 0
    for i in range(5):
        if word[i] in AlphabetLeft[i]:
            if beg:
                count += random.random()*0.05 + 1 / weights[word[i]]
            else:
                count += 1 + getSWeight(word[i],i)

    if count == 0:
        wordsToRemove.add(word)
    return count

def getSWeight(char,pos):
    return specialWeights[pos].get(char,0)
    
def copyAlphabet():
    tmp = []
    for i in range(5):
        tmp.append(set(alphabet[i]))
    return tmp

#MAIN
print("Running")

#Setup
words = set()
alphabet = [set() for _ in range(5)]
weights = {
    "a":1.125,
    "b":1.027,
    "c":1.036,
    "d":1.033,
    "e":1.1,
    "f":1.020,
    "g":1.06,
    "h":1.031,
    "i":1.061,
    "j":1.002,
    "k":1.021,
    "l":1.056,
    "m":1.031,
    "n":1.052,
    "o":1.066,
    "p":1.03,
    "q":1.002,
    "r":1.072,
    "s":1.075,
    "t":1.056,
    "u":1.044,
    "v":1.011,
    "w":1.016,
    "x":1.002,
    "y":1.038,
    "z":1.006
}
specialWeights = [{
"v":0.00,
"q":0.02,
"s":-0.02
},{
"q":0.00,
"v":0.02,
"g":0.02,
"j":0.02
},{
"k":0.00
},{
"x":0.02
},{

}]

print("Generating Alpha Library")

#Generate needed characters in each positions from wordle list
with open("..\Wordles.txt") as f:
    for line in f:
        words.add(line.removesuffix("\n"))

for word in words:
    for i in range(5):
        alphabet[i].add(word[i])

#Append all other valid words to the wordle list
with open("..\Words.txt") as f:
    for line in f:
        words.add(line.removesuffix("\n"))

print("Finding Combinations")

minNumWords = 42
minWords = []

#the bigger the range the better the result, set at 30 for a good heuristic, >10000 is way too big
for k in range(30):
    found = []
    size = 0
    AlphaLibrary = copyAlphabet()
    workingWords = words.copy()

    #while not enough information found keep adding words
    while True:
        maxInfo = 0
        bestWord = ""
        beg = size <=15 #15 has given me the lowest guesses: 31
        wordsToRemove = set()

        #Find the amount of information gained for each word, add the word that gives most information
        for word in workingWords:
            temp = newAlphaLocations(AlphaLibrary, word, beg)
            if temp > maxInfo:
                maxInfo = temp
                bestWord = word
        
        found.append(bestWord)
        size += 1
        updateAlphaLibrary(AlphaLibrary,found[-1])

        workingWords.difference_update(wordsToRemove)

        if size >= minNumWords:
            break
        elif containall(AlphaLibrary): #if new group of words is smaller than last then save for later
            minNumWords = size
            minWords = found
            print(f"{size} words were found that fulfill the criteria")
            break        

print("Lowest amount of words that fulfilled criteria:")
print(minWords)
print(minNumWords)