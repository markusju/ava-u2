__author__ = 'markus'

import argparse
import avanode
import avanode.file.graphviz


parser = argparse.ArgumentParser(description="AVA Node")

parser.add_argument("id", type=int)
parser.add_argument("--conf-file", type=str, default="file.txt")
parser.add_argument("--dot-file", type=str, default="file.dot")
parser.add_argument("--neighbors", type=str, choices=["dotfile", "random"], default="dotfile")

args = parser.parse_args()


if args.neighbors == "random":
    node = avanode.AvaNode(args.id, args.conf_file)
    node.run()

if args.neighbors == "dotfile":
    node = avanode.AvaNode(args.id, args.conf_file, args.dot_file)
    node.run()