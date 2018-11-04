#!/usr/bin/env python
import argparse
import API

# Runs api commands
# create the top-level parser
parser = argparse.ArgumentParser(prog='encourse')
#parser.add_argument('--foo', action='store_true', help='foo help')
subparsers = parser.add_subparsers(help='sub-command help', dest='command')

# create the parser for the "statistics" command
parser_statistics = subparsers.add_parser('statistics', help='statistics help')
parser_statistics.add_argument("logfile", type=argparse.FileType('r'), help="path to commit log file")
parser_statistics.add_argument("timefile", type=argparse.FileType('r'), help="path to commit time file")
parser_statistics.add_argument("name", help="user name")
parser_statistics.add_argument("tests", help="test case string")
parser_statistics.add_argument("-t", "--timeout", help="time spent timeout")
parser_statistics.add_argument("-l", "--limit", help="ignore file changes above limit")
parser_statistics.set_defaults(func=API.get_statistics.jsonprint)

bar_parser = subparsers.add_parser('bar')

# parse some argument lists
parsed_args = parser.parse_args(['statistics', 'test_datasets/sampleCommitList.txt', 'test_datasets/sampleCountsDay.txt', 'cutz', 'cutz;Test1:P:1.0;Test2:P:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0'])
#parser.parse_args(['--foo', 'b', '--baz', 'Z'])

if parsed_args.command == "statistics":
    parsed_args.func(parsed_args)
