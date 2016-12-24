__author__ = 'markus'

import random

from typing import List

from .fileentry import FileEntry
from .graphviz import GraphViz


class FileConfig:

    def __init__(self, own_id, filename, filenamegraph=None):

        self.filename = filename
        self.filenamegraph = filenamegraph

        self.configdict = {}

        self.own_id = own_id
        self.neighbors = None

        if filenamegraph is not None:
            self.graphviz = GraphViz(self.filenamegraph, own_id)
        else:
            self.graphviz = None

        self.__read_from_file(filename)
        self.__read_neighbors()

    def __read_neighbors(self):
        """
        Reads the neighbors from graphiz or chooses random neighbors, depending on
        constructor input (filenamegraph)
        :return:
        """
        if self.filenamegraph is None:
            self.neighbors = self.get_n_samples(3)
        else:
            ids = self.graphviz.get_neighbors()
            self.neighbors = []

            for id in ids:
                self.neighbors.append(self.configdict[id])

    def __read_from_file(self, filename):
        """
        Parses the configuration file
        :param filename:
        :return:
        """
        with open(filename) as f:
            for line in f.readlines():
                parts = line.split(" ")

                if len(parts) != 2:
                    raise Exception("Malformed Config File")

                file_id = int(parts[0])
                file_host_port = parts[1]

                parts = file_host_port.split(":")

                if len(parts) != 2:
                    raise Exception("Malformed Config File")

                file_host = parts[0]
                file_port = int(parts[1])

                self.configdict[file_id] = FileEntry(file_id, file_host, file_port)

    def get_n_samples(self, n):
        """
        Picks n samples from all available nodes
        :param n:
        :return:
        """
        tempdict = self.configdict.copy()
        tempdict.pop(self.own_id)
        return random.sample(list(tempdict.values()), n)

    def get_neighbors(self) -> List[FileEntry]:
        """
        Returns the neighbors associated with the defined node id
        :return: avanode.file.fileentry.FileEntry
        """
        return self.neighbors

    def get_own_id(self) -> int:
        """
        Returns the id
        :return:
        """
        return self.own_id

    def get_entry_by_id(self, id) -> FileEntry:
        """
        returns the config data for a given id
        :param id:
        :return: avanode.file.FileEntry
        """
        id = int(id)
        try:
            retval = self.configdict[id]
            if not isinstance(retval, FileEntry):
                raise ValueError("Internal Typing Error")
            return retval
        except KeyError:
            raise Exception("ID does not exist in Config")

    @staticmethod
    def gen_config_file(max_id, filename = 'file.txt'):
        """
        Generates a config file
        :param max_id:
        :param filename:
        :return:
        """
        text_file = open(filename, "w")
        for i in range(1, max_id+1):
            text_file.write(str(i)+" 127.0.0.1:"+str(int(5000+(i-1)))+"\n")
        text_file.close()
