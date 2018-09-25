import sys
import json

def format_commit_data(users):
    data = {}
    for key in users.keys():
        user = users[key]
        user_data = {}
        if len(user) == 2:
            user_data["start"] = user[0]
            user_data["end"] = user[1]
        data[key] = user_data
    return data

def input_commit_data(commitTimesFile):
    line = commitTimesFile.readline()
    line = line.lstrip(" ").rstrip("\n")
    users = {}
    currentName = ""
    previous_words = []
    while line != "":
        #print(line)
        words = line.split(" ")
        if words[0] == "Start":
            # Update the current user
            currentName = words[1]
            line = commitTimesFile.readline()
            line = line.lstrip(" ").rstrip("\n")
            words = line.split(" ")
            users[currentName] = (line.split(" ")[1], 0)
        elif words[0] == "End":
            # Update the user's final time
            if currentName != "" and previous_words != []:
                users[currentName] = (users[currentName][0], previous_words[1])
            
        line = commitTimesFile.readline()
        line = line.lstrip(" ").rstrip("\n")
        previous_words = words
    return users

# Runs on file call
commit_file = open(sys.argv[1], "r")

users = input_commit_data(commit_file)
for key in users.keys():
    user = users[key]
    #print("{} -> {}".format(key, user))

data = format_commit_data(users)
#print(data)
json = json.dumps(data)
# Outputs json to stdout
print(json)

