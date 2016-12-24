__author__ = 'markus'

import avanode
import avanode.file.graphviz
import avanode.cli
import avanode.client
import avanode.file.fileconfig
import avanode.simu.AvaSimu
import time


nodes_arr = []

tests = [
    #n  , m   , c,     r, i
    (100, 150, 3, "a.1", 1),
    (100, 150, 3, "a.2", 1),
    (100, 150, 3, "a.3", 1),
    (100, 150, 6, "b.1", 1),
    (100, 150, 6, "b.2", 1),
    (100, 150, 6, "b.3", 1),
    (100, 150, 12, "c.1", 1),
    (100, 150, 12, "c.2", 1),
    (100, 150, 12, "c.3", 1),
    (100, 200, 2, "d.1", 1),
    (100, 200, 2, "d.2", 1),
    (100, 200, 2, "d.3", 1),
    (500, 501, 3, "e.1", 1),
    (500, 501, 3, "e.2", 1),
    (500, 501, 3, "e.3", 1),
    (500, 900, 3, "f.1", 1),
    (500, 900, 3, "f.2", 1),
    (500, 900, 3, "f.3", 1),
    (1000, 1900, 3, "g.1", 1),
    (1000, 1900, 3, "g.2", 1)
]

results = {}


def all_nodes_down(nodes_arr):
    for node in nodes_arr:
        if node.isAlive():
            return False
    return True


def all_nodes_up(nodes_arr):
    for node in nodes_arr:
        if not node.isAlive():
            return False
    return True


def wait_for_shutdown_of_all_nodes(nodes_arr):
    while not all_nodes_down(nodes_arr):
        pass


def wait_for_start_of_all_nodes(nodes_arr):
    while not all_nodes_up(nodes_arr):
        pass


def perform_test(nodes, edges, c, rumor, init):
    del nodes_arr[:]

    sim = avanode.simu.AvaSimu.AvaSimu()
    # Generiere neue Testumgebung
    sim.gen_env(nodes, edges)

    # Starte Nodes
    for i in range(1, nodes+1):
        node = avanode.AvaNode(i, "file.txt", "file.dot")
        nodes_arr.append(node)
        node.start()

    # Block until all nodes have started...
    wait_for_start_of_all_nodes(nodes_arr)

    # Execute test...
    res = sim.test_run(nodes, c, rumor, init)

    # Shutdown nodes...
    for i in range(1, nodes+1):
        sim.shutdown(i)

    # Block until all nodes are down
    wait_for_shutdown_of_all_nodes(nodes_arr)
    time.sleep(1)
    return res


# Execute tests...

for el in tests:
    results[el] = perform_test(*el)


print("Test completed:")
print("Nodes\tEdges\tBelieveCount\tRumor\tInitNode\t\tBelievers\t\tPercentage\t\tAvgNodeDeg")
print("")

for el in list(results.keys()):
    buffer = str(el[0]) + "\t\t" + str(el[1]) + "\t\t" + str(el[2]) + "\t\t\t\t" + str(el[3]) + "\t\t" + str(el[4]) + "\t\t\t\t" + str(results[el][0]) + "\t\t\t\t" + str((results[el][0]*100.0/el[0])) + "\t\t\t\t" + str(results[el][1])
    print(buffer)


print("table data....")

for el in list(results.keys()):
    buffer = str(el[0]) + "\t" + str(el[1]) + "\t" + str(el[2]) + "\t" + str(el[3]) + "\t" + str(el[4]) + "\t" + str(results[el][0]) + "\t" + str((results[el][0]*100.0/el[0])) + "\t" + str(results[el][1])
    print(buffer)