__author__ = 'markus'


from abc import ABCMeta, abstractmethod

from avanode.protocol.analysis import RequestAnalyzer
from avanode.file.fileconfig import FileConfig
from avanode.sockets.server import Server
from avanode.store.datastore import DataStore
from avanode.protocol.nodeprotocol import NodeProtocol


class AbstractCommand(metaclass=ABCMeta):
    def __init__(self, requestanalyzer: RequestAnalyzer):
        if not isinstance(requestanalyzer, RequestAnalyzer):
            raise ValueError("Argument must be instance of RequestAnalyzer")
        self.requestanalyzer = requestanalyzer

    @abstractmethod
    def get_method_name(self):
        pass

    @abstractmethod
    def execute(self, fileconfig: FileConfig, server: Server, datastore: DataStore, nodeprotocol: NodeProtocol):
        """
        Abstract method for all commands. Defines the behavior of a certain command
        :param fileconfig:
        :param server:
        :param datastore:
        :param nodeprotocol:
        :return:
        """
        pass