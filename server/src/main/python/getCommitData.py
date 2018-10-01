import sys

def getCommitData():
    return 0



# Runs on file call
commitDataFile = open(sys.argv[1], "r")
#print(commitDataFile.read())
line = commitDataFile.read()
while line != "":
    print(line)
    
    line = commitDataFile.read()
    

