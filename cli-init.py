__author__ = 'markus'

import argparse
import avanode
import avanode.file.graphviz
import avanode.cli


parser = argparse.ArgumentParser(description="AVA Node Init Tool")

parser.add_argument("nodes", type=int)
parser.add_argument("edges", type=int)

args = parser.parse_args()

try:
    avanode.file.GraphViz.gen_graph_save_to_file(args.nodes, args.edges)
    avanode.file.FileConfig.gen_config_file(args.nodes)
except ValueError as e:
    avanode.cli.write_ava(None, "ERROR: Could not initialize: "+e.message)
