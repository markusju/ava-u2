__author__ = 'markus'


from .abstractcommand import AbstractCommand
import avanode.cli


class SHUTDOWN(AbstractCommand):
    def get_method_name(self):
        pass

    def execute(self, fileconfig, server, datastore, nodeprotocol):
        avanode.cli.write_ava(fileconfig.get_own_id(), "Server shutdown requested", self.requestanalyzer.src_parameter)
        server.server.shutdown()
