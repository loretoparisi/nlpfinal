# -- coding: utf-8 --

global ptoIPA

def makeDict(filename):
    f = open(filename, 'r')
    for line in f:
        words = line.split(',')
        print words
        if len(words) > 2:
            ptoIPA[words[0]] = {}
            ptoIPA[words[0]][words[2]] = words[1]
        else:
            ptoIPA[words[0]] = words[1]
    for key in ptoIPA:
        if type(ptoIPA[key]) == unicode:         
            print key, ptoIPA[key].decode("utf-8")
        else:
            print key, ptoIPA[key]

def toIPA(filename):
    f = open(filename, 'r')
    for line in f:
        words = line.split()
        newWords = []
        for word in words:
            pinyin = word[:-1]
            tone = word[-1]
            newWord = ''
            if pinyin in ptoIPA:
                if isinstance(ptoIPA[pinyin], dict):
                    print ptoIPA[pinyin]
                    if tone == 5:
                        newWord+=ptoIPA[pinyin]["5thTone"]
                    else:
                        newWord+=ptoIPA[pinyin]["Default"]
                else:
                    newWord+=ptoIPA[pinyin]
            newWord+=tone
        newWords += newWord
        for word in newWords:
            print word,

if __name__ == '__main__':
    ptoIPA = {}
    makeDict('pintoipa.txt')
    toIPA('pinyin.final')