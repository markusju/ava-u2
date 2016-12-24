__author__ = 'markus'


class RumorType:
    def __init__(self, rumor, received_from=None):
        self._received_from = set()
        if received_from is not None:
            self._received_from.add(int(received_from))
        self._rumor = rumor
        self._sent_to = set()

    def get_rumor(self):
        return self._rumor

    def get_received_from(self):
        """
        Returns all Nodes from which a rumor has been received
        :return:
        """
        return self._received_from

    def add_received_from(self, received_from: int):
        """
        Adds a node to the list of senders
        :param received_from:
        :return:
        """
        self._received_from.add(int(received_from))

    def get_sent_to(self):
        """
        Returns all Nodes to which the rumor has been sent/forwarded
        :return:
        """
        return self._sent_to

    def add_sent_to(self, sent_to: int):
        """
        Adds a node to the list of recipients
        :param sent_to:
        :return:
        """
        self._sent_to.add(int(sent_to))

    def is_believed(self) -> bool:
        """
        Tells whether a node believes the rumor
        :return:
        """
        return len(self.get_received_from()) >= 3

    def __str__(self):
        return "<"+str(self.get_rumor())+" RECVFROM: "+str(self.get_received_from())+" SENTTO: "+str(self.get_sent_to())+">"

    def __repr__(self):
        return str(self)
