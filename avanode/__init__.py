__author__ = 'markus'


import avanode.file
from avanode.sockets.server import Server
import avanode.cli
import avanode.client
import threading

import time


class AvaNode(threading.Thread):

    def __init__(self, own_id, config_file, dot_file = None):
        threading.Thread.__init__(self)
        self.config = avanode.file.FileConfig(own_id, config_file, dot_file)

    def run(self):
        port = self.config.get_entry_by_id(self.config.get_own_id()).get_port()
        serv = Server("0.0.0.0", port, self.config)
        try:
            serv.run()
        except KeyboardInterrupt:
            serv.stop_server()
            avanode.cli.write("Server forcefully terminated.")