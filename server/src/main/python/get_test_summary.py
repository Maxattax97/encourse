import sys
import json
import copy
import argparse
from datetime import datetime
from helper import time_string
from helper import daterange
from helper import date_string
from helper import eprint
from test_completion import get_test_completion as get_test_scores
from start_end import commit_data


def api_format_data(data, hidden):
    tests = {}
    test_totals = {}
    # Collect scores for each test
    for student in data:
        info = data[student]
        test_scores = info["tests"]
        for test_name in test_scores:
            if test_scores[test_name] == "P":
                if test_name in tests:
                    tests[test_name] += 1
                    test_totals[test_name] += 1
                else:
                    tests[test_name] = 0
                    test_totals[test_name] = 1
            else:
                if test_name in tests:
                    test_totals[test_name] += 1
                else:
                    tests[test_name] = 0
                    test_totals[test_name] = 1

    test_list = []
    # Reformat into api format
    for test_name in tests:
        test_score = tests[test_name]
        test_total = test_totals[test_name]
        new_bar = {}
        new_bar["name"] = test_name + " H" if hidden else test_name
        new_bar["hidden"] = hidden
        new_bar["score"] = int(test_score * 100 / test_total)
        test_list.append(new_bar)
    return test_list


def merge_data(visible, hidden):
    visible.append(hidden)
    return visible


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("visible", help="path to visible test score file")
    parser.add_argument("hidden", help="path to hidden test score file")
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

    args = parser.parse_args()

    visible_test_score_file = open(args.visible, "r")
    hidden_test_score_file = open(args.hidden, "r")

    visible_data = get_test_scores(visible_test_score_file)
    hidden_data = get_test_scores(hidden_test_score_file)

    formatted_visible = api_format_data(visible_data, False)
    formatted_hidden = api_format_data(hidden_data, True)
    api_json = json.dumps(merge_data(formatted_visible, formatted_hidden))
    print(api_json)
