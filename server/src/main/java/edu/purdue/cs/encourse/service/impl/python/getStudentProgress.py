import sys
from helper import is_number as is_number

def get_cumulative_progress_for_student(student, progress_file):
    expect_time = False
    name = ""
    total_additions = 0
    total_deletions = 0
    data_tuples = []
    commit_lines = []
    for line in progress_file:
        line = line.strip("\n").strip(" ")
        line = " ".join(line.split("\t"))
        words = line.split(" ")
        if words == ['']:
            expect_time = True
            #print("No words found in line: {}".format(line))
            continue
        if words[0] == "Start":     # Start of user
            expect_time = True
            name = words[1]
            #print(name)
            #TODO: implement
            pass
        elif words[0] == "End":     # End of user
            #TODO: implement
            #print("End")
            pass
        elif expect_time == True:   # New Data/Time/Code tuple
            expect_time = False
            #TODO: implement
            #print("Found new date/time/code")
            if len(words) != 3:
                print("Expected date, time, and code. Found: {}".format(words))
            date = words[0]
            time = words[1]             # Unused
            code = words[2]             # Unused
            commit_lines.append((date))
            current_date = date
            pass
        else:                       # New Addition/Deletion/File tuple
            #TODO: implement
            #print("{} == {}".format(name, student))
            if name == student:
                # Start tracking changes
                if len(words) != 3:
                    print("Unknown line format with words {}".format(words))
                    continue
                additions = int(words[0]) if is_number(words[0]) else 0
                deletions = int(words[1]) if is_number(words[1]) else 0
                file_path = words[2]    # Unused
                commit_lines.append((additions, deletions))
                #print(additions, deletions, file_path)

    else:
        print("EOF")

    shouldTrackChanges = False
    for line in progress_file:
        line = line.strip("\n").strip(" ")
        line = " ".join(line.split("\t"))
        words = line.split(" ")
        if len(words) < 1:
            print("No words found")

        if words[0] == "Start":
            if student == words[1]:
                shouldTrackChanges = True
                continue;
        elif words[0] == "End":
            shouldTrackChanges = False
        else:
            commit_lines.append(line)

    print(commit_lines)
    for line in commit_lines():
        pass

    current_date = ""  
    print(commit_lines)
    for data_tuple in list(reversed(commit_lines)):
            if len(data_tuple) == 1:
                date = data_tuple[0]
                if date != current_date:
                    if current_date != "":
                        # TODO: totals are being passed by reference?
                        commit_tuple = (current_date, total_additions, total_deletions)
                        data_tuples.append(commit_tuple)
            elif len(data_tuple) == 2:
                additions = data_tuple[0]
                deletions = data_tuple[1]
                total_additions += additions
                total_deletions += deletions

    return list(reversed(data_tuples)), total_additions, total_deletions
    
