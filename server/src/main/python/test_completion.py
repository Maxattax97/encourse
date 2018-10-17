import sys
from helper import is_number as is_number
from helper import eprint


def get_test_completion(test_file):
    """Generates test score dictionary for each student
    
    **Args**:
        **test_file** (file): A specially formatted file containing test data
        The following is a sample of one of these files: ::
            
            name1;Test1:P:1.0;Test2:P:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0
            name2;Test1:F:1.0;Test2:F:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0

    **Returns**:
        dict: A dictionary mapping students to their respective test data.
        The dictionary has the following format: ::

            {
                "name1": {
                   "tests": {
                        "Test1": (bool, float),
                        ...
                   },
                   "total": int (percentage)
                },
                ...
            }

    """
    students = {}
    for line in test_file:
        # Clean line for parsing
        line = line.strip("\n").strip(" ")
        line = " ".join(line.split("\t"))

        words = line.split(";")
        if len(words) == 0 or words == [""]:
            continue
        name = words[0]
        # print(name)
        total_score = 0
        test_score = 0
        tests = {}
        for word in words[1:]:
            test, passed, score = word.split(":")
            score = float(score)
            passed = True if passed == "P" else False
            # print(test,score)
            tests[test] = (passed, score)
            if passed:
                test_score += score
            total_score += score
        if total_score == 0:
            return {"tests": tests, "total": 0}
        students[name] = {"tests": tests, "total": test_score * 100 / total_score}
    return students


def get_test_completion_string(test_string):
    """Generates test score dictionary for each student
    
    **Args**:
        **test_case_string** (str): A specially formatted string containing test data
        The following is a sample of one of these strings: ::
            
            "name2;Test1:F;Test2:F;Test3:P;Test4:P;Test5:P"

    **Returns**:
        dict: A dictionary mapping students to their respective test data.
        The dictionary has the following format: ::

            {
                "name1": {
                   "tests": {
                        "Test1": (bool, float),
                        ...
                   },
                   "total": int (percentage)
                },
                ...
            }

    """
    words = test_string.split(";")
    if len(words) == 0 or words == [""]:
        return {}
    name = words[0]
    total_score = 0
    test_score = 0
    tests = {}
    for word in words[1:]:
        test, passed, score = word.split(":")
        score = float(score)
        passed = True if passed == "P" else False
        # print(test,score)
        tests[test] = (passed, score)
        if passed:
            test_score += score
        total_score += score

    if total_score == 0:
        return {"tests": tests, "total": 0}
    return {"tests": tests, "total": test_score * 100 / total_score}
