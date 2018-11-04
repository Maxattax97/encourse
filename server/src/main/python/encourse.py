#!/usr/bin/env python
import argparse
import API

# Runs api commands
# create the top-level parser
parser = argparse.ArgumentParser(prog='encourse')
#parser.add_argument('--foo', action='store_true', help='foo help')
subparsers = parser.add_subparsers(help='sub-command help')

# create the parser for the "statistics" command
parser_statistics = subparsers.add_parser('statistics', help='statistics help')
parser_statistics.add_argument("logfile", type=argparse.FileType('r'), help="path to commit log file")
parser_statistics.add_argument("timefile", type=argparse.FileType('r'), help="path to commit time file")
parser_statistics.add_argument("name", help="user name")
parser_statistics.add_argument("tests", help="test case string")
parser_statistics.add_argument("-t", "--timeout", help="time spent timeout")
parser_statistics.add_argument("-l", "--limit", help="ignore file changes above limit")
parser_statistics.add_argument(func=API.get_statistics.testcli)

# parse some argument lists
parser.parse_args(['statistics', 'A', 'B', 'C', 'D'])
#parser.parse_args(['--foo', 'b', '--baz', 'Z'])

