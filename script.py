import re

# def split(filename):
#     f = open(filename, 'r');
#     for line in f:
#         words = line.split("   ")
#         for word in words:
#             print word

def removeWhite(filename):
    f = open(filename, 'r')
    for line in f:
        match = re.match(r'^\s+$', line);
        if match:
            continue
        else:
            print line[:-1]


# def clean(filename):
#     f = open(filename,'r')
#     count = -1
#     for line in f:
#         count += 1
#         if count%2 == 0:
#             print line[:-1]

if __name__ == '__main__':
    #split("chinese2.txt")
    #split("chinese3.txt")
    #split("html1.out")
    # split("pinyin.split")
    removeWhite("pinyin.split2")