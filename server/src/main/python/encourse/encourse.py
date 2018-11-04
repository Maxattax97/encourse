#!/usr/bin/env python
import argparse
import API


def setup_stats(parser):
    """Configure stats parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument(
        "timefile", type=argparse.FileType("r"), help="path to commit time file"
    )
    parser.add_argument("name", help="user name")
    parser.add_argument("tests", help="test case string")
    parser.add_argument("-t", "--timeout", help="time spent timeout")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.set_defaults(func=API.stats.jsonprint)


def setup_commitcount(parser):
    """Configure commitcount parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument("name", help="user name")
    parser.set_defaults(func=API.commitcount.jsonprint)


def setup_changes(parser):
    """Configure changes parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument(
        "timefile", type=argparse.FileType("r"), help="path to commit time file"
    )
    parser.add_argument("name", help="user name")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")
    parser.set_defaults(func=API.changes.jsonprint)


def setup_gitlist(parser):
    """Configure gitlist parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument("name", help="user name")
    parser.set_defaults(func=API.gitlist.jsonprint)


if __name__ == "__main__":
    # Create the top-level parser
    parser = argparse.ArgumentParser(prog="encourse")
    subparsers = parser.add_subparsers(dest="command")

    # Create the parser for the "statistics" command
    stats_parser = subparsers.add_parser("stats")
    setup_stats(stats_parser)

    # Create the parser for the "commitcount" command
    commitcount_parser = subparsers.add_parser("commitcount")
    setup_commitcount(commitcount_parser)

    changes_parser = subparsers.add_parser("changes")
    setup_changes(changes_parser)

    gitlist_parser = subparsers.add_parser("gitlist")
    setup_gitlist(gitlist_parser)

    # Tests
    print("stats")
    parsed_args = parser.parse_args(
        [
            "stats",
            "data/sampleCommitList.txt",
            "data/sampleCountsDay.txt",
            "cutz",
            "cutz;Test1:P:1.0;Test2:P:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("commitcount")
    parsed_args = parser.parse_args(
        ["commitcount", "data/sampleCommitList.txt", "cutz"]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("changes")
    parsed_args = parser.parse_args(
        [
            "changes",
            "data/sampleCommitList.txt",
            "data/sampleCountsDay.txt",
            "cutz",
            "-l 100",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("gitlist")
    parsed_args = parser.parse_args(
        ["gitlist", "data/sampleCommitList.txt", "cutz"]
    )
    parsed_args.func(parsed_args)
    print("\n")

    # Actual CLI code
    parsed_args = parser.parse_args()
    if parsed_args.command:
        parsed_args.func(parsed_args)
