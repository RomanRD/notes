#!/bin/bash

mongosh --host 172.17.0.1:27017 --eval 'rs.initiate({_id : "rs0", members: [{ _id : 0, host : "172.17.0.1:27017" }]})'