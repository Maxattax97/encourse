import sys
from helper import is_number as is_number

def get_test_completion(test_case_file):
    students = {}
    for line in test_case_file:
        # Clean line for parsing
        line = line.strip("\n").strip(" ")
        line = " ".join(line.split("\t"))

        words = line.split(";")
        if len(words) == 0 or words == ['']:
            continue
        name = words[0]
        #print(name)
        total_score = 0
        test_score = 0
        tests = {}
        for word in words[1:]:
            test, score = word.split(":")
            #print(test,score)
            tests[test] = score
            if score == "P":
                test_score += 1
            total_score += 1
        if total_score == 0:
            return {"tests":tests, "total":0}
        students[name] = {"tests":tests, "total":test_score * 100 / total_score}
    return students
    
def get_test_completion_string(test_case_string):
    words = test_case_string.split(";")
    if len(words) == 0 or words == ['']:
        return {}
    name = words[0]
    #print(name)
    total_score = 0
    test_score = 0
    tests = {}
    for word in words[1:]:
        test, score = word.split(":")
        #print(test,score)
        tests[test] = score
        if score == "P":
            test_score += 1
        total_score += 1

    if total_score == 0:
	    return {"tests":tests, "total":0} 
    return {"tests":tests, "total":test_score * 100 / total_score}
