__author__ = 'markus'


from .abstractcommand import AbstractCommand
import avanode.cli
import socket
import time


class RUMOR(AbstractCommand):
    def get_method_name(self):
        pass

    def execute(self, fileconfig, server, datastore, nodeprotocol):
        src = self.requestanalyzer.src_parameter
        if src is None:
            src = fileconfig.get_own_id()

        message = self.requestanalyzer.request_method_arg
        avanode.cli.write_ava(fileconfig.get_own_id(), "RUMOR received: '"+message+"'", src)
        rumortype = datastore.add_rumor(message, src)

        # Geruecht schon gehoert? Dann keinem mehr erzaehlen
        if len(rumortype.get_received_from()) > 1:
            return

        for node in fileconfig.get_neighbors(): # type: avanode.file.FileEntry
            try:
                # Nicht an den Nachbarn senden, der mir die Daten geschickt hat...
                if node.get_id() in rumortype.get_received_from():
                    avanode.cli.write_ava(fileconfig.get_own_id(), "NOTICE: Not sending rumor to this host, as it originates from it.", node.get_id())
                    continue
                client = avanode.sockets.Client(node.get_host(), node.get_port(), fileconfig.get_own_id())

                client.send_command("RUMOR "+self.requestanalyzer.request_method_arg)
                rumortype.add_sent_to(node.get_id())
                avanode.cli.write_ava(fileconfig.get_own_id(), "SENT: 'RUMOR "+self.requestanalyzer.request_method_arg+"'", node.get_id())
            except socket.error as e:
                print(e)
                avanode.cli.write_ava(fileconfig.get_own_id(), "ERROR: Connection to host failed! ", node.get_id())