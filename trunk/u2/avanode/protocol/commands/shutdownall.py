__author__ = 'markus'


from .abstractcommand import AbstractCommand
import avanode.cli
import socket
from threading import Thread


class SHUTDOWNALL(AbstractCommand):
    def get_method_name(self):
        pass

    def execute(self, fileconfig, server, datastore, nodeprotocol):
        avanode.cli.write_ava(fileconfig.get_own_id(), "Server shutdown of all nodes requested", self.requestanalyzer.src_parameter)
        server.finish()
        server.server.shutdown()

        for node in fileconfig.get_neighbors():
            #self.connect_send_shutdown(node, fileconfig)
            t = Thread(target=self.connect_send_shutdown, args=(node, fileconfig))
            t.setDaemon(True)
            t.start()

    def connect_send_shutdown(self, node, fileconfig):
        try:
            client = avanode.sockets.Client(node.get_host(), node.get_port(), fileconfig.get_own_id())
            client.put_line2("SHUTDOWNALL")
        except socket.error:
                avanode.cli.write_ava(fileconfig.get_own_id(), "Shutdown of remote node did not succeed. Connection refused.", node.get_id())
