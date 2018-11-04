#!/usr/bin/env python
import argparse
import API

# Runs api commands
# create the top-level parser
parser = argparse.ArgumentParser(prog="encourse")
# parser.add_argument('--foo', action='store_true', help='foo help')
subparsers = parser.add_subparsers(dest="command")

# create the parser for the "statistics" command
statistics_parser = subparsers.add_parser("statistics")
statistics_parser.add_argument(
    "logfile", type=argparse.FileType("r"), help="path to commit log file"
)
statistics_parser.add_argument(
    "timefile", type=argparse.FileType("r"), help="path to commit time file"
)
statistics_parser.add_argument("name", help="user name")
statistics_parser.add_argument("tests", help="test case string")
statistics_parser.add_argument("-t", "--timeout", help="time spent timeout")
statistics_parser.add_argument("-l", "--limit", help="ignore file changes above limit")
statistics_parser.set_defaults(func=API.stats.jsonprint)

commitcount_parser = subparsers.add_parser("commitcount")
commitcount_parser.add_argument(
    "logfile", type=argparse.FileType("r"), help="path to commit log file"
)
commitcount_parser.add_argument("name", help="user name")
commitcount_parser.add_argument(
    "-O", "--obfuscate", action="store_true", help="obfuscate flag"
)
commitcount_parser.set_defaults(func=API.commitcount.jsonprint)

# Tests
parsed_args = parser.parse_args(
    [
        "statistics",
        "test_datasets/sampleCommitList.txt",
        "test_datasets/sampleCountsDay.txt",
        "cutz",
        "cutz;Test1:P:1.0;Test2:P:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0",
    ]
)
parsed_args.func(parsed_args)
parsed_args = parser.parse_args(
    ["commitcount", "test_datasets/sampleCommitList.txt", "cutz"]
)
parsed_args.func(parsed_args)

# Actual CLI code
parsed_args = parser.parse_args()
if parsed_args.command:
    parsed_args.func(parsed_args)
