__author__ = 'markus'


from .abstractcommand import AbstractCommand
import avanode.cli
import socket


class INIT(AbstractCommand):
    def get_method_name(self):
        pass

    def execute(self, fileconfig, server, datastore, nodeprotocol):
        avanode.cli.write_ava(fileconfig.get_own_id(), "INIT requested", self.requestanalyzer.src_parameter)
        for node in fileconfig.get_neighbors():
            try:
                client = avanode.sockets.Client(node.get_host(), node.get_port(), fileconfig.get_own_id())
                client.send_command(self.requestanalyzer.request_method_arg)
                avanode.cli.write_ava(fileconfig.get_own_id(), "SENT TO NODE: '"+self.requestanalyzer.request_method_arg+"'", node.get_id())
            except socket.error:
                avanode.cli.write_ava(fileconfig.get_own_id(), "ERROR: Connection to Node failed!", node.get_id())
