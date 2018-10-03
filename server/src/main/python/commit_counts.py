import json

def commit_counts(count_file):
    counts = {}
    line = count_file.readline()
    line = line.strip("\n").strip(" ")
    while line != "":
        words = line.split(" ")
        if len(words) == 2:
            user = words[0]
            count = words[1]
            if user in counts:
                print("Multiple counts for user {}: ({}, {})".format(user, user[counts], count))
            counts[user] = count
        
        line = count_file.readline()
        line = line.strip("\n").strip(" ")
    return counts
