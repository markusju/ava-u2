
import socketserver
from avanode.file.fileconfig import FileConfig
from avanode.store import DataStore
import avanode.protocol


class Server:
        def __init__(self, bindaddr: str, port: int, conf: FileConfig):
            self.conf = conf
            self.datastore = DataStore()
            self.port = port
            self.server = socketserver.ThreadingTCPServer((bindaddr, port), self._create_handler(), False)

        def run(self):
            """
            Start Server and bind to socket
            :return:
            """
            self.server.allow_reuse_address = True # Prevent 'cannot bind to address' errors on restart
            self.server.server_bind()     # Manually bind, to support allow_reuse_address
            self.server.server_activate()
            avanode.cli.write_ava(self.conf.get_own_id(), "Server listening on port "+str(self.port))
            self.server.serve_forever()

        def stop_server(self):
            self.server.shutdown()

        def _create_handler(self):
            conf = self.conf
            datastore = self.datastore

            class MyTCPHandler(socketserver.StreamRequestHandler):
                def handle(self):
                    try:
                        avanode.protocol.NodeProtocol(self.rfile, self.wfile, conf, datastore, self).run()
                    finally:
                        self.finish()

                    # work = worker.Worker(self.rfile, self.wfile, conf, datastore, self)
                    # work.start()
            return MyTCPHandler
