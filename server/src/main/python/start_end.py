import sys
from helper import eprint


def commit_data(time_file):
    """Generates commit time data for all users

    Takes in a specially formatted file generated by git commands, and stores the data
    from it into an eaily accessible python dictionary.

    **Args**
        **time_file** (file): A specially formatted file generated from a bash script.
        A small example of the formatting follows: ::
            
            Start name1
                 3 2018-08-25
                 25 2018-08-30
            End name1

    **Returns**: 
        dict: A dictionary that maps students to a tuple of start and end dates.
        The dictionary is of the following form: ::
         
            {
                "name1": ("mm-dd-yyyy", "mm-dd-yyyy")
                ...
            }

    """
    line = time_file.readline()
    line = line.lstrip(" ").rstrip("\n")
    users = {}
    current_name = ""
    previous_words = []
    while line != "":
        words = line.split(" ")
        if words[0] == "Start":
            # Update the current user
            current_name = words[1]
            line = time_file.readline()
            line = line.lstrip(" ").rstrip("\n")
            words = line.split(" ")
            users[current_name] = (line.split(" ")[1], 0)
        elif words[0] == "End":
            # Update the user's final time
            if current_name != "" and previous_words != []:
                users[current_name] = (users[current_name][0], previous_words[1])

        line = time_file.readline()
        line = line.lstrip(" ").rstrip("\n")
        previous_words = words
    return users
