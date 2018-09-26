import sys
from helper import is_number as is_number

def get_cumulative_progress_for_student(student, progress_file):
    expect_time = False
    name = ""
    current_date = ""
    total_additions = 0
    total_deletions = 0
    data_tuples = []
    for line in progress_file:
        line = line.strip("\n").strip(" ")
        line = " ".join(line.split("\t"))
        words = line.split(" ")
        if words == ['']:
            expect_time = True
            continue
        if words[0] == "Start":     # Start of user
            expect_time = True
            if name == student and name != "" and current_date != "":
                data_tuples.append((current_date, total_additions, total_deletions))
            name = words[1]
            pass
        elif words[0] == "End":     # End of user
            pass
        elif expect_time == True:   # New Data/Time/Code tuple
            if name != student:
                continue
            expect_time = False
            if len(words) != 3:
                print("Expected date, time, and code. Found: {}".format(words))
            date = words[0]
            time = words[1]             # Unused
            code = words[2]             # Unused
            if date != current_date and current_date != "":
                data_tuples.append((current_date, total_additions, total_deletions))
            current_date = date
            pass
        else:                       # New Addition/Deletion/File tuple
            if name != student:
                continue
            # Start tracking changes
            if len(words) != 3:
                print("Unknown line format with words {}".format(words))
                continue
            additions = int(words[0]) if is_number(words[0]) else 0
            deletions = int(words[1]) if is_number(words[1]) else 0
            file_path = words[2]    # Unused
            total_additions += additions
            total_deletions += deletions
                
    return data_tuples, total_additions, total_deletions
    
