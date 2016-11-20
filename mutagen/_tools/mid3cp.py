# -*- coding: utf-8 -*-
# Copyright 2014 Marcus Sundman

# This program is free software; you can redistribute it and/or modify
# it under the terms of version 2 of the GNU General Public License as
# published by the Free Software Foundation.

"""A program replicating the functionality of id3lib's id3cp, using mutagen for
tag loading and saving.
"""

import sys
import os.path

import mutagen
import mutagen.id3
from mutagen._senf import print_, argv
from mutagen._compat import text_type

from ._util import SignalHandler, OptionParser


VERSION = (0, 1)
_sig = SignalHandler()


def printerr(*args, **kwargs):
    kwargs.setdefault("file", sys.stderr)
    print_(*args, **kwargs)


class ID3OptionParser(OptionParser):
    def __init__(self):
        mutagen_version = mutagen.version_string
        my_version = ".".join(map(str, VERSION))
        version = "mid3cp %s\nUses Mutagen %s" % (my_version, mutagen_version)
        self.disable_interspersed_args()
        OptionParser.__init__(
            self, version=version,
            usage="%prog [option(s)] <src> <dst>",
            description=("Copies ID3 tags from <src> to <dst>. Mutagen-based "
                         "replacement for id3lib's id3cp."))


def copy(src, dst, write_v1=True, excluded_tags=None, verbose=False):
    """Returns 0 on success"""

    if excluded_tags is None:
        excluded_tags = []

    try:
        id3 = mutagen.id3.ID3(src, translate=False)
    except mutagen.id3.ID3NoHeaderError:
        print_(u"No ID3 header found in ", src, file=sys.stderr)
        return 1
    except Exception as err:
        print_(str(err), file=sys.stderr)
        return 1
    else:
        if verbose:
            print_(u"File", src, u"contains:", file=sys.stderr)
            print_(id3.pprint(), file=sys.stderr)

        for tag in excluded_tags:
            id3.delall(tag)

        # if the source is 2.3 save it as 2.3
        if id3.version < (2, 4, 0):
            id3.update_to_v23()
            v2_version = 3
        else:
            id3.update_to_v24()
            v2_version = 4

        try:
            id3.save(dst, v1=(2 if write_v1 else 0), v2_version=v2_version)
        except Exception as err:
            print_(u"Error saving", dst, u":\n%s" % text_type(err),
                   file=sys.stderr)
            return 1
        else:
            if verbose:
                print_(u"Successfully saved", dst, file=sys.stderr)
            return 0


def main(argv):
    parser = ID3OptionParser()
    parser.add_option("-v", "--verbose", action="store_true", dest="verbose",
                      help="print out saved tags", default=False)
    parser.add_option("--write-v1", action="store_true", dest="write_v1",
                      default=False, help="write id3v1 tags")
    parser.add_option("-x", "--exclude-tag", metavar="TAG", action="append",
                      dest="x", help="exclude the specified tag", default=[])
    (options, args) = parser.parse_args(argv[1:])

    if len(args) != 2:
        parser.print_help(file=sys.stderr)
        return 1

    (src, dst) = args

    if not os.path.isfile(src):
        print_(u"File not found:", src, file=sys.stderr)
        parser.print_help(file=sys.stderr)
        return 1

    if not os.path.isfile(dst):
        printerr(u"File not found:", dst, file=sys.stderr)
        parser.print_help(file=sys.stderr)
        return 1

    # Strip tags - "-x FOO" adds whitespace at the beginning of the tag name
    excluded_tags = [x.strip() for x in options.x]

    with _sig.block():
        return copy(src, dst, options.write_v1, excluded_tags, options.verbose)


def entry_point():
    _sig.init()
    return main(argv)
