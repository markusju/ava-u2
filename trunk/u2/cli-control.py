__author__ = 'markus'

import avanode.client.commandcontrol
import argparse

parser = argparse.ArgumentParser(description="AVA Node Command and Control Node")

parser.add_argument("host", type=str)
parser.add_argument("port", type=int)
parser.add_argument("action", type=str, choices=["rumor", "shutdown", "shutdownall", "init", "status"])
parser.add_argument("action_args", type=str)

args = parser.parse_args()

client = avanode.client.commandcontrol.CommandControl(args.host, args.port)

if args.action == "rumor":
    client.rumor(args.action_args)

if args.action == "shutdown":
    client.shutdown()

if args.action == "shutdownall":
    client.shutdownall()

if args.action == "init":
    client.init(args.action_args)

if args.action == "status":
    print(client.status())
