__author__ = 'markus'

import avanode
import time


class AvaSimu:
    def __init__(self):
        self.config = None

    def gen_env(self, nodes: int, edges: int):
        avanode.file.GraphViz.gen_graph_save_to_file(nodes, edges)
        avanode.file.FileConfig.gen_config_file(nodes)

    def _read_config(self):
        self.config = avanode.file.FileConfig(1, "file.txt", "file.dot")

    def shutdown_all(self, node: int):
        node = self.config.get_entry_by_id(node)
        controla = avanode.client.CommandControl(node.get_host(), node.get_port())
        controla.shutdownall()

    def shutdown(self, node: int):
        node = self.config.get_entry_by_id(node)
        controla = avanode.client.CommandControl(node.get_host(), node.get_port())
        controla.shutdown()

    def start_servers(self, nodes: int):
        # Start Nodes
        for i in range(1, nodes+1):
            node = avanode.AvaNode(i, "file.txt", "file.dot")
            node.start()

    def test_run(self, nodes: int, believe: int, rumor: str, num_node_init: int):
        """
        Exceutes a test run, with a set of given parameters.
        Returns a dictionary containing the test results for each particpating node.
        :param nodes:
        :param believe:
        :param rumor:
        :param num_node_init:
        :return:
        """
        self._read_config()

        # Spread Rumor
        init_node = self.config.get_entry_by_id(num_node_init)

        control = avanode.client.CommandControl(init_node.get_host(), init_node.get_port())
        control.rumor(rumor)

        # Waiting for the rumor to disperse
        time.sleep(15)

        statuses = {}

        # Reading Status...
        for i in range(1, nodes+1):
            node = self.config.get_entry_by_id(i)
            control = avanode.client.CommandControl(node.get_host(), node.get_port())
            statuses[i] = control.status()

        test_results = {}

        for el in list(statuses.keys()):
            for rumortype in statuses[el]:
                if rumortype.get_rumor() not in list(test_results.keys()):
                    test_results[rumortype.get_rumor()] = {0: 0, 1: 0}

                test_results[rumortype.get_rumor()][1] += len(rumortype.get_sent_to())

                if not (len(rumortype.get_received_from()) >= believe):
                    continue


                test_results[rumortype.get_rumor()][0] += 1

        # Average
        test_results[rumortype.get_rumor()][1] /= float(nodes)
        return test_results[rumor]
