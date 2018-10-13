import sys
import json
import copy
import argparse
from datetime import datetime
from test_completion import get_test_completion as get_scores


def format_data(data):
    data = copy.deepcopy(data)
    for student in data:
        pass
    return data


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("testfile", help="path to test score file")
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

    args = parser.parse_args()

    commit_data_file = open(args.timefile, "r")
    data = get_scores(commit_data_file)
    # print(data)

    formatted_data = format_data(data)
    json = json.dumps(formatted_data)
    print(json)
