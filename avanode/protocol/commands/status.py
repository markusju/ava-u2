__author__ = 'markus'

from .abstractcommand import AbstractCommand
from avanode.store.rumortype import RumorType


class STATUS(AbstractCommand):
    def get_method_name(self):
        pass

    def execute(self, fileconfig, server, datastore, nodeprotocol):
        rumors = datastore.get_rumor_list() #type: dict

        buffer = ""

        for k in list(rumors.keys()):
            rumor = rumors[k] #type: RumorType
            buffer += "RUMOR " + rumor.get_rumor() + "\n"
            buffer += "RECVDFROM: " + str(rumor.get_received_from()) + "\n"
            buffer += "SENTTO: " + str(rumor.get_sent_to()) + "\n"
            buffer += "BELIEVED: " + str(rumor.is_believed()) + "\n"

        nodeprotocol.put_line(buffer)


