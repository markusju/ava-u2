__author__ = 'markus'

from .abstractcommand import AbstractCommand
import avanode.sockets
import avanode.cli
import socket


class MESSAGE(AbstractCommand):
    def get_method_name(self):
        pass

    def execute(self, fileconfig, server, datastore, nodeprotocol):
        avanode.cli.write_ava(fileconfig.get_own_id(), "RECVD: '" + self.requestanalyzer.request_full +"'", self.requestanalyzer.src_parameter)

        neighbors = fileconfig.get_neighbors()

        for node in neighbors:
            try:
                if node.is_already_contacted():
                    avanode.cli.write_ava(fileconfig.get_own_id(), "NOTICE: Host was already contacted and will not be contacted again.", node.get_id())
                    continue

                client = avanode.sockets.Client(node.get_host(), node.get_port(), fileconfig.get_own_id())
                client.put_line(str(fileconfig.get_own_id()))

                node.set_already_contacted()
                avanode.cli.write_ava(fileconfig.get_own_id(), "SENT: '"+str(fileconfig.get_own_id())+"'", node.get_id())

            except socket.error:
                avanode.cli.write_ava(fileconfig.get_own_id(), "ERROR: Connection to host failed!", node.get_id())
