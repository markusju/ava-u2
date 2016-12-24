__author__ = 'markus'


import avanode.simu.AvaSimu


nodes = eval(input("Number of nodes: "))
c = eval(input("Believe constant: "))
rumor = input("Rumor: ")
initnode = eval(input("Init: "))

sim = avanode.simu.AvaSimu.AvaSimu()
print(sim.test_run(nodes, c, rumor, initnode))
sim.shutdown_all(1)
