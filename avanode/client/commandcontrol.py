__author__ = 'markus'

from avanode.sockets.client import Client
from avanode.store.rumortype import RumorType
import ast


class CommandControl ():
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.client = Client(self.host, self.port, None)

    def rumor(self, rumor):
        """
        Spread a given rumor
        :param rumor:
        :return:
        """
        self.client.put_line("RUMOR "+rumor+"\n")

    def init(self, msg):
        """
        Send a init message.
        :param msg:
        :return:
        """
        self.client.put_line("INIT "+msg+"\n")

    def shutdownall(self):
        """
        Shutdown all nodes.
        :return:
        """
        self.client.put_line("SHUTDOWNALL\n")

    def shutdown(self):
        """
        Shutdown a single node.
        :return:
        """
        self.client.put_line("SHUTDOWN\n")

    def status(self):
        """
        Read status of remote node and deserialize the data..
        :return:
        """
        resp = self.client.put_line_read_response("STATUS\n")
        truncated = []

        for line in resp:
            truncated.append(line[:-1])

        rumor_stack = []

        # Really dirty parsing...

        for line in truncated:
            if "RUMOR" in line:
                a = line.split(" ")[1]
                rumor_stack.append(RumorType(a))
            if "RECVDFROM" in line:
                b = line.split(": ")[1]
                if "set(" in line:
                    b = b.split("set(")[1].split(")")[0]
                    obj = ast.literal_eval(b)
                    for el in obj:
                        rumor_stack[-1].add_received_from(el)
            if "SENTTO" in line:
                c = line.split(": ")[1]
                if "set(" in line:
                    c = c.split("set(")[1].split(")")[0]
                    obj = ast.literal_eval(c)
                    for el in obj:
                        rumor_stack[-1].add_sent_to(el)
            if "BELIEVED" in line:
                d = line.split(": ")[1]

        return rumor_stack
