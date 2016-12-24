__author__ = 'markus'

from .rumortype import RumorType


class DataStore:

    def __init__(self):
        self._rumors = {}

    def get_rumor(self, rumor: str) -> RumorType:
        """
        Returns a rumor object by its textual identifier
        :param rumor:
        :return:
        """
        return self._rumors[rumor]

    def get_rumor_list(self):
        """
        Returns a list of Rumors
        :return:
        """
        return self._rumors

    def add_rumor(self, rumor: str, received_from: int) -> RumorType:
        """
        Adds a new rumor the datastore
        :param rumor:
        :param received_from:
        :return:
        """

        if rumor in list(self._rumors.keys()):
            dat = self._rumors[rumor]
            dat.add_received_from(received_from)
            return dat

        self._rumors[rumor] = RumorType(rumor, received_from)
        return self._rumors[rumor]
