Monitoring Vivaldi Stack [![Build Status](https://travis-ci.org/callicles/VivaldiMonitoringPlay.png?branch=master)](https://travis-ci.org/callicles/VivaldiMonitoringPlay)
=====================================

This is a play 2.2.1 scala application for monitoring Vivaldi coordinates generated from [this project](https://github.com/BeyondTheClouds/VivaldiGSI). This Project provides the full stack for the monitoring.

1. A REST API to aggregate the data
2. A web interface to visualise the data.

## Deploy

Check the play documentation [here](http://www.playframework.com/documentation/2.2.1/Production)

[Here](http://vivaldi-monitoring-demo.herokuapp.com/) is a running demo

## Test

To test this app there is a data generator provided in the `PythonDummyDataGenerator` folder. To use it, you just have to configure the settings file with the adress where you are deploying this app.

Then you just have to execute. It will clean the dataBase and create a `Python Network` with dummy data in it.

## REST API

_IMPORTANT NOTE_ : As for now the POSTing endpoints do not have a protection of any kind : no authentification or query checking ...


### Endpoints

    GET           /networks/
    GET           /networks/:id/nodes                                 
    GET           /networks/:id/coordinates
    POST          /networks/                                         
    DELETE        /networks/:id                                     
    
    GET           /nodes/                                           
    GET           /nodes/:id/initTimes                                
    GET           /nodes/:nodeId/initTimes/:initId/coordinates       
    POST          /nodes/                                           
    DELETE        /nodes/:id                                          
    
    GET           /initTimes/                                        
    POST          /initTimes/                                        
    DELETE        /initTimes/:id                                     
    
    GET           /coordinates/                                       
    POST          /coordinates/                                      
    DELETE        /coordinates/:id
    
    GET           /closeNodes/                                        
    POST          /closeNodes/                                        
    POST          /closeNodes/list                                    
    DELETE        /closeNodes/:id                                     


### How to use to monitor a vivaldi network

1. You can either use the default network or create a new network.

    To register a network :

    REQUEST type: `POST` <br> 
    adress: `networks` <br> 
    header: `content-type: application/json`<br>  
    payload:
```Json
    {
        "networkName": "<yourNetworkName>"
    }
```

    RESPONSE type : `JSON`<br> 
    payload :
```Json
    {
        "id": 1,
        "networkName": "default"
    }
```



2. Each node needs to be registered and it should be done only ONCE.<br>

    To register a node :

    REQUEST type: `POST`<br> 
    adress: `/nodes/`<br> 
    header: `content-type: application/json`<br> 
    payload:
```Json
    {
        "nodeName": "<yourNodeName>",
        "networkId": <yourNetworkId>
    }
```

    RESPONSE type : `JSON`<br> 
    payload :
```Json
    {
        "id": 1,
        "nodeName": "Nantes",
        "networkId": 1
    }
```



3. Then on each vivaldi initialization time you need to send an initialization request to the server. The aim is to be able to get consistent data from a run.<br>

    To register an initialization time :

    REQUEST type : `POST`<br> 
    adress: `/initTimes/`<br> 
    header: `content-type: application/json`<br> 
    payload:
```Json
    {
        "nodeId": <currrentNodeId>
    }
```


4. Finaly, to save a calculated coordinate :

    REQUEST type : `POST`<br> 
    adress: `/coordinates/`<br> 
    header: `content-type: application/json` <br> 
    payload:
```Json
    {
        "nodeId": <yourNodeId>,
        "x":<x>,
        "y":<y>
    }
```

5. You can also save the close node generated list:

    REQUEST type : `POST`<br> 
    adress: `/closeNodes/list`<br> 
    header: `content-type: application/json` <br> 
    payload:
```Json
    [
        {
            "localNodeId": <yourLocalNodeId1>,
            "distantNodeId": <yourDistantNodeId1>,
            "distance": <distance>
        },
        {
            "localNodeId": <yourLocalNodeId2>,
            "distantNodeId": <yourDistantNodeId2>,
            "distance": <distance>
        }
    ]
```

## Configuration and dependencies

By default this play app uses :<br>  
- Play 2.2.1 - Scala 
- Postgres v9.3
- Foundation 5
- JQuery
- Google Charts

Play database configuration can be found in 'conf/application.conf'
The database initialisation script can be found here 'conf/evolutions/default/1.sql'


## License

Copyright (c) 2013 Nicolas JOSEPH

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
