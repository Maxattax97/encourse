#!/usr/bin/env python
import argparse
import API
import sys


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
    parser.set_defaults(func=API.changes.jsonprint)


def setup_cheating(parser):
    parser.add_argument(
        "visiblefile",
        type=argparse.FileType("r"),
        help="path to historic progress file for visible test cases",
    )
    parser.add_argument(
        "hiddenfile",
        type=argparse.FileType("r"),
        help="path to historic progress file for hidden test cases",
    )
    parser.add_argument("logfile", type=argparse.FileType("r"), help="path to log file")
    parser.add_argument("-t", "--timeout", help="time spent timeout")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.add_argument(
        "-v",
        "--velocity",
        help="the maximum daily progress per hour spent before a student is flagged as suspicious",
    )
    parser.add_argument(
        "-r",
        "--rate",
        help="the maximum daily progress per commit before a student is flagged as suspicious",
    )
    parser.set_defaults(func=API.cheating.jsonprint)


def setup_commitcount(parser):
    """Configure commitcount parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument("name", help="user name")
    parser.set_defaults(func=API.commitcount.jsonprint)


def setup_class_progress(parser):
    parser.add_argument(
        "visible", type=argparse.FileType("r"), help="path to visible test score file"
    )
    parser.add_argument(
        "hidden", type=argparse.FileType("r"), help="path to hidden test score file"
    )
    parser.set_defaults(func=API.class_progress.jsonprint)


def setup_gitlist(parser):
    """Configure gitlist parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument("name", help="user name")
    parser.set_defaults(func=API.gitlist.jsonprint)

def setup_identical(parser):
    """ Configure identical parser"""
    parser.add_argument(
        "diff_file", type=argparse.FileType("r"), help="path to diff file"
    )
    parser.set_defaults(func=API.identical.jsonprint)


def setup_stats(parser):
    """Configure stats parser"""
    parser.add_argument(
        "logfile", type=argparse.FileType("r"), help="path to commit log file"
    )
    parser.add_argument(
        "visiblefile", type=argparse.FileType("r"), help="path to visible test file"
    )
    parser.add_argument(
        "hiddenfile", type=argparse.FileType("r"), help="path to hidden test file"
    )
    parser.add_argument("-t", "--timeout", type=float, help="time spent timeout")
    parser.add_argument("-l", "--limit", type=int, help="ignore file changes above limit")
    parser.set_defaults(func=API.stats.jsonprint)


def setup_student_progress(parser):
    """Configure student progress parser"""
    parser.add_argument(
        "visiblefile",
        type=argparse.FileType("r"),
        help="path to historic progress file for visible test cases",
    )
    parser.add_argument(
        "hiddenfile",
        type=argparse.FileType("r"),
        help="path to historic progress file for hidden test cases",
    )
    parser.add_argument(
        "timefile", type=argparse.FileType("r"), help="path to time file"
    )
    parser.add_argument("name", help="user name")
    parser.set_defaults(func=API.student_progress.jsonprint)


def setup_test_summary(parser):
    parser.add_argument(
        "visible", type=argparse.FileType("r"), help="path to visible test score file"
    )
    parser.add_argument(
        "hidden", type=argparse.FileType("r"), help="path to hidden test score file"
    )
    parser.set_defaults(func=API.test_summary.jsonprint)


def setup_velocity(parser):
    parser.add_argument(
        "visiblefile",
        type=argparse.FileType("r"),
        help="path to historic progress file for visible test cases",
    )
    parser.add_argument(
        "hiddenfile",
        type=argparse.FileType("r"),
        help="path to historic progress file for hidden test cases",
    )
    parser.add_argument("logfile", type=argparse.FileType("r"), help="path to log file")
    parser.add_argument("name", help="user name")
    parser.add_argument("-t", "--timeout", help="time spent timeout")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.add_argument(
        "-v",
        "--velocity",
        help="the maximum daily progress per hour spent before a student is flagged as suspicious",
    )
    parser.add_argument(
        "-r",
        "--rate",
        help="the maximum daily progress per commit before a student is flagged as suspicious",
    )
    parser.set_defaults(func=API.velocity.jsonprint)


if __name__ == "__main__":
    # Create the top-level parser
    parser = argparse.ArgumentParser(prog="encourse")
    subparsers = parser.add_subparsers(dest="command")

    changes_parser = subparsers.add_parser("changes")
    setup_changes(changes_parser)

    cheating_parser = subparsers.add_parser("cheating")
    setup_cheating(cheating_parser)

    class_progress_parser = subparsers.add_parser("class-progress")
    setup_class_progress(class_progress_parser)

    commitcount_parser = subparsers.add_parser("commitcount")
    setup_commitcount(commitcount_parser)

    gitlist_parser = subparsers.add_parser("gitlist")
    setup_gitlist(gitlist_parser)

    identical_parser = subparsers.add_parser("identical")
    setup_identical(identical_parser)

    stats_parser = subparsers.add_parser("stats")
    setup_stats(stats_parser)

    student_progress_parser = subparsers.add_parser("student-progress")
    setup_student_progress(student_progress_parser)

    test_summary_parser = subparsers.add_parser("test-summary")
    setup_test_summary(test_summary_parser)

    velocity_parser = subparsers.add_parser("velocity")
    setup_velocity(velocity_parser)

    # Actual CLI code
    if len(sys.argv) == 1:
        parser.print_help(sys.stderr)
        sys.exit(1)
    parsed_args = parser.parse_args()
    if parsed_args.command:
        parsed_args.func(parsed_args)

    ################# Tests ##################
    """
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
    parsed_args = parser.parse_args(["gitlist", "data/sampleCommitList.txt", "cutz"])
    parsed_args.func(parsed_args)
    print("\n")

    print("student-progress")
    parsed_args = parser.parse_args(
        [
            "student-progress",
            "data/sampleTestsDay.txt",
            "data/sampleTestsDay.txt",
            "data/sampleCountsDay.txt",
            "cutz",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("class-progress")
    parsed_args = parser.parse_args(
        [
            "class-progress",
            "data/sampleVisibleTestCases.txt",
            "data/sampleHiddenTestCases.txt",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("test-summary")
    parsed_args = parser.parse_args(
        [
            "test-summary",
            "data/sampleVisibleTestCases.txt",
            "data/sampleHiddenTestCases.txt",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("velocity")
    parsed_args = parser.parse_args(
        [
            "velocity",
            "data/sampleTestsDay.txt",
            "data/sampleTestsDay.txt",
            "data/sampleCommitList.txt",
            "cutz",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")

    print("cheating")
    parsed_args = parser.parse_args(
        [
            "cheating",
            "data/sampleTestsDay.txt",
            "data/sampleTestsDay.txt",
            "data/sampleCommitList.txt",
        ]
    )
    parsed_args.func(parsed_args)
    print("\n")
    """
