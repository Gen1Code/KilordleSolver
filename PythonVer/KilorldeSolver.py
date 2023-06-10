import random

#checks to see if an appropriate amount of information has been acheived
def containall(AlphabetLeft):
    flag = True
    for i in range(0,5):
        if len(AlphabetLeft[i]) > 0:#amount of letters left per position 
            flag = False
            break
    return flag

#Generates an array containing the strings of letters not yet used in each position
def generateAlphaLibrary(AlphabetLeft,newWord):
    for i in range(0,5):
        AlphabetLeft[i] = AlphabetLeft[i].replace(newWord[i],'')


#returns the amount of information found if this word was used as the next word
def newAlphaLocations(AlphabetLeft,word,beg):
    count = 0
    for i in range(0,5):
        if word[i] in AlphabetLeft[i]:
            if beg:
                count = count + random.random()*0.03+1/(weights[word[i]])
            else:
                count = count + 1 + getSWeight(word[i],i)
    return count

def getSWeight(char,pos):
    if char in specialWeights[pos]:
        return specialWeights[pos][char]
    else:
        return 0
    
def copyAlphabet():
    tmp = []
    for i in range(0,5):
        tmp.append(alphabet[i][:])
    return tmp

#MAIN
print("Running")

#Setup
words = []
alphabet = [""]*5
weights = {
    "a":1.105,
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
        words.append(line.removesuffix("\n"))

for word in words:
    for i in range(0,5):
        if word[i] not in alphabet[i]:
            alphabet[i] += word[i]

#Sort each index for readability
for i in range(0,5):
    alphabet[i] = ''.join(sorted(alphabet[i]))

print(alphabet)

#You can un comment and change this to fit your own wordle list
#This is one generated for this kilordle: https://jonesnxt.github.io/kilordle/
#alphabet = ['abcdefghijklmnopqrstuvwyz', 'abcdefghijklmnopqrstuvwxyz', 'abcdefghijklmnopqrstuvwxyz', 'abcdefghijklmnoprstuvwxyz', 'abcdefghiklmnoprstuwxyz']

#copyable comment, full alphabet in each position
#alphabet = ["abcdefghijklmnopqrstuvwxyz","abcdefghijklmnopqrstuvwxyzy","abcdefghijklmnopqrstuvwxyz","abcdefghijklmnopqrstuvwxyz","abcdefghijklmnopqrstuvwxyz"]

#Append all other valid words to the wordle list
with open("..\Words.txt") as f:
    for line in f:
        words.append(line.removesuffix("\n"))


print("Finding Combination")

minNumWords = 40
minWords = []

#the bigger the range the better the result, set at 30 for a good heuristic, >10000 will take too long
for k in range(0,300):
    found = []
    size = 0
    max = 0
    lastmax = 0
    AlphaLibrary = copyAlphabet()
    #shuffle the words so as to change which word is picked when information gain is the same
    random.shuffle(words)
    print(k)

    #while not enough information found keep adding words
    while True:
        lastmax = max 
        max = 0
        x = ''
        beg = size <=15 #15 has given me the lowest guesses: 31

        #Find the amount of information gained for each word, add the word that gives most information
        for word in words:
            temp = newAlphaLocations(AlphaLibrary, word, beg)
            if temp > max:
                max = temp
                x = word
            if max == lastmax and not beg:
                break
        
        found.append(x)
        size = size + 1

        generateAlphaLibrary(AlphaLibrary,found[-1])

        if size >= minNumWords:
            break
        elif containall(AlphaLibrary): #if new group of words is smaller than last then save for later
            minNumWords = size
            minWords = found
            print(str(size)+" words were found that fulfill the criteria")
            break        

print("Lowest amount of words that fulfilled criteria:")
print(minWords)
print(minNumWords)