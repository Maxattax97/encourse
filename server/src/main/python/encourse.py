#!/usr/bin/env python
import argparse
import API

if __name__ == "__main__":
    # Create the top-level parser
    parser = argparse.ArgumentParser(prog="encourse")
    subparsers = parser.add_subparsers(dest="command")

    # Create the parser for the "statistics" command
    stats_parser = subparsers.add_parser("stats")
    stats_parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    stats_parser.add_argument(
        "timefile", type=argparse.FileType("r"), help="path to commit time file"
    )
    stats_parser.add_argument("name", help="user name")
    stats_parser.add_argument("tests", help="test case string")
    stats_parser.add_argument("-t", "--timeout", help="time spent timeout")
    stats_parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    stats_parser.set_defaults(func=API.stats.jsonprint)

    # Create the parser for the "commitcount" command
    commitcount_parser = subparsers.add_parser("commitcount")
    commitcount_parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    commitcount_parser.add_argument("name", help="user name")
    commitcount_parser.set_defaults(func=API.commitcount.jsonprint)

    changes_parser = subparsers.add_parser("changes")
    changes_parser.add_argument("logfile", type=argparse.FileType("r"), help="path to commit log file")
    changes_parser.add_argument("timefile", type=argparse.FileType("r"), help="path to commit time file")
    changes_parser.add_argument("name", help="user name")
    changes_parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    changes_parser.add_argument(
        "-O", "--obfuscate", action="store_true", help="obfuscate flag"
    )
    changes_parser.set_defaults(func=API.changes.jsonprint)

    gitlist_parser = subparsers.add_parser("gitlist")
    gitlist_parser.add_argument("logfile", type=argparse.FileType("r"), help="path to commit log file")
    gitlist_parser.add_argument("name", help="user name")
    gitlist_parser.set_defaults(func=API.gitlist.jsonprint)


    # Tests
    print("stats")
    parsed_args = parser.parse_args(
        [
            "stats",
            "test_datasets/sampleCommitList.txt",
            "test_datasets/sampleCountsDay.txt",
            "cutz",
            "cutz;Test1:P:1.0;Test2:P:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("commitcount")
    parsed_args = parser.parse_args(
        ["commitcount", "test_datasets/sampleCommitList.txt", "cutz"]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("changes")
    parsed_args = parser.parse_args(
        [
            "changes",
            "test_datasets/sampleCommitList.txt",
            "test_datasets/sampleCountsDay.txt",
            "cutz",
            "-l 100",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("gitlist")
    parsed_args = parser.parse_args(
        ["gitlist", "test_datasets/sampleCommitList.txt", "cutz"]
    )
    parsed_args.func(parsed_args)
    print("\n")
    

    # Actual CLI code
    parsed_args = parser.parse_args()
    if parsed_args.command:
        parsed_args.func(parsed_args)
