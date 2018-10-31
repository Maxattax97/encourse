import sys
from datetime import datetime
from helper import eprint


def past_progress(time_file):
    """Generates historic progrss data for all users

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
        dict: A dictionary that maps students to their progress at each recorded date.
        The dictionary is of the following form: ::
         
            {
                "name1": {
                        "date": datetime,
                        "progress": int (0 <= int <= 100)
                    }
                ...
            }

    """
    line = time_file.readline()
    line = line.lstrip(" ").rstrip("\n")
    users = {}
    current_name = ""
    previous_words = []
    history = []
    while line != "":
        # print(line)
        words = line.split(" ")
        if words[0] == "Start":
            # Update the current user
            current_name = words[1]
            history = []
        elif words[0] == "End":
            # Update the user's final time
            if current_name != "" and previous_words != []:
                users[current_name] = history
        else:
            eprint(words)
            progress = int(float(words[1]))
            date = datetime.strptime(words[0], "%Y-%m-%d").date()
            history.append({"date": date, "progress": progress})

        line = time_file.readline()
        line = line.lstrip(" ").rstrip("\n")
        previous_words = words
    return users
