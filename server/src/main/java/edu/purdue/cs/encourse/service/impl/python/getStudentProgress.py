import sys
from helper import is_number as is_number

def get_cumulative_progress_for_student(student, progress_file):
    expect_time = False
    name = ""
    total_additions = 0
    total_deletions = 0
    data_tuples = []
    commit_lines = []
    print("Hi")
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
    dates = []
    current_date_list = []
    is_date_next = True
    #for line in commit_lines:
        

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
    
