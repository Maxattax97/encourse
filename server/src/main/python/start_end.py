import sys


def commit_data(commit_times_file):
    line = commit_times_file.readline()
    line = line.lstrip(" ").rstrip("\n")
    users = {}
    current_name = ""
    previous_words = []
    while line != "":
        # print(line)
        words = line.split(" ")
        if words[0] == "Start":
            # Update the current user
            current_name = words[1]
            line = commit_times_file.readline()
            line = line.lstrip(" ").rstrip("\n")
            words = line.split(" ")
            users[current_name] = (line.split(" ")[1], 0)
        elif words[0] == "End":
            # Update the user's final time
            if current_name != "" and previous_words != []:
                users[current_name] = (users[current_name][0], previous_words[1])

        line = commit_times_file.readline()
        line = line.lstrip(" ").rstrip("\n")
        previous_words = words
    return users
