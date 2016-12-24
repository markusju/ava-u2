__author__ = 'markus'

import avanode.protocol.analysis
from avanode.file.fileconfig import FileConfig
from avanode.store.datastore import DataStore
from avanode.sockets.server import Server
from . import commands


class NodeProtocol:
    def __init__(self, rfile, wfile, conf: FileConfig, datastore: DataStore, server: Server):
        self.rfile = rfile
        self.wfile = wfile
        self.currentLine = None
        self.conf = conf
        self.server = server
        self.datastore = datastore

    def run(self):
        """
        Protocol definition.
        Defines the different actions
        :return:
        """

        try:
            request = avanode.protocol.analysis.RequestAnalyzer(self)
            cmd = commands.CommandFactory(request)
            cmd.get_command().execute(self.conf, self.server, self.datastore, self)
        except Exception as e:
            avanode.cli.write_ava(self.conf.get_own_id(), "An Error occured while processing a message received from a client: "+e.message, None)

    def read_line(self):
        """
        Reads the next line
        Raises IOError when no more lines are available
        :return:
        """
        line = self.rfile.readline().decode("utf-8")
        # Remove Newline from Content
        if not line:
            raise IOError("No more data")
        self.currentLine = line

    def get_current_line(self):
        """
        Returns the current line
        :return:
        """
        return str(self.currentLine)

    def put_line(self, line):
        """
        Writes a line of text
        :param line:
        :return:
        """
        if not isinstance(line, str):
            raise ValueError("Only Strings are supported for put_line")
        self.wfile.write(line+"\n")

