import json
import yaml
import requests
import time
import math
import random

# Before you can use this script you will have to create a file
# called settings.yaml with your login and password to access the API

# You also need two modules pyYAML and requests. They are available on pip.

# It should be in the YAML format as following
# rootUrl: <APIUrl>
# login: <login>
# password: <password> 

with open('settings.yaml', 'r') as f:
    settings = yaml.load(f)

#function to generate converging coordinates for each node
def computeConvergingNumber(convergeParam, step, goal):
	if (step != 0):
		return goal + math.sin(convergeParam*step)/step
	else:
		return goal + math.sin(convergeParam)

def distance(coordinateOne,coordinateTwo):
	return math.sqrt(math.pow(coordinateOne['x']-coordinateTwo['x'],2)+math.pow(coordinateOne['y']-coordinateTwo['y'],2))

headers = {'content-type': 'application/json', 'Accept': 'application/json'}

print("====================================")
print("|         Database clean up        |")
print("====================================")


ExistingNetworksJson = requests.get(settings["rootUrl"]+"/networks/", auth=(settings["login"], settings["password"]), headers=headers)
print("Retrieve network list status code : "+str(ExistingNetworksJson.status_code))
ExistingNetworks = json.loads(ExistingNetworksJson.text)

for n in ExistingNetworks:
	print("DELETE Network : "+str(n))
	response = requests.delete(settings["rootUrl"]+"/networks/"+str(n["id"]), auth=(settings["login"], settings["password"]))
	print ("DELETE status : "+str(response.status_code))

print("\n====================================")
print("|        Dummy data creation       |")
print("====================================")

print("\n -----> Network <-----")

network = {'networkName':'Python network'}
response = requests.post(settings["rootUrl"]+"/networks/", data=json.dumps(network), auth=(settings["login"], settings["password"]), headers=headers)
print("network POST status : "+str(response.status_code))

networksJson = requests.get(settings["rootUrl"]+"/networks/", auth=(settings["login"], settings["password"]), headers=headers)
network = json.loads(networksJson.text)
network = network[0]
# print("network : "+str(network))


print("\n -----> Nodes <-----")

nodeNumber = 10
nodes = []

for i in range(nodeNumber):
	nodes.append({'nodeName':'nodename-'+str(i),'networkId':network['id']})

for n in nodes:
	response = requests.post(settings["rootUrl"]+"/nodes/", data=json.dumps(n), auth=(settings["login"], settings["password"]), headers=headers)
	print(str(n)+"\n ---> POST status : "+ str(response.status_code))

nodesJson = requests.get(settings["rootUrl"]+"/nodes/", auth=(settings["login"], settings["password"]), headers=headers)
nodes = json.loads(nodesJson.text)


print("\n -----> Coordinates <-----")
iterations = 20

# Aimed coordinates for each node
goalCoordinates = []

for i in range (nodeNumber):
	goalCoordinates.append({'x': random.randint(0, 20), 'y': random.randint(0, 20)})

# First index represents the node number, second index, the iteration
coordinates = [[0 for x in range(iterations)] for x in range(len(nodes))] 

for i in range(len(nodes)):
	print ("\n Node Initialization")
	init = {'nodeId': nodes[i]['id']}
	response = requests.post(settings["rootUrl"]+"/initTimes/", data=json.dumps(init), auth=(settings["login"], settings["password"]), headers=headers)
	print(str(init)+"\n ---> POST status : "+ str(response.status_code))	

	for j in range(iterations):
		coordinates[i][j] = {'nodeId':nodes[i]['id'], 'x':computeConvergingNumber(i,j,goalCoordinates[i]['x']), 'y':computeConvergingNumber(i,j,goalCoordinates[i]['y'])}
		response = requests.post(settings["rootUrl"]+"/coordinates/", data=json.dumps(coordinates[i][j]), auth=(settings["login"], settings["password"]), headers=headers)
		print(str(coordinates[i][j])+"\n ---> POST status : "+ str(response.status_code))


print("\n -----> Distances <-----")

# First index : iterations - second: local node - third: distant node
distanceMatrix = [[[0 for x in range(len(nodes))]for x in range(len(nodes))] for x in range(iterations)] 

for i in range (iterations):
	for j in range(len(nodes)):
		for k in range (len(nodes)):
			distanceMatrix[i][j][k] = {'localNodeId':nodes[j]['id'], 'distantNodeId': nodes[k]['id'], 'distance': distance(coordinates[j][i], coordinates[k][i])}

for i in range(iterations):
	for j in range(len(nodes)):
		closeNodes = sorted(distanceMatrix[i][j], key= lambda closeNode: closeNode['distance'])[:5]
		response = requests.post(settings["rootUrl"]+"/closeNodes/list", data=json.dumps(closeNodes), auth=(settings["login"], settings["password"]), headers=headers)
		print(str(closeNodes)+"\n ---> POST status : "+ str(response.status_code))