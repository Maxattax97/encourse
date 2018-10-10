from __future__ import print_function
from datetime import date, timedelta
import sys


def is_number(string):
    try:
        int(string)
        return True
    except ValueError:
        return False


def date_string(date):
    # print(date)
    return date.isoformat()


def time_string(seconds):
    """
    Converts a datetime object to a concise string
    returns a String:
    'X days' if the number of days is greater than 0
    'X hours' if the number of hours is greater than 0
    'X minutes' if the number of minutes if greater than 1
    'Not started' otherwise
    """

    if seconds < 60:
        return "None"
    minutes, seconds = divmod(seconds, 60)
    if minutes < 60:
        return "{} minutes".format(int(minutes))
    hours, minutes = divmod(minutes, 60)
    if hours < 24:
        return "{} hours".format(int(hours))
    days, hours = divmod(hours, 24)
    return "{} days".format(int(days))


def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)
